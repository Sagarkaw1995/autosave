package com.cctns.autosave.producer.service.utility;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class EncryptionUtil {

    @Value("${encryption.enc-algorithm}")
    private String ENC_ALGORITHM;

    @Value("${encryption.secret-key-string}")
    private String SECRET_KEY_STRING;

    @Value("${encryption.base64.regex}")
    private String regex;

    private SecretKey SECRET_KEY;

    @PostConstruct
    public void initialize() {
        // Ensure properties are set properly before creating SECRET_KEY
        if (ENC_ALGORITHM == null || SECRET_KEY_STRING == null) {
            throw new IllegalStateException("ENC_ALGORITHM or SECRET_KEY_STRING is not properly configured");
        }

        SECRET_KEY = new SecretKeySpec(
                SECRET_KEY_STRING.length() > 16 ?
                        SECRET_KEY_STRING.substring(0, 16).getBytes() :
                        (SECRET_KEY_STRING + "                ").substring(0, 16).getBytes(),
                ENC_ALGORITHM
        );
    }

    public String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance(ENC_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, SECRET_KEY);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting data", e);
        }
    }

    public String decrypt(String encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance(ENC_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, SECRET_KEY);
            byte[] decodedData = Base64.getDecoder().decode(encryptedData);
            byte[] decryptedData = cipher.doFinal(decodedData);
            return new String(decryptedData);
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting data", e);
        }
    }

    public boolean isBase64Encoded(String data){
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(data);
        return matcher.matches();
    }
}
