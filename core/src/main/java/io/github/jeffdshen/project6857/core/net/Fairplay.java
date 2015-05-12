package io.github.jeffdshen.project6857.core.net;

import com.badlogic.gdx.Gdx;
import io.github.jeffdshen.project6857.core.board.Compare;
import io.github.jeffdshen.project6857.core.board.PieceType;
import io.github.jeffdshen.project6857.core.board.Rank;

import java.io.*;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Fairplay implements FairplayComparer {
    public static Map<PieceType, Integer> TYPE_TO_INT = typeToInt();
    public static Map<Rank, Integer> RANK_TO_INT = rankToInt();
    public static Map<Integer, Compare> INT_TO_COMPARE = intToCompare();

    private final String ip;
    private final boolean alice;
    private final File dir;
    private final String pathExec;
    private final String pathSFDL;
    private final String namePrefix;
    private final String outputPrefix;
    private final String inputPrefix;
    private BufferedReader in;
    private PrintWriter out;

    private static Map<PieceType, Integer> typeToInt() {
        Map<PieceType, Integer> map = new HashMap<>();
        map.put(PieceType.ROCK, 0);
        map.put(PieceType.PAPER, 1);
        map.put(PieceType.SCISSORS, 2);
        return Collections.unmodifiableMap(map);
    }

    private static Map<Rank, Integer> rankToInt() {
        Map<Rank, Integer> map = new HashMap<>();
        map.put(Rank.BOMB, 0);
        map.put(Rank.ONE, 1);
        map.put(Rank.TWO, 2);
        map.put(Rank.THREE, 3);
        map.put(Rank.FOUR, 4);
        map.put(Rank.FIVE, 5);
        return Collections.unmodifiableMap(map);
    }

    private static Map<Integer, Compare> intToCompare() {
        Map<Integer, Compare> map = new HashMap<>();
        map.put(0, Compare.WIN);
        map.put(1, Compare.LOSS);
        map.put(2, Compare.TIE);
        return Collections.unmodifiableMap(map);
    }

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

    public void setReader(BufferedReader in) {
        this.in = in;
    }

    public void setWriter(PrintWriter out) {
        this.out = out;
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
        return Gdx.files.internal("assets/fairplay/run/run_alice").file().getParentFile();
    }

    private static String getPathExec(boolean alice) {
        return alice ? "./run_alice" : "./run_bob";
    }

    private static String getPathSFDL() {
        return "progs/compare.txt";
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
        map.put(inputPrefix + "piece", TYPE_TO_INT.get(type) + "");
        map.put(inputPrefix + "rank", RANK_TO_INT.get(rank) + "");
        return INT_TO_COMPARE.get(compare(map));
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
        System.out.println(pathExec);
        System.out.println(pathSFDL);
        System.out.println(dir.getAbsolutePath());
        System.out.println(builder);
//        System.exit(1);
        builder.directory(dir);
        builder.redirectErrorStream(true);

        if (alice) {
            in.readLine();
        }

        Process process = builder.start();

        if (!alice) {
            out.println("started");
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
             BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
             PrintWriter out = new PrintWriter(bw, true)
        ) {
            StringBuilder buffer = new StringBuilder();
            for (int c = in.read(); c != -1; c = in.read()) {
                char x = (char) c;
                System.out.print(x);
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
                    System.out.println(input.get(buffer.toString()));
                    out.println(input.get(buffer.toString()));
                    buffer.delete(0, buffer.length());
                }
            }
        }

        throw new IOException("Could not find output");
    }
}
