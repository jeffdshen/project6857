package io.github.jeffdshen.project6857.core.net;

import io.github.jeffdshen.project6857.core.board.Compare;
import io.github.jeffdshen.project6857.core.board.PieceType;
import io.github.jeffdshen.project6857.core.board.Rank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by jdshen on 5/9/15.
 */
public class FairplayAlternator implements FairplayComparer {
    private final Fairplay alice;
    private final Fairplay bob;
    private boolean isAlice;

    public FairplayAlternator(Fairplay alice, Fairplay bob, boolean isAlice) {
        this.alice = alice;
        this.bob = bob;
        this.isAlice = isAlice;
    }


    @Override
    public void setReader(BufferedReader in) {
        alice.setReader(in);
    }

    @Override
    public void setWriter(PrintWriter out) {
        bob.setWriter(out);
    }

    @Override
    public Compare compare(PieceType type, Rank rank) throws IOException {
        Compare result = isAlice ? alice.compare(type, rank) : bob.compare(type, rank);
        isAlice = !isAlice;
        return result;
    }
}
