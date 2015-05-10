package io.github.jeffdshen.project6857.core.net;

import io.github.jeffdshen.project6857.core.board.Piece;
import io.github.jeffdshen.project6857.core.board.Result;

import java.io.IOException;

/**
 * Created by jdshen on 4/29/15.
 */
public interface PieceComparer {
    public Result compare(Piece piece) throws IOException;
}
