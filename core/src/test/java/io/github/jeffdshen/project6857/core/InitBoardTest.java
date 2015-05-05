package io.github.jeffdshen.project6857.core;

import io.github.jeffdshen.project6857.core.board.*;
import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.Assert.*;

/**
 * Created by chenp on 4/22/2015.
 */
public class InitBoardTest {
    @Test
    public void testGet(){
        InitBoard board = new InitBoard(10, 10, 4, InitBoard.getDefaultPieces());
        assertEquals(board.getPiece(6, 6), new Piece(PieceType.UNKNOWN, Rank.UNKNOWN));
        assertNull(board.getPiece(0, 0));
        assertNull(board.getPiece(11, 11));

        board.setPiece(0, 0, new Piece(PieceType.FLAG, Rank.FLAG));
        assertEquals(board.getPiece(0, 0), new Piece(PieceType.FLAG, Rank.FLAG));
        assertFalse(board.setPiece(1, 1, new Piece(PieceType.FLAG, Rank.FLAG)));
        assertFalse(board.setPiece(0, 0, new Piece(PieceType.ROCK, Rank.ONE)));
        assertTrue(board.setPiece(1, 1, new Piece(PieceType.ROCK, Rank.ONE)));

        assertFalse(board.removePiece(2, 2));
        assertFalse(board.removePiece(11, 11));

    }

    @Test
    public void testSet(){
        InitBoard board = new InitBoard(10, 10, 4, InitBoard.getDefaultPieces());
        board.setPiece(0, 0, new Piece(PieceType.FLAG, Rank.FLAG));
        assertEquals(board.getPiece(0, 0), new Piece(PieceType.FLAG, Rank.FLAG));
        assertFalse(board.setPiece(1, 1, new Piece(PieceType.FLAG, Rank.FLAG)));
        assertFalse(board.setPiece(0, 0, new Piece(PieceType.ROCK, Rank.ONE)));
        assertTrue(board.setPiece(1, 1, new Piece(PieceType.ROCK, Rank.ONE)));
        assertFalse(board.setPiece(5, 5, new Piece(PieceType.ROCK, Rank.TWO)));
    }

    @Test
    public void testRemove(){
        InitBoard board = new InitBoard(10, 10, 4, InitBoard.getDefaultPieces());
        assertFalse(board.removePiece(8, 8));
        assertFalse(board.removePiece(10, 10));
        assertFalse(board.removePiece(3, 3));
        board.setPiece(3, 3, new Piece(PieceType.PAPER, Rank.ONE));
        assertTrue(board.removePiece(3, 3));
        assertNull(board.getPiece(3, 3));
    }

    @Test
    public void getRemainingPieces(){
        InitBoard board = new InitBoard(10, 10, 4, InitBoard.getDefaultPieces());
        Map<Piece, Integer> remaining = board.getRemainingPieces();
        for (Piece p: remaining.keySet()){
            assertEquals(remaining.get(p), InitBoard.getDefaultPieces().get(p));
        }
    }

    @Test
    public void getBoard(){
        InitBoard board = new InitBoard(10, 10, 4, InitBoard.getDefaultPieces());
        Piece[][] getBoard = board.getBoard();
        for (int i = 0; i < getBoard.length; i ++){
            for (int j = 0; j < getBoard[i].length; j ++){
                assertEquals(getBoard[i][j], board.getPiece(j, i));
            }
        }
    }
}
