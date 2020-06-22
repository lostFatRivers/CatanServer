package com.jokerbee.util;

import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Security;

public class AESUtil {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private static final String KEY_ALGORITHM = "AES";
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS7Padding";//默认的加密算法

    private static final Logger logger = LoggerFactory.getLogger("AES");

    /**
     * 加密;
     */
    public static String encrypt(String content, String key){
        if(key == null) {
            return null;
        }
        if(key.length() != 16) {
            return null;
        }
        try{
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            byte[] raw = key.getBytes();
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(raw, "AES"));
            byte[] encrypted = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
            return Base64Utils.encode(encrypted);
        } catch (Exception e) {
            logger.error("encrypt error",e);
        }
        return null;
    }

    /**
     * 解密
     */
    public static String decrypt(String content, String key){
        try {
            if (content == null) {
                return null;
            }
            byte[] data = Base64Utils.decode(content);
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), KEY_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            return new String(cipher.doFinal(data), StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.error("decrypt error",e);
        }
        return null;
    }

}
