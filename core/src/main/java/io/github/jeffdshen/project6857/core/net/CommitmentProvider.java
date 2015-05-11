package io.github.jeffdshen.project6857.core.net;

/**
 * Created by jdshen on 5/11/15.
 */
public interface CommitmentProvider {
    public Commitment makeCommitment(String data);
    public Commitment getCommitment(String hash);
}
