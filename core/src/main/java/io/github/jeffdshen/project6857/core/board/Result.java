package io.github.jeffdshen.project6857.core.board;

/**
 * Created by jdshen on 5/5/15.
 */
public class Result {
    private final Compare compare;
    private final Piece yourPiece;
    private final Piece theirPiece;

    /**
     * The result of a comparison. yourPiece/theirPiece are non-null if your or their piece died.
     */
    public Result(Compare compare, Piece yourPiece, Piece theirPiece) {
        this.compare = compare;
        this.yourPiece = yourPiece;
        this.theirPiece = theirPiece;
    }

    public Result opposite() {
        return new Result(compare.opposite(), theirPiece, yourPiece);
    }

    public Compare getCompare() {
        return compare;
    }

    public Piece getYourPiece() {
        return yourPiece;
    }

    public Piece getTheirPiece() {
        return yourPiece;
    }
}
