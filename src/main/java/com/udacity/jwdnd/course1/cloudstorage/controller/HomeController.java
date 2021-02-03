package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.mapper.CredentialForm;
import com.udacity.jwdnd.course1.cloudstorage.mapper.FileForm;
import com.udacity.jwdnd.course1.cloudstorage.mapper.NoteForm;
import com.udacity.jwdnd.course1.cloudstorage.model.File;
import com.udacity.jwdnd.course1.cloudstorage.service.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class HomeController {
    private final FileService fileService;
    private final NoteService noteService;
    //private final EncryptionService encryptionService;
    private final CredentialService credentialService;
    private final UserService userService;

    public HomeController(final FileService fileService, final NoteService noteService//, final EncryptionService encryptionService
            , final CredentialService credentialService, final UserService userService) {
        this.fileService = fileService;
        this.noteService = noteService;
        //this.encryptionService = encryptionService;
        this.credentialService = credentialService;
        this.userService = userService;
    }

    private Integer getAuthenticationPrincipalId(@CurrentSecurityContext(expression="authentication.name") String username) {
        return userService.getUserIdWithUserName(username);
    }

    @GetMapping("/home")
    public String getHomePage(FileForm fileForm, NoteForm noteForm, CredentialForm credentialForm, @CurrentSecurityContext(expression="authentication.name") String username, Model model, WebRequest request) {
        Integer userId = getAuthenticationPrincipalId(username);
        request.setAttribute("authenticationPrincipalId", userId, WebRequest.SCOPE_SESSION);
        System.out.println("Home - authenticationPrincipalId Session attribute = " + request.getAttribute("authenticationPrincipalId", WebRequest.SCOPE_SESSION));
        model.addAttribute("files", this.fileService.getFilesFromUser(userId));
        model.addAttribute("notes", this.noteService.getNotesFromUser(userId));
        model.addAttribute("credentials", this.credentialService.getCredentialsFromUser(userId));
        model.addAttribute("credentialService", this.credentialService);
        //model.addAttribute("encryptionService", encryptionService);
        return "home";
    }

    @GetMapping("/view-file/fileId={fileId}")
    public ResponseEntity<Resource> viewFile(@PathVariable Integer fileId, Model model, WebRequest request) {
        System.out.println("View file - authenticationPrincipalId Session attribute as userId = " + request.getAttribute("authenticationPrincipalId", WebRequest.SCOPE_SESSION));
        Integer userId = (Integer) request.getAttribute("authenticationPrincipalId", WebRequest.SCOPE_SESSION);
        if (fileId == null || fileId <= 0 || fileService.getFile(fileId) == null) {
            model.addAttribute("result", "error");
            model.addAttribute("errorMessage", "File identifier " + fileId + " is not allowed!");
            return null;
        }
        else {
            File file = fileService.getFile(fileId);
            Integer ownerId = file.getUserId();
            if (ownerId != userId) {
                System.out.println("ownerId = " + ownerId);
                model.addAttribute("result", "error");
                model.addAttribute("errorMessage", "File owner is not you!");
                return null;
            }
            try{
                System.out.println("contentType = " + file.getContentType());
                System.out.println("filename = " + file.getFileName());
                System.out.println("contentLength = " + Long.valueOf(file.getFileSize()));
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getFileName())
                        .contentType(MediaType.parseMediaType(file.getContentType()))
                        .contentLength(Long.valueOf(file.getFileSize()))
                        .body(new ByteArrayResource(file.getFileData()));
            } catch (Exception e){
                return ResponseEntity.badRequest().build();
            }
        }
    }

    @GetMapping(value = "/delete-file/fileId={fileId}")
    public String deleteFile(@PathVariable Integer fileId, Model model, WebRequest request) {
        if (fileId == null || fileId <= 0 || fileService.getFile(fileId) == null) {
            model.addAttribute("result", "error");
            model.addAttribute("errorMessage", "File identifier " + fileId + " is not allowed!");
        }
        else {
            System.out.println("Delete file - authenticationPrincipalId Session attribute as userId = " + request.getAttribute("authenticationPrincipalId", WebRequest.SCOPE_SESSION));
            Integer userId = (Integer) request.getAttribute("authenticationPrincipalId", WebRequest.SCOPE_SESSION);
            Integer ownerId = fileService.getFile(fileId).getUserId();
            if (ownerId != userId) {
                model.addAttribute("result", "error");
                model.addAttribute("errorMessage", "File owner is not you!");
            }
            else {
                int nbDeleted = fileService.deleteFile(fileId);
                if (nbDeleted == 1) {
                    model.addAttribute("result", "success");
                    model.addAttribute("successMessage", "File was successfully deleted!");
                }
                else {
                    model.addAttribute("result", "notSaved");
                    model.addAttribute("notSavedMessage", "File was not deleted!");
                }
            }
        }
        return "result";
    }

    @PostMapping("/upload-file")
    public String uploadFile(FileForm newFile, Model model, WebRequest request) throws IOException {
        if (newFile == null || newFile.getFile() == null || newFile.getFile().isEmpty()) {
            model.addAttribute("result", "error");
            model.addAttribute("errorMessage", "None file was selected!");
        }
        else if (newFile.getFile().getContentType() == null || newFile.getFile().getOriginalFilename() == null) {
            model.addAttribute("result", "error");
            model.addAttribute("errorMessage", "Invalid file was selected!");
        }
        else if (newFile.getFile().getSize() > 10485760) {
            model.addAttribute("result", "error");
            model.addAttribute("errorMessage", "File size exceeds the maximum upload size of 10485760 (10 Mb). Please try with a smaller file.");
        }
        else {
            System.out.println("Upload file - authenticationPrincipalId Session attribute as userId = " + request.getAttribute("authenticationPrincipalId", WebRequest.SCOPE_SESSION));
            Integer userId = (Integer) request.getAttribute("authenticationPrincipalId", WebRequest.SCOPE_SESSION);
            MultipartFile multipartFile = newFile.getFile();
            String fileName = multipartFile.getOriginalFilename();
            System.out.println("fileName = \"" + fileName + "\" (type : " + multipartFile.getContentType() + ") of " + multipartFile.getSize() + " bytes.");
            if (fileService.isFilenameAvailable(userId, fileName)) {
                int nbUploaded = fileService.uploadFile(multipartFile, userId);
                if (nbUploaded == 1) {
                    model.addAttribute("result", "success");
                    model.addAttribute("successMessage", "File was successfully uploaded!");
                }
                else {
                    model.addAttribute("result", "notSaved");
                    model.addAttribute("notSavedMessage", "File was not uploaded!");
                }
            }
            else {
                model.addAttribute("result", "error");
                model.addAttribute("errorMessage", "Duplicate file names is not allowed!");
            }
        }
        return "result";
    }

    @PostMapping("/edit-note")
    public String editNote(NoteForm newNote, Model model, WebRequest request) {
        if (newNote == null || newNote.getNoteTitle() == null || newNote.getNoteDescription() == null) {
            model.addAttribute("result", "error");
            model.addAttribute("errorMessage", "Invalid note was input!");
        }
        else {
            System.out.println("Edit note - authenticationPrincipalId Session attribute as userId = " + request.getAttribute("authenticationPrincipalId", WebRequest.SCOPE_SESSION));
            Integer userId = (Integer) request.getAttribute("authenticationPrincipalId", WebRequest.SCOPE_SESSION);
            String noteTitle = newNote.getNoteTitle();
            Integer noteId = newNote.getNoteId();
            System.out.println("noteTitle = \"" + noteTitle + "\" (id : " + noteId + ")");
            if (noteService.isNoteTitleAvailable(noteId, noteTitle, userId)) {
                if (noteId == null) {
                    int nbCreated = noteService.addANewNote(noteTitle, newNote.getNoteDescription(), userId);
                    if (nbCreated == 1) {
                        model.addAttribute("result", "success");
                        model.addAttribute("successMessage", "Note was successfully added!");
                    }
                    else {
                        model.addAttribute("result", "notSaved");
                        model.addAttribute("notSavedMessage", "Note was not added!");
                    }
                }
                else {
                    if (noteId <= 0 || noteService.getNote(noteId) == null) {
                        model.addAttribute("result", "error");
                        model.addAttribute("errorMessage", "Note identifier " + noteId + " is not allowed!");
                    }
                    else {
                        Integer ownerId = noteService.getNote(noteId).getUserId();
                        if (ownerId != userId) {
                            model.addAttribute("result", "error");
                            model.addAttribute("errorMessage", "Note owner is not you!");
                        } else {
                            int nbUpdated = noteService.editNote(noteId, noteTitle, newNote.getNoteDescription());
                            if (nbUpdated == 1) {
                                model.addAttribute("result", "success");
                                model.addAttribute("successMessage", "Note was successfully updated!");
                            } else {
                                model.addAttribute("result", "notSaved");
                                model.addAttribute("notSavedMessage", "Note was not updated!");
                            }
                        }
                    }
                }
            }
            else {
                model.addAttribute("result", "error");
                model.addAttribute("errorMessage", "Duplicate note titles is not allowed!");
            }
        }
        return "result";
    }

    @GetMapping(value = "/delete-note/noteId={noteId}")
    public String deleteNote(@PathVariable Integer noteId, Model model, WebRequest request) {
        if (noteId == null || noteId <= 0 || noteService.getNote(noteId) == null) {
            model.addAttribute("result", "error");
            model.addAttribute("errorMessage", "Note identifier " + noteId + " is not allowed!");
        }
        else {
            System.out.println("Delete note - authenticationPrincipalId Session attribute as userId = " + request.getAttribute("authenticationPrincipalId", WebRequest.SCOPE_SESSION));
            Integer userId = (Integer) request.getAttribute("authenticationPrincipalId", WebRequest.SCOPE_SESSION);
            Integer ownerId = noteService.getNote(noteId).getUserId();
            if (ownerId != userId) {
                model.addAttribute("result", "error");
                model.addAttribute("errorMessage", "Note owner is not you!");
            }
            else {
                int nbDeleted = noteService.deleteNote(noteId);
                if (nbDeleted == 1) {
                    model.addAttribute("result", "success");
                    model.addAttribute("successMessage", "Note was successfully deleted!");
                }
                else {
                    model.addAttribute("result", "notSaved");
                    model.addAttribute("notSavedMessage", "Note was not deleted!");
                }
            }
        }
        return "result";
    }

    @PostMapping("/edit-credential")
    public String editCredential(CredentialForm newCredential, Model model, WebRequest request) {
        if (newCredential == null || newCredential.getUrl() == null || newCredential.getUsername() == null
                || newCredential.getPassword() == null) {
            model.addAttribute("result", "error");
            model.addAttribute("errorMessage", "Invalid credential was input!");
        }
        else {
            System.out.println("Edit credential - authenticationPrincipalId Session attribute as userId = " + request.getAttribute("authenticationPrincipalId", WebRequest.SCOPE_SESSION));
            Integer userId = (Integer) request.getAttribute("authenticationPrincipalId", WebRequest.SCOPE_SESSION);
            String url = newCredential.getUrl();
            Integer credentialId = newCredential.getCredentialId();
            System.out.println("credentialUrl = \"" + url + "\" (id : " + credentialId + ")");
            if (credentialService.isCredentialUrlAvailable(credentialId, url, userId)) {
                if (credentialId == null) {
                    int nbCreated = credentialService.addANewCredential(url, newCredential.getUsername(), newCredential.getPassword(), userId);
                    if (nbCreated == 1) {
                        model.addAttribute("result", "success");
                        model.addAttribute("successMessage", "Credential was successfully added!");
                    }
                    else {
                        model.addAttribute("result", "notSaved");
                        model.addAttribute("notSavedMessage", "Credential was not added!");
                    }
                }
                else {
                    if (credentialId <= 0 || credentialService.getCredential(credentialId) == null) {
                        model.addAttribute("result", "error");
                        model.addAttribute("errorMessage", "Credential identifier " + credentialId + " is not allowed!");
                    }
                    else {
                        Integer ownerId = credentialService.getCredential(credentialId).getUserId();
                        if (ownerId != userId) {
                            model.addAttribute("result", "error");
                            model.addAttribute("errorMessage", "Credential owner is not you!");
                        } else {
                            int nbUpdated = credentialService.editCredential(credentialId, url, newCredential.getUsername(), newCredential.getPassword());
                            if (nbUpdated == 1) {
                                model.addAttribute("result", "success");
                                model.addAttribute("successMessage", "Credential was successfully updated!");
                            } else {
                                model.addAttribute("result", "notSaved");
                                model.addAttribute("notSavedMessage", "Credential was not updated!");
                            }
                        }
                    }
                }
            }
            else {
                model.addAttribute("result", "error");
                model.addAttribute("errorMessage", "Duplicate credential urls is not allowed!");
            }
        }
        return "result";
    }

    @GetMapping(value = "/delete-credential/credentialId={credentialId}")
    public String deleteCredential(@PathVariable Integer credentialId, Model model, WebRequest request) {
        if (credentialId == null || credentialId <= 0 || credentialService.getCredential(credentialId) == null) {
            model.addAttribute("result", "error");
            model.addAttribute("errorMessage", "Credential identifier " + credentialId + " is not allowed!");
        }
        else {
            System.out.println("Delete credential - authenticationPrincipalId Session attribute as userId = " + request.getAttribute("authenticationPrincipalId", WebRequest.SCOPE_SESSION));
            Integer userId = (Integer) request.getAttribute("authenticationPrincipalId", WebRequest.SCOPE_SESSION);
            Integer ownerId = credentialService.getCredential(credentialId).getUserId();
            if (ownerId != userId) {
                model.addAttribute("result", "error");
                model.addAttribute("errorMessage", "Credential owner is not you!");
            }
            else {
                int nbDeleted = credentialService.deleteCredential(credentialId);
                if (nbDeleted == 1) {
                    model.addAttribute("result", "success");
                    model.addAttribute("successMessage", "Credential was successfully deleted!");
                }
                else {
                    model.addAttribute("result", "notSaved");
                    model.addAttribute("notSavedMessage", "Credential was not deleted!");
                }
            }
        }
        return "result";
    }
}
