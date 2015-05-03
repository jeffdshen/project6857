package io.github.jeffdshen.project6857.core.board;

/**
 * Created by chenp on 4/18/2015.
 */
public class Piece {
    private final PieceType type;
    private final Rank rank;

    public Piece(PieceType type, Rank rank){
        this.type = type;
        this.rank = rank;
    }

    public Rank getRank() {
        return rank;
    }

    public PieceType getType() {
        return type;
    }

    public boolean canMove() {
        return this.rank != Rank.BOMB && this.rank != Rank.FLAG;
    }

    public boolean equals(Object other){
        if (other == null || other.getClass() != this.getClass()){
            return false;
        }

        Piece that = (Piece)other;
        return (this.type == that.type && this.rank == that.rank);
    }

    public int hashCode(){
        int hash = 7;
        hash = 71 * hash + this.type.hashCode();
        hash = 71 * hash + this.rank.hashCode();
        return hash;
    }
}
