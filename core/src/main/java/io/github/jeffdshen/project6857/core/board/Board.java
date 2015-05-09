package io.github.jeffdshen.project6857.core.board;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenp on 4/18/2015.
 */
public class Board {
    private Piece[][] board;

    private Object myMoveLock;
    private Object theirMoveLock;

    private Move myMove;
    private Move theirMove;
    private List<Round> rounds;
    private final int width;
    private final int height;

    public Board(Piece[][] board){
        this.board = board;
        height = board.length;
        width = board[0].length;
        rounds = new ArrayList<>();
        myMoveLock = new Object();
        theirMoveLock = new Object();
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
        return new Piece(this.board[y][x].getType(), this.board[y][x].getRank(), this.board[y][x].getIsMine());
    }

    private void setPiece(Location loc, Piece piece){
        board[loc.getY()][loc.getX()] = piece;
    }

    // Returns true if the game is over
    public synchronized boolean startRound() {
        synchronized (myMoveLock) {
            synchronized (theirMoveLock) {
                boolean gameOver = false;
                Result myStatus;
                Result theirStatus;

                if (myMove.getEnd().equals(theirMove.getEnd())){
                    // Checks if the two pieces are colliding on the same square
                    Result result = compare(myMove.getStart(), theirMove.getStart());
                    myStatus = result;
                    theirStatus = result.opposite();
                } else {
                    if (getPiece(myMove.getEnd()) == null || myMove.getEnd().equals(theirMove.getStart())){
                        // Checks if moving onto an empty piece (piece that the other player just vacated is empty)
                        myStatus = new Result(Compare.WIN, null, null);
                    } else {
                        myStatus = compare(myMove.getStart(), myMove.getEnd());
                    }

                    if (getPiece(theirMove.getEnd()) == null || theirMove.getEnd().equals(myMove.getStart())){
                        // Checks if moving onto an empty piece (piece that the other player just vacated is empty)
                        theirStatus = new Result(Compare.WIN, null, null);
                    } else {
                        theirStatus = compare(theirMove.getEnd(), theirMove.getStart()).opposite();
                    }
                }

                // Sets the end location based on the comparison results
                Piece myPiece = getPiece(myMove.getStart());
                Piece theirPiece = getPiece(theirMove.getStart());
                setPiece(myMove.getStart(), null);
                setPiece(theirMove.getStart(), null);
                if (myStatus.getCompare() == Compare.WIN || myStatus.getCompare() == Compare.GAMEWIN){
                    setPiece(myMove.getEnd(), myPiece);
                } else if (myStatus.getCompare() == Compare.TIE){
                    setPiece(myMove.getEnd(), null);
                }
                if (theirStatus.getCompare() == Compare.WIN || theirStatus.getCompare() == Compare.GAMEWIN){
                    setPiece(theirMove.getEnd(), theirPiece);
                } else if (theirStatus.getCompare() == Compare.TIE){
                    setPiece(theirMove.getEnd(), null);
                }

                // Adds the round to a arraylist
                rounds.add(new Round(myMove, myStatus, theirMove, theirStatus));

                // Checks if the game is over
                if (myStatus.getCompare() == Compare.GAMEWIN || theirStatus.getCompare() == Compare.GAMEWIN){
                    gameOver = true;
                }

                // Resets the move
                myMove = null;
                theirMove = null;
                return gameOver;
            }
        }
    }

