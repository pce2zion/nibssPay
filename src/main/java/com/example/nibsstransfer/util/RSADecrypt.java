package com.example.nibsstransfer.util;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.Base64;

@Slf4j
public class RSADecrypt {

    public static String decrypt(String cipherText)  {
        try {
            KeyPair keyPair = RsaKeyGen.generateKeyPair();
            PrivateKey privateKey = keyPair.getPrivate();
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(cipherText));
            return new String(decryptedBytes);
        }catch (Exception e){
            log.error("Exception caught while trying to decrypt",e);
            return " ";
        }
    }


}
