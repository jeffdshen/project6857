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
public interface FairplayComparer {
    public void setReader(BufferedReader in);
    public void setWriter(PrintWriter out);
    public Compare compare(PieceType type, Rank rank) throws IOException;
}
