package io.github.jeffdshen.project6857.core.net;

import java.security.SecureRandom;

/**
 * Created by jdshen on 5/7/15.
 */
public class Commitment {
    public static final int NONCE_BYTES_LENGTH = 16;

    // Our nonce is in hex
    public static final int NONCE_LENGTH = NONCE_BYTES_LENGTH * 2;
    public static final int ID_LENGTH = 1;

    private String nonce;
    private String data;
    private String hash;
    private String id;

    private Commitment() {}

    /**
     * Updates the commitment with the secret (nonce + id + data) and verifies the hash.
     * @param secret
     * @return true if the hash matches
     */
    public boolean update(String secret) {
        if (secret.length() < NONCE_LENGTH + ID_LENGTH) {
            return false;
        }

        String data = secret.substring(NONCE_LENGTH + ID_LENGTH);
        String id = secret.substring(NONCE_LENGTH, NONCE_LENGTH + ID_LENGTH);
        String nonce = secret.substring(0, NONCE_LENGTH);
        if (hash.equals(Sha256.hash(secret))) {
            this.id = id;
            this.data = data;
            this.nonce = nonce;
            return true;
        }

        return false;
    }

    public String getId() {
        return id;
    }

    public String getSecret() {
        return nonce + id + data;
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

    public static CommitmentProvider getCommitmentProvider(final String identifier) {
        if (identifier.length() != ID_LENGTH) {
            throw new IllegalArgumentException();
        }
        return new CommitmentProvider() {
            @Override
            public Commitment makeCommitment(String data) {
                SecureRandom random = new SecureRandom();
                Commitment c = new Commitment();
                c.data = data;
                byte[] bytes = new byte[NONCE_BYTES_LENGTH];
                random.nextBytes(bytes);
                c.nonce = EncodingProtocol.encodeBytes(bytes);
                c.id = identifier;
                c.hash = Sha256.hash(c.nonce + c.getId() + c.data);
                return c;
            }

            @Override
            public Commitment getCommitment(String hash) {
                Commitment c = new Commitment();
                c.hash = hash;
                return c;
            }
        };
    }
}
