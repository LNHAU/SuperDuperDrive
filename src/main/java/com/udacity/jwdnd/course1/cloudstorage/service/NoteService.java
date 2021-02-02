package com.udacity.jwdnd.course1.cloudstorage.service;

import com.udacity.jwdnd.course1.cloudstorage.mapper.NoteMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {
    private final NoteMapper noteMapper;

    public NoteService(final NoteMapper noteMapper) {
        this.noteMapper = noteMapper;
    }

    public Note getNote(Integer noteId) {
        return noteMapper.getNote(noteId);
    }

    public List<Note> getNotesFromUser(Integer userId) {
        return noteMapper.getNotesFromUser(userId);
    }

    public boolean isNoteTitleAvailable(Integer noteId, String noteTitle, Integer userId) {
        Note foundNote = noteMapper.getNoteFromUserWithNoteTitle(userId, noteTitle);
        return (foundNote == null
                || (foundNote != null && foundNote.getNoteId().equals(noteId)));
    }

    public int addANewNote(String noteTitle, String noteDescription, Integer userId) {
        return noteMapper.insertNote(new Note(null, noteTitle, noteDescription, userId));
    }

    public int editNote(Integer noteId, String noteTitle, String noteDescription) {
        return noteMapper.updateNote(noteId, noteTitle, noteDescription);
    }

    public int deleteNote(Integer noteId) {
        return noteMapper.deleteNote(noteId);
    }
}