    private Result compare(Location loc1, Location loc2){
        if (getPiece(loc1) == null || getPiece(loc2) == null){
            throw new IllegalArgumentException();
        }
        if (getPiece(loc1).getType() == PieceType.UNKNOWN && getPiece(loc2).getType() == PieceType.UNKNOWN){
            throw new IllegalArgumentException();
        }
        if (getPiece(loc1).getIsMine() == getPiece(loc2).getIsMine()){
            throw new IllegalArgumentException();
        }
        if (getPiece(loc1).getType() != PieceType.UNKNOWN && getPiece(loc2).getType() != PieceType.UNKNOWN){
            Piece piece1 = getPiece(loc1);
            Piece piece2 = getPiece(loc2);
            if (!getPiece(loc1).getIsMine()){
                piece1 = getPiece(loc2);
                piece2 = getPiece(loc1);
            }
            if (piece1.getType() == PieceType.FLAG){
                return new Result(Compare.GAMELOSS, piece1, piece2);
            } else if (piece2.getType() == PieceType.FLAG){
                return new Result(Compare.GAMEWIN, piece1, piece2);
            } else if (piece1.getType() == piece2.getType()){
                if (piece1.getRank() == piece2.getRank()){
                    return new Result(Compare.TIE, piece1, piece2);
                } else if (piece1.getRank() == Rank.ONE ||
                        (piece1.getRank() == Rank.TWO && piece2.getRank() != Rank.ONE) ||
                        (piece1.getRank() == Rank.THREE && piece2.getRank() != Rank.ONE && piece2.getRank() != Rank.TWO) ||
                        (piece1.getRank() == Rank.FOUR && (piece2.getRank() == Rank.FIVE || piece2.getRank() == Rank.BOMB)) ||
                        (piece1.getRank() == Rank.FIVE && piece2.getRank() == Rank.BOMB)){
                    return new Result(Compare.LOSS, piece1, piece2);
                }
                return new Result(Compare.WIN, piece1, piece2);
            } else if ((piece1.getType() == PieceType.ROCK && piece2.getType() == PieceType.PAPER) ||
                    (piece1.getType() == PieceType.PAPER && piece2.getType() == PieceType.SCISSORS) ||
                    (piece1.getType() == PieceType.SCISSORS && piece2.getType() == PieceType.ROCK)){
                if ((piece1.getRank() == Rank.THREE && piece2.getRank() == Rank.ONE) ||
                        (piece1.getRank() == Rank.FOUR && piece2.getRank() == Rank.TWO) ||
                        (piece1.getRank() == Rank.FIVE && piece2.getRank() == Rank.THREE)) {
                    return new Result(Compare.TIE, piece1, piece2);
                } else if (piece1.getRank() == Rank.ONE || piece1.getRank() == Rank.TWO || piece1.getRank() == Rank.THREE ||
                        (piece1.getRank() == Rank.FOUR && piece2.getRank() != Rank.ONE) ||
                        (piece1.getRank() == Rank.FIVE && piece2.getRank() != Rank.ONE && piece2.getRank() != Rank.TWO)){
                    return new Result(Compare.LOSS, piece1, piece2);
                }
                return new Result(Compare.WIN, piece1, piece2);
            } else {
                if ((piece2.getRank() == Rank.THREE && piece1.getRank() == Rank.ONE) ||
                        (piece2.getRank() == Rank.FOUR && piece1.getRank() == Rank.TWO) ||
                        (piece2.getRank() == Rank.FIVE && piece1.getRank() == Rank.THREE)) {
                    return new Result(Compare.TIE, piece1, piece2);
                } else if (piece2.getRank() == Rank.ONE || piece2.getRank() == Rank.TWO || piece2.getRank() == Rank.THREE ||
                        (piece2.getRank() == Rank.FOUR && piece1.getRank() != Rank.ONE) ||
                        (piece2.getRank() == Rank.FIVE && piece1.getRank() != Rank.ONE && piece1.getRank() != Rank.TWO)){
                    return new Result(Compare.WIN, piece1, piece2);
                }
                return new Result(Compare.LOSS, piece1, piece2);
            }
        }
        return null; //TODO do Fairplay here to determine win
    }

    private Result compare(int myX, int myY, int theirX, int theirY){
        Location loc1 = new Location(myX, myY);
        Location loc2 = new Location(theirX, theirY);
        return compare(loc1, loc2);
    }

    private Move makeMove(Location loc, Direction direction){
        if (getPiece(loc) == null || !getPiece(loc).canMove()){
            return null;
        }
        Location end = loc.add(direction);
        if (!inBoard(end)){
            return null;
        }
        return new Move(loc, end);
    }

    public synchronized boolean makeMyMove(Location loc, Direction direction){
        return makeMyMove(loc.getX(), loc.getY(), direction);
    }

    public synchronized boolean makeMyMove(int x, int y, Direction direction) {
        synchronized (myMoveLock) {
            if (myMove != null) {
                return false;
            }
            Location loc = new Location(x, y);
            Move result = makeMove(loc, direction);
            if (result == null) {
                return false;
            }
            if (getPiece(result.getStart()) == null || !getPiece(result.getStart()).getIsMine()) {
                return false;
            } else if (getPiece(result.getEnd()) != null && getPiece(result.getEnd()).getIsMine()) {
                // Cannot move onto your own piece
                return false;
            }
            myMove = result;
            myMoveLock.notifyAll();
            return true;
        }
    }

    public synchronized boolean makeTheirMove(Location loc, Direction direction){
        return makeTheirMove(loc.getX(), loc.getY(), direction);
    }

    public synchronized boolean makeTheirMove(int x, int y, Direction direction){
        synchronized (theirMoveLock) {
            if (theirMove != null) {
                return false;
            }
            Location loc = new Location(x, y);
            Move result = makeMove(loc, direction);
            if (result == null) {
                return false;
            }
            if (getPiece(result.getStart()) == null || getPiece(result.getStart()).getIsMine()) {
                return false;
            } else if (getPiece(result.getEnd()) != null && !getPiece(result.getStart()).getIsMine()) {
                // Cannot move onto your own piece
                return false;
            }
            theirMove = result;
            theirMoveLock.notifyAll();
            return true;
        }
    }

    public synchronized List<Round> getRounds(){
        return new ArrayList<>(rounds);
    }

    public synchronized Round getLastRound() {
        return rounds.get(rounds.size() - 1);
    }

    public Move awaitMyMove() throws InterruptedException {
        synchronized (myMoveLock) {
            while (myMove == null) {
                myMoveLock.wait();
            }
            return myMove;
        }
    }

    public Move awaitTheirMove() throws InterruptedException {
        synchronized (theirMoveLock) {
            while (theirMove == null) {
                theirMoveLock.wait();
            }
            return theirMove;
        }
    }
}
