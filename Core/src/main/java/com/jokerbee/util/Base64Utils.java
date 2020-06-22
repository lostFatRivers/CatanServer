package com.jokerbee.util;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Base64Utils {
    private static final Logger logger = LoggerFactory.getLogger("Base64");

    /**
     * 编码表
     */
    public static char[] BASE64_DIGITS = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };

    /**
     * Base64加密;
     */
    public static String encode(byte[] bytes) {
        return encode(bytes, 0, bytes.length);
    }

    /**
     * Base64加密;
     */
    public static String encode(byte[] bytes, int offset, int length) {
        int totalBits = length * 8;
        int curPos = offset;
        StringBuilder base64 = new StringBuilder();
        while (curPos < totalBits) {
            int bytePos = curPos / 8;
            switch (curPos % 8) {
                case 0 -> base64.append(BASE64_DIGITS[(bytes[bytePos] & 0xfc) >> 2]);
                case 2 -> base64.append(BASE64_DIGITS[(bytes[bytePos] & 0x3f)]);
                case 4 -> {
                    if (bytePos == bytes.length - 1) {
                        base64.append(BASE64_DIGITS[((bytes[bytePos] & 0x0f) << 2) & 0x3f]);
                    } else {
                        int pos = (((bytes[bytePos] & 0x0f) << 2) | ((bytes[bytePos + 1] & 0xc0) >> 6)) & 0x3f;
                        base64.append(BASE64_DIGITS[pos]);
                    }
                }
                case 6 -> {
                    if (bytePos == bytes.length - 1) {
                        base64.append(BASE64_DIGITS[((bytes[bytePos] & 0x03) << 4) & 0x3f]);
                    } else {
                        int pos = (((bytes[bytePos] & 0x03) << 4) | ((bytes[bytePos + 1] & 0xf0) >> 4)) & 0x3f;
                        base64.append(BASE64_DIGITS[pos]);
                    }
                }
                default -> {}
            }
            curPos += 6;
        }

        if (totalBits % 6 == 2) {
            base64.append("==");
        } else if (totalBits % 6 == 4) {
            base64.append("=");
        }
        return base64.toString();
    }

    /**
     * Base64解密;
     */
    public static byte[] decode(String info) {
        try {
            return Base64.decodeBase64(info);
        } catch (Exception e) {
            logger.error("error", e);
        }
        return null;
    }
}
