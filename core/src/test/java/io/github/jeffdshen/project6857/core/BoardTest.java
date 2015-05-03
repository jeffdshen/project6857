package io.github.jeffdshen.project6857.core;

import io.github.jeffdshen.project6857.core.board.*;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by chenp on 4/22/2015.
 */
public class BoardTest {
    @Test
    public void initializeBoardTest(){
        InitBoard initBoard = new InitBoard(10, 10, 4, InitBoard.getDefaultPieces());
        initBoard.setPiece(0, 3, new Piece(PieceType.PAPER, Rank.FIVE));
        Board board = new Board(initBoard.getBoard());
        assertEquals(board.getPiece(0, 3), new Piece(PieceType.PAPER, Rank.FIVE));
    }

    @Test
    public void moveTest(){
        InitBoard initBoard = new InitBoard(10, 10, 4, InitBoard.getDefaultPieces());
        initBoard.setPiece(0, 3, new Piece(PieceType.PAPER, Rank.FIVE));
        initBoard.setPiece(5, 3, new Piece(PieceType.PAPER, Rank.FOUR));
        initBoard.setPiece(4, 3, new Piece(PieceType.PAPER, Rank.FOUR));
        Board board = new Board(initBoard.getBoard());

        // Moves correctly
        assertTrue(board.makeMyMove(0, 3, Direction.FORWARD));
        assertTrue(board.makeTheirMove(0, 6, Direction.BACKWARD));
        assertTrue(board.startRound());
        assertNull(board.getPiece(0, 3));
        assertNull(board.getPiece(0, 6));
        assertEquals(board.getPiece(0, 4), new Piece(PieceType.PAPER, Rank.FIVE));
        assertEquals(board.getPiece(0, 5), new Piece(PieceType.UNKNOWN, Rank.UNKOWN));

        // Cannot move other player's pieces
        assertFalse(board.makeMyMove(5, 6, Direction.BACKWARD));
        assertFalse(board.makeTheirMove(5, 3, Direction.FORWARD));

        // Cannot move onto your own piece
        assertFalse(board.makeMyMove(4, 3, Direction.RIGHT));
        assertFalse(board.makeTheirMove(4, 6, Direction.LEFT));

        // If two pieces move onto each others' square, no conflicts
        assertTrue(board.makeMyMove(0, 4, Direction.FORWARD));
        assertTrue(board.makeTheirMove(0, 5, Direction.BACKWARD));
        assertTrue(board.startRound());
        assertEquals(board.getPiece(0, 5), new Piece(PieceType.PAPER, Rank.FIVE));
        assertEquals(board.getPiece(0, 4), new Piece(PieceType.UNKNOWN, Rank.UNKOWN));
    }
}
