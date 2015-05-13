package io.github.jeffdshen.project6857.core.board;

/**
 * Created by jdshen on 5/10/15.
 */
public class VerificationResult {
    private final String message;
    private final Exception e;

    public VerificationResult(String message, Exception e) {
        this.message = message;
        this.e = e;
    }

    public boolean isVerified() {
        return e == null;
    }

    public Exception getException() {
        return e;
    }

    public String getMessage() {
        return message;
    }
}
