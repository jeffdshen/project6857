package io.github.jeffdshen.project6857.core.board;

import java.util.Objects;

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

    @Override
    public boolean equals(Object other) {
        if (other == null || other.getClass() != this.getClass()){
            return false;
        }

        Result that = (Result)other;
        return (this.compare == that.compare && Objects.equals(this.yourPiece, that.yourPiece)
            && Objects.equals(this.theirPiece, that.theirPiece));
    }

    public int hashCode(){
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(compare);
        hash = 71 * hash + Objects.hashCode(yourPiece);
        hash = 71 * hash + Objects.hashCode(theirPiece);
        return hash;
    }
}
