package com.example.nibsstransfer.util;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Base64;

@Slf4j
public class RSAEncrypt {

    public static String encrypt(String plainText) {
        try {
            KeyPair keyPair = RsaKeyGen.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        }catch (Exception e){
            log.error("Exception caught while encrypting ", e);
            return " ";
        }

    }

    public static void main(String[] args) throws Exception {
        KeyPair keyPair = RsaKeyGen.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();

        String json = "{\"name\":\"John Doe\", \"email\":\"john.doe@example.com\"}";
        String encryptedJson = encrypt(json);
        System.out.println("Encrypted JSON: " + encryptedJson);
    }
}

