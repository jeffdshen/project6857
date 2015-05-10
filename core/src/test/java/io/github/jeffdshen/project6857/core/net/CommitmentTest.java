package io.github.jeffdshen.project6857.core.net;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class CommitmentTest {

    @Test
    public void testCommitment() throws Exception {
        String data = "hello world";
        Commitment send = Commitment.makeCommitment(data);
        Commitment recv = Commitment.getCommitment(send.getHash());
        assertTrue(recv.update(send.getSecret()));
        assertEquals(data, send.getData());
        assertEquals(data, recv.getData());
        assertEquals(send.getNonce(), recv.getNonce());
        assertEquals(send.getHash(), recv.getHash());
    }
}