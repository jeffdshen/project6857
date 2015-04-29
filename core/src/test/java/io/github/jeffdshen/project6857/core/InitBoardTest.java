package io.github.jeffdshen.project6857.core;

import com.sun.org.apache.xml.internal.security.Init;
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
        assertEquals(board.getPiece(6, 6), new Piece(PieceType.UNKNOWN, Rank.UNKOWN));
        assertEquals(board.getPiece(0, 0), null);
        assertEquals(board.getPiece(11, 11), null);

        board.setPiece(0, 0, new Piece(PieceType.FLAG, Rank.FLAG));
        assertEquals(board.getPiece(0, 0), new Piece(PieceType.FLAG, Rank.FLAG));
        assertEquals(board.setPiece(1, 1, new Piece(PieceType.FLAG, Rank.FLAG)), false);
        assertEquals(board.setPiece(0, 0, new Piece(PieceType.ROCK, Rank.ONE)), false);
        assertEquals(board.setPiece(1, 1, new Piece(PieceType.ROCK, Rank.ONE)), true);

        assertEquals(board.removePiece(2, 2), false);
        assertEquals(board.removePiece(11, 11), false);

    }

    @Test
    public void testSet(){
        InitBoard board = new InitBoard(10, 10, 4, InitBoard.getDefaultPieces());
        board.setPiece(0, 0, new Piece(PieceType.FLAG, Rank.FLAG));
        assertEquals(board.getPiece(0, 0), new Piece(PieceType.FLAG, Rank.FLAG));
        assertEquals(board.setPiece(1, 1, new Piece(PieceType.FLAG, Rank.FLAG)), false);
        assertEquals(board.setPiece(0, 0, new Piece(PieceType.ROCK, Rank.ONE)), false);
        assertTrue(board.setPiece(1, 1, new Piece(PieceType.ROCK, Rank.ONE)));
        assertEquals(board.setPiece(5, 5, new Piece(PieceType.ROCK, Rank.TWO)), false);
    }

    @Test
    public void testRemove(){
        InitBoard board = new InitBoard(10, 10, 4, InitBoard.getDefaultPieces());
        assertEquals(board.removePiece(8, 8), false);
        assertEquals(board.removePiece(10, 10), false);
        assertEquals(board.removePiece(3, 3), false);
        board.setPiece(3, 3, new Piece(PieceType.PAPER, Rank.ONE));
        assertTrue(board.removePiece(3, 3));
        assertEquals(board.getPiece(3, 3), null);
    }

    @Test
    public void getRemainingPieces(){
        InitBoard board = new InitBoard(10, 10, 4, InitBoard.getDefaultPieces());
        boolean correct = true;
        Map<Piece, Integer> remaining = board.getRemainingPieces();
        for (Piece p: remaining.keySet()){
            if (!remaining.get(p).equals(InitBoard.getDefaultPieces().get(p))){
                correct = false;
            }
        }
        assertTrue(correct);
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
