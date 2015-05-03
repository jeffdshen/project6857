package io.github.jeffdshen.project6857.core.board;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenp on 4/18/2015.
 */
public class InitBoard {
    private Piece[][] board;
    private Map<Piece, Integer> remainingPieces;
    private int width;
    private int height;
    private int playerHeight;

    public static Map<Piece, Integer> getDefaultPieces(){
        Map<Piece, Integer> map = new HashMap<Piece, Integer>();
        map.put(new Piece(PieceType.FLAG, Rank.FLAG), 1);
        for (PieceType p : new PieceType[]{PieceType.PAPER, PieceType.ROCK, PieceType.SCISSORS}){
            map.put(new Piece(p, Rank.ONE), 4);
            map.put(new Piece(p, Rank.TWO), 3);
            map.put(new Piece(p, Rank.THREE), 2);
            map.put(new Piece(p, Rank.FOUR), 1);
            map.put(new Piece(p, Rank.FIVE), 1);
            map.put(new Piece(p, Rank.BOMB), 2);
        }
        return map;
    }

    public InitBoard(int width, int height, int playerHeight, Map<Piece, Integer> remainingPieces){
        this.width = width;
        this.height = height;
        this.playerHeight = playerHeight;
        board = new Piece[height][width];

        for (int i = 0; i < width; i ++) {
            for (int j = height - playerHeight; j < height; j++) {
                board[j][i] = new Piece(PieceType.UNKNOWN, Rank.UNKNOWN);
            }
        }

        this.remainingPieces = remainingPieces;
    }

    private boolean inBoard(int x, int y){
        return (y >= 0 && x >= 0 && y < height && x < width);
    }

    private boolean inPlayerArea(int x, int y){
        return (y >= 0 && x >= 0 && y < playerHeight && x < width);
    }

    public Piece getPiece(int x, int y){
        if (!inBoard(x, y) || this.board[y][x] == null){
            return null;
        }
        return new Piece(this.board[y][x].getType(), this.board[y][x].getRank());
    }

    public boolean setPiece(int x, int y, Piece piece){
        if (!inPlayerArea(x, y)){
            return false;
        }
        if (getPiece(x, y) != null){
            return false;
        }
        if (!remainingPieces.containsKey(piece)){
            return false;
        }
        if (remainingPieces.get(piece) == 0){
            return false;
        }

        board[y][x] = piece;
        remainingPieces.put(piece, remainingPieces.get(piece) - 1);
        return true;
    }

    public boolean removePiece(int x, int y){
        if (!inPlayerArea(x, y)){
            return false;
        }
        if (getPiece(x, y) == null){
            return false;
        }
        Piece p = getPiece(x, y);
        remainingPieces.put(p, remainingPieces.get(p) + 1);
        board[y][x] = null;
        return true;
    }

    public Map<Piece, Integer> getRemainingPieces(){
        Map<Piece, Integer> result = new HashMap<Piece, Integer>();
        for (Piece p : remainingPieces.keySet()){
            result.put(p, remainingPieces.get(p));
        }
        return result;
    }

    public boolean noRemainingPieces() {
        for (Piece p: remainingPieces.keySet()) {
            if (remainingPieces.get(p) != 0) {
                return false;
            }
        }
        return true;
    }

    public Piece[][] getBoard(){
        Piece[][] result = new Piece[height][width];
        for (int i = 0; i < width; i ++) {
            for (int j = 0; j < height; j++) {
                result[j][i] = getPiece(i, j);
            }
        }
        return result;
    }
}
