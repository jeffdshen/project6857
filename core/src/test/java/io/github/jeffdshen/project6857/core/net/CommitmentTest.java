package io.github.jeffdshen.project6857.core.net;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class CommitmentTest {

    @Test
    public void testCommitment() throws Exception {
        String data = "hello world";
        String id = "S";
        CommitmentProvider provider = Commitment.getCommitmentProvider(id);
        Commitment send = provider.makeCommitment(data);
        Commitment recv = provider.getCommitment(send.getHash());
        assertTrue(recv.update(send.getSecret()));
        assertEquals(send.getData(), data);
        assertEquals(recv.getData(), data);
        assertEquals(send.getId(), id);
        assertEquals(recv.getId(), id);
        assertEquals(send.getNonce(), recv.getNonce());
        assertEquals(send.getHash(), recv.getHash());
    }
}