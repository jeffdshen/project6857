package io.github.jeffdshen.project6857.core.net;

import org.testng.annotations.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import static junit.framework.Assert.assertEquals;

public class FairplayTest {
    @Test
    public void testCompare() throws Exception {
        // test.txt compares only the ranks and returns 1 if greater, -1 if less, 0 if equal
        final Fairplay bob = new Fairplay(
            null,
            false,
            getClass().getResource("fairplay/run").toURI().getPath(),
            "./run_bob",
            "progs/test.txt"
        );
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bob.setWriter(new PrintWriter(outputStream));

        final HashMap<String, String> bobInputs = new HashMap<>();
        bobInputs.put("input.bob.piece", "0");
        bobInputs.put("input.bob.rank", "2");

        new Thread() {
            public void run() {
                try {
                    int result = bob.compare(bobInputs);
                    assertEquals(1, result);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        Thread.sleep(500);

        final Fairplay alice = new Fairplay(
            "127.0.0.1",
            true,
            getClass().getResource("fairplay/run").toURI().getPath(),
            "./run_alice",
            "progs/test.txt"
        );
        InputStream inputStream = new ByteArrayInputStream("started\n".getBytes(StandardCharsets.UTF_8));
        alice.setReader(new BufferedReader(new InputStreamReader(inputStream)));

        final HashMap<String, String> aliceInputs = new HashMap<>();
        aliceInputs.put("input.alice.piece", "0");
        aliceInputs.put("input.alice.rank", "1");
        int result = alice.compare(aliceInputs);
        // test.txt stores this as a 2 bit int, so -1 = 3
        assertEquals(3, result);
    }
}
