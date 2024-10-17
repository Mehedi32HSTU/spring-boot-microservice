package com.javabean.api_gateway.api_key;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

public class EncryptionUtil {
    private static final String ALGORITHM = "AES";
    private static final String CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding";

    // Your actual secret key and salt. Consider moving these to secure storage in production!
    private static final String SECRET_KEY = "secret-key@api-key";
    private static final String SALT = "api-key-salt";
    private static final int KEY_SIZE = 256;
    private static final int IV_SIZE = 16;

    // Generate SecretKey from a password, salt, and iteration count
    private static SecretKeySpec getKeyFromPassword(String password, String salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, KEY_SIZE);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), ALGORITHM);
    }

    public static String encrypt(String apiKey) throws Exception {
        SecretKeySpec keySpec = getKeyFromPassword(SECRET_KEY, SALT);

        // Generate random IV (Initialization Vector)
        byte[] iv = new byte[IV_SIZE];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivParams = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParams);
        byte[] encrypted = cipher.doFinal(apiKey.getBytes(StandardCharsets.UTF_8));

        // Combine IV and encrypted data
        byte[] encryptedIvAndData = new byte[IV_SIZE + encrypted.length];
        System.arraycopy(iv, 0, encryptedIvAndData, 0, IV_SIZE);
        System.arraycopy(encrypted, 0, encryptedIvAndData, IV_SIZE, encrypted.length);

        return Base64.getEncoder().encodeToString(encryptedIvAndData);
    }

    // Decrypt the API key
    public static String decrypt(String encryptedApiKey) throws Exception {
        SecretKeySpec keySpec = getKeyFromPassword(SECRET_KEY, SALT);

        byte[] encryptedIvAndData = Base64.getDecoder().decode(encryptedApiKey);

        // Ensure the length of encryptedIvAndData is valid
        if (encryptedIvAndData.length < IV_SIZE) {
            throw new IllegalArgumentException("Invalid encrypted API key data.");
        }

        // Extract IV (first 16 bytes)
        byte[] iv = new byte[IV_SIZE];
        System.arraycopy(encryptedIvAndData, 0, iv, 0, IV_SIZE);
        IvParameterSpec ivParams = new IvParameterSpec(iv);

        // Extract encrypted data (remaining bytes)
        byte[] encryptedData = new byte[encryptedIvAndData.length - IV_SIZE];
        System.arraycopy(encryptedIvAndData, IV_SIZE, encryptedData, 0, encryptedData.length);

        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParams);
        byte[] decrypted = cipher.doFinal(encryptedData);

        return new String(decrypted, StandardCharsets.UTF_8);
    }
}
