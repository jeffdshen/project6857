package io.github.jeffdshen.project6857.core.board;

/**
 * Created by chenp on 4/18/2015.
 */
public class Piece {
    private final PieceType type;
    private final Rank rank;
    private final boolean isMine;

    public Piece(PieceType type, Rank rank, boolean isMine){
        this.type = type;
        this.rank = rank;
        this.isMine = isMine;
    }

    public Piece flipSides() {
        return new Piece(type, rank, !isMine);
    }

    public Rank getRank() {
        return rank;
    }

    public PieceType getType() {
        return type;
    }

    public boolean getIsMine() {
        return isMine;
    }

    public boolean canMove() {
        return this.rank != Rank.BOMB && this.rank != Rank.FLAG;
    }

    public boolean equals(Object other){
        if (other == null || other.getClass() != this.getClass()){
            return false;
        }

        Piece that = (Piece)other;
        return (this.type == that.type && this.rank == that.rank && this.isMine == that.isMine);
    }

    @Override
    public String toString() {
        return type + "," + rank + "," + isMine;
    }

    @Override
    public int hashCode(){
        int hash = 7;
        hash = 71 * hash + this.type.hashCode();
        hash = 71 * hash + this.rank.hashCode();
        hash = 71 * hash + (this.isMine ? 1 : 0);
        return hash;
    }
}
