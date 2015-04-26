package io.github.jeffdshen.project6857.core.net;

import com.badlogic.gdx.Gdx;
import io.github.jeffdshen.project6857.core.board.Compare;
import io.github.jeffdshen.project6857.core.board.PieceType;
import io.github.jeffdshen.project6857.core.board.Rank;

import java.io.*;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class Fairplay {
    private final String ip;
    private final boolean alice;
    private final File dir;
    private final String pathExec;
    private final String pathSFDL;
    private final String namePrefix;
    private final String outputPrefix;
    private final String inputPrefix;

    public Fairplay(String ip, boolean alice, File dir, String pathExec, String pathSFDL) {
        this.ip = ip;
        this.alice = alice;
        this.dir = dir;
        this.pathExec = pathExec;
        this.pathSFDL = pathSFDL;
        namePrefix = alice ? "alice" : "bob";
        outputPrefix = "output." + namePrefix;
        inputPrefix = "input." + namePrefix + ".";
    }

    public Fairplay(String ip, boolean alice, String dirName, String pathExec, String pathSFDL) {
        this(ip, alice, new File(dirName), pathExec, pathSFDL);
    }

    public Fairplay() {
        this(null, false);
    }

    public Fairplay(String ip) {
        this(ip, true);
    }

    public Fairplay(String ip, boolean alice) {
        this(ip, alice, getDirectory(), getPathExec(alice), getPathSFDL());
    }

    private static File getDirectory() {
        return Gdx.files.internal("fairplay/run").file();
    }

    private static String getPathExec(boolean alice) {
        return alice ? "./run_alice" : "./run_bob";
    }

    private static String getPathSFDL() {
        return "progs/comparison.txt";
    }

    public boolean isAlice() {
        return alice;
    }

    public boolean isBob() {
        return !alice;
    }

    /**
     * Compare a piece against the other player's piece.
     * Bob should start this process first, then Alice.
     * @return the result of the fairplay comparison
     */
    public Compare compare(PieceType type, Rank rank) throws IOException {
        HashMap<String, String> map = new HashMap<>();
        map.put(inputPrefix + "type", type.ordinal() + "");
        map.put(inputPrefix + "rank", rank.ordinal() + "");
        return Compare.values()[compare(map)];
    }

    /**
     * Runs fairplay on the given input.
     * Bob should start this process first, then Alice.
     * @param input A map from string keys to values.
     * @return the raw result of the fairplay comparison, does not convert anything
     */
    public int compare(Map<String, String> input) throws IOException {
        SecureRandom random = new SecureRandom();
        String randomString = new BigInteger(130, random).toString(32);

        ProcessBuilder builder = alice ? new ProcessBuilder(pathExec, "-r", pathSFDL, randomString, ip)
            : new ProcessBuilder(pathExec, "-r", pathSFDL, randomString, "4");
        builder.directory(dir);
        builder.redirectErrorStream(true);
        Process process = builder.start();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
             BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
             PrintWriter out = new PrintWriter(bw, true)
        ) {
            StringBuilder buffer = new StringBuilder();
            for (int c = in.read(); c != -1; c = in.read()) {
                char x = (char) c;
                // if new line, check if it's output, then discard the line.
                if (x == '\n' || x == '\r') {
                    String s = buffer.toString();
                    if (s.contains(outputPrefix)) {
                        int i = s.lastIndexOf(outputPrefix);
                        return Integer.parseInt(s.substring(i + outputPrefix.length()));
                    }

                    buffer.delete(0, buffer.length());
                    continue;
                }

                // otherwise, check if it's input
                buffer.append(x);
                if (input.containsKey(buffer.toString())) {
                    out.println(input.get(buffer.toString()));
                    buffer.delete(0, buffer.length());
                }
            }
        }

        throw new IOException("Could not find output");
    }
}
