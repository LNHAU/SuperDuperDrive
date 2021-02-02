package com.udacity.jwdnd.course1.cloudstorage.service;

import com.udacity.jwdnd.course1.cloudstorage.mapper.CredentialMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

@Service
public class CredentialService {
    private final CredentialMapper credentialMapper;
    private final EncryptionService encryptionService;

    public CredentialService(final CredentialMapper credentialMapper, final EncryptionService encryptionService) {
        this.credentialMapper = credentialMapper;
        this.encryptionService = encryptionService;
    }

    public Credential getCredential(Integer credentialId) {
        Credential credentialInDB = credentialMapper.getCredential(credentialId);
        String decryptedPassword = encryptionService.decryptValue(credentialInDB.getPassword(), credentialInDB.getKey());
        Credential credentialToShow = new Credential(credentialInDB.getCredentialId(), credentialInDB.getUrl(),
                credentialInDB.getUsername(), credentialInDB.getKey(), decryptedPassword, credentialInDB.getUserId());
        return credentialToShow;
    }

    public List<Credential> getCredentialsFromUser(Integer userId) {
        return credentialMapper.getCredentialsFromUser(userId);
    }

    public boolean isCredentialUrlAvailable(Integer credentialId, String url, Integer userId) {
        Credential foundCredential = credentialMapper.getCredentialFromUserWithUrl(userId, url);
        return (foundCredential == null
                || (foundCredential != null && foundCredential.getCredentialId().equals(credentialId)));
    }

    public int addANewCredential(String url, String username, String password, Integer userId) {
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[16];
        random.nextBytes(key);
        String encodedKey = Base64.getEncoder().encodeToString(key);
        String encryptedPassword = encryptionService.encryptValue(password, encodedKey);
        return credentialMapper.insertCredential(new Credential(null, url, username, encodedKey, encryptedPassword, userId));
    }

    public int editCredential(Integer credentialId, String url, String username, String password) {
        String encodedKey = credentialMapper.getCredential(credentialId).getKey();
        String encryptedPassword = encryptionService.encryptValue(password, encodedKey);
        return credentialMapper.updateCredential(credentialId, url, username, encryptedPassword);
    }

    public int deleteCredential(Integer credentialId) {
        return credentialMapper.deleteCredential(credentialId);
    }
}
