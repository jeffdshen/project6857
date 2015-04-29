package io.github.jeffdshen.project6857.core.board;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by chenp on 4/18/2015.
 */
public class Board {
    private Piece[][] board;
    private Move myMove;
    private Move theirMove;
    private List<Round> rounds;
    private int width;
    private int height;

    public Board(Piece[][] board){
        this.board = board;
        height = board.length;
        width = board[0].length;
        rounds = new ArrayList<Round>();
    }

    public synchronized Piece[][] getBoard(){
        Piece[][] result = new Piece[height][width];
        for (int i = 0; i < width; i ++) {
            for (int j = 0; j < height; j++) {
                result[j][i] = getPiece(i, j);
            }
        }
        return result;
    }

    private boolean inBoard(Location loc){
        return inBoard(loc.getX(), loc.getY());
    }

    private boolean inBoard(int x, int y){
        return (y >= 0 && x >= 0 && y < height && x < width);
    }

    public Location getRotatedLocation(Location loc){
        return getRotatedLocation(loc.getX(), loc.getY());
    }

    public Location getRotatedLocation(int x, int y){
        return new Location(width - x - 1, height - y -1);
    }

    private Piece getPiece(Location loc) {
        return getPiece(loc.getX(), loc.getY());
    }

    public synchronized Piece getPiece(int x, int y) {
        if (!inBoard(x, y) || this.board[y][x] == null) {
            return null;
        }
        return new Piece(this.board[y][x].getType(), this.board[y][x].getRank());
    }

    private void setPiece(Location loc, Piece piece){
        board[loc.getY()][loc.getX()] = piece;
    }

    public synchronized boolean startRound(){
        if (myMove == null || theirMove == null){
            return false;
        }
        Compare myStatus;
        Compare theirStatus;

        if (myMove.getEnd().equals(theirMove.getEnd())){
            // Checks if the two pieces are colliding on the same square
            Compare result = runFairPlay(myMove.getStart(), theirMove.getStart());
            if (result.equals(Compare.WIN)){
                myStatus = Compare.WIN;
                theirStatus = Compare.LOSS;
            } else if (result.equals(Compare.LOSS)){
                myStatus = Compare.LOSS;
                theirStatus = Compare.WIN;
            } else {
                myStatus = Compare.TIE;
                theirStatus = Compare.TIE;
            }
        } else {
            if (getPiece(myMove.getEnd()) == null || myMove.getEnd().equals(theirMove.getStart())){
                // Checks if moving onto an empty piece (piece that the other player just vacated is empty)
                myStatus = Compare.WIN;
            } else {
                Compare result = runFairPlay(myMove.getStart(), myMove.getEnd());
                if (result.equals(Compare.WIN)){
                    myStatus = Compare.WIN;
                } else if (result.equals(Compare.LOSS)){
                    myStatus = Compare.LOSS;
                } else {
                    myStatus = Compare.TIE;
                }
            }

            if (getPiece(theirMove.getEnd()) == null || theirMove.getEnd().equals(myMove.getStart())){
                // Checks if moving onto an empty piece (piece that the other player just vacated is empty)
                theirStatus = Compare.WIN;
            } else {
                Compare result = runFairPlay(theirMove.getEnd(), theirMove.getStart());
                if (result.equals(Compare.LOSS)){
                    theirStatus = Compare.WIN;
                } else if (result.equals(Compare.WIN)){
                    theirStatus = Compare.LOSS;
                } else {
                    theirStatus = Compare.TIE;
                }
            }
        }

        // Sets the end location based on the comparison results
        Piece myPiece = getPiece(myMove.getStart());
        Piece theirPiece = getPiece(theirMove.getStart());
        if (myStatus.equals(Compare.WIN)){
            setPiece(myMove.getEnd(), myPiece);
        } else if (myStatus.equals(Compare.TIE)){
            setPiece(myMove.getEnd(), null);
        }
        if (theirStatus.equals(Compare.WIN)){
            setPiece(theirMove.getEnd(), theirPiece);
        } else if (theirStatus.equals(Compare.TIE)){
            setPiece(theirMove.getEnd(), null);
        }

        // Removes the piece at the starting location if it hasn't already been replaced
        if (myMove.getStart() != null && getPiece(myMove.getStart()).equals(myPiece)){
            setPiece(myMove.getStart(), null);
        }
        if (theirMove.getStart() != null && getPiece(theirMove.getStart()).equals(theirPiece)){
            setPiece(theirMove.getStart(), null);
        }

        // Adds the round to a arraylist
        rounds.add(new Round(myMove, myStatus, theirMove, theirStatus));

        // Resets the moves
        myMove = null;
        theirMove = null;
        return true;
    }

    private Compare runFairPlay(Location loc1, Location loc2){
        return runFairPlay(loc1.getX(), loc1.getY(), loc2.getX(), loc2.getY());
    }

    private Compare runFairPlay(int myX, int myY, int theirX, int theirY){
        return Compare.TIE; //TODO do Fairplay here to determine win
    }

    private Move makeMove(Location loc, Direction direction){
        if (getPiece(loc) == null || !getPiece(loc).canMove()){
            return null;
        }
        Location end = new Location(loc.getX() + direction.getX(), loc.getY() + direction.getY());
        if (!inBoard(end)){
            return null;
        }
        return new Move(loc, end);
    }

    public synchronized boolean makeMyMove(Location loc, Direction direction){
        return makeMyMove(loc.getX(), loc.getY(), direction);
    }

    public synchronized boolean makeMyMove(int x, int y, Direction direction) {
        if (myMove != null) {
            return false;
        }
        Location loc = new Location(x, y);
        Move result = makeMove(loc, direction);
        if (result == null){
            return false;
        }
        if (getPiece(result.getStart()) == null || getPiece(result.getStart()).getType().equals(PieceType.UNKNOWN)){
            return false;
        }
        else if (getPiece(result.getEnd()) != null && !getPiece(result.getEnd()).getType().equals(PieceType.UNKNOWN)){
            // Cannot move onto your own piece
            return false;
        }
        myMove = result;
        return true;
    }

    public synchronized boolean makeTheirMove(Location loc, Direction direction){
        return makeTheirMove(loc.getX(), loc.getY(), direction);
    }

    public synchronized boolean makeTheirMove(int x, int y, Direction direction){
        if (theirMove != null) {
            return false;
        }
        Location loc = new Location(x, y);
        Move result = makeMove(loc, direction);
        if (result == null){
            return false;
        }
        if (getPiece(result.getStart()) == null || !getPiece(result.getStart()).getType().equals(PieceType.UNKNOWN)){
            return false;
        }
        else if (getPiece(result.getEnd()) != null && getPiece(result.getEnd()).getType().equals(PieceType.UNKNOWN)){
            // Cannot move onto your own piece
            return false;
        }
        theirMove = result;
        return true;
    }

    public List<Round> getRounds(){
        return Collections.unmodifiableList(rounds);
    }

    public Move getMyMove(){
        return myMove;
    }
}
