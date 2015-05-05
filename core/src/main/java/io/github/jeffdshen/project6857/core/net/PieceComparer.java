package io.github.jeffdshen.project6857.core.net;

import io.github.jeffdshen.project6857.core.board.Compare;
import io.github.jeffdshen.project6857.core.board.PieceType;
import io.github.jeffdshen.project6857.core.board.Rank;

import java.io.IOException;

/**
 * Created by jdshen on 4/29/15.
 */
public interface PieceComparer {
    public Compare compare(PieceType type, Rank rank) throws IOException;
    public boolean isAlice();
    public boolean isBob();
}
