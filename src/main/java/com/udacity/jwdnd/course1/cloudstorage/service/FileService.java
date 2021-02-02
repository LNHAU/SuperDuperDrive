package com.udacity.jwdnd.course1.cloudstorage.service;

import com.udacity.jwdnd.course1.cloudstorage.mapper.FileMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.File;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class FileService {
    private final FileMapper fileMapper;

    public FileService(final FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }

    public File getFile(Integer fileId) {
        return fileMapper.getFile(fileId);
    }

    public List<File> getFilesFromUser(Integer userId) {
        return fileMapper.getFilesFromUser(userId);
    }

    public boolean isFilenameAvailable(Integer userId, String fileName) {
        return fileMapper.getFileFromUserWithFileName(userId, fileName) == null;
    }

    public int uploadFile(MultipartFile multipartFile, Integer userId) throws IOException {
        String fileName = multipartFile.getOriginalFilename();
        if (!isFilenameAvailable(userId, fileName)) {
            return 0;
        }

        String contentType = multipartFile.getContentType();
        String fileSize = String.valueOf(multipartFile.getSize());

        InputStream fis = multipartFile.getInputStream();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = fis.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        byte[] fileData = buffer.toByteArray();
        buffer.close();
        fis.close();

        File newFile = new File(null, fileName, contentType, fileSize, userId, fileData);
        return fileMapper.insertFile(newFile);
    }

    public int deleteFile(Integer fileId) {
        return fileMapper.deleteFile(fileId);
    }
}
