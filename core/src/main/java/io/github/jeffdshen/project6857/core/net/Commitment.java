package io.github.jeffdshen.project6857.core.net;

import java.security.SecureRandom;

/**
 * Created by jdshen on 5/7/15.
 */
public class Commitment {
    public static final int NONCE_BYTES_LENGTH = 16;

    // Our nonce is in hex
    public static final int NONCE_LENGTH = NONCE_BYTES_LENGTH * 2;

    private String nonce;
    private String data;
    private String hash;

    private Commitment() {}

    /**
     * Updates the commitment with the secret (nonce + data) and verifies the hash.
     * @param secret
     * @return true if the hash matches
     */
    public boolean update(String secret) {
        if (secret.length() < NONCE_LENGTH) {
            return false;
        }

        String data = secret.substring(NONCE_LENGTH);
        String nonce = secret.substring(0, NONCE_LENGTH);
        if (hash.equals(Sha256.hash(secret))) {
            this.data = data;
            this.nonce = nonce;
            return true;
        }

        return false;
    }

    public String getSecret() {
        return nonce + data;
    }

    public String getData() {
        return data;
    }

    public String getNonce() {
        return nonce;
    }

    public String getHash() {
        return hash;
    }

    public static Commitment makeCommitment(String data) {
        SecureRandom random = new SecureRandom();
        Commitment c = new Commitment();
        c.data = data;
        byte[] bytes = new byte[NONCE_BYTES_LENGTH];
        random.nextBytes(bytes);
        c.nonce = EncodingProtocol.encodeBytes(bytes);
        c.hash = Sha256.hash(c.nonce + c.data);
        return c;
    }

    public static Commitment getCommitment(String hash) {
        Commitment c = new Commitment();
        c.hash = hash;
        return c;
    }
}
