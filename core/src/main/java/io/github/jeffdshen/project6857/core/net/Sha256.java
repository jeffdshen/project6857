package io.github.jeffdshen.project6857.core.net;

import java.security.MessageDigest;

/**
 * Created by jdshen on 5/7/15.
 */
public class Sha256 {
    /**
     * Returns the hash in hex
     */
    public static String hash(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes("UTF-8"));
            return Encoding.bytesToHex(hash);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
