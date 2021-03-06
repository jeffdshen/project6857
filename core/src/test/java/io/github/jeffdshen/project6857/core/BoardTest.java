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
        initBoard.setPiece(0, 3, new Piece(PieceType.PAPER, Rank.FIVE, true));
        Board board = new Board(initBoard.getBoard(), null, true);
        assertEquals(board.getPiece(0, 3), new Piece(PieceType.PAPER, Rank.FIVE, true));
    }

    @Test
    public void moveTest() throws Exception{
        InitBoard initBoard = new InitBoard(10, 10, 4, InitBoard.getDefaultPieces());
        initBoard.setPiece(0, 3, new Piece(PieceType.PAPER, Rank.FIVE, true));
        initBoard.setPiece(5, 3, new Piece(PieceType.PAPER, Rank.FOUR, true));
        initBoard.setPiece(4, 3, new Piece(PieceType.PAPER, Rank.FOUR, true));
        Board board = new Board(initBoard.getBoard(), null, true);

        // Moves correctly
        assertTrue(board.makeMyMove(0, 3, Direction.FORWARD));
        assertTrue(board.makeTheirMove(0, 6, Direction.BACKWARD));
        assertFalse(board.startRound());
        assertNull(board.getPiece(0, 3));
        assertNull(board.getPiece(0, 6));
        assertEquals(board.getPiece(0, 4), new Piece(PieceType.PAPER, Rank.FIVE, true));
        assertEquals(board.getPiece(0, 5), new Piece(PieceType.UNKNOWN, Rank.UNKNOWN, false));

        // Cannot move other player's pieces
        assertFalse(board.makeMyMove(5, 6, Direction.BACKWARD));
        assertFalse(board.makeTheirMove(5, 3, Direction.FORWARD));

        // Cannot move onto your own piece
        assertFalse(board.makeMyMove(4, 3, Direction.RIGHT));
        assertFalse(board.makeTheirMove(4, 6, Direction.LEFT));

        // If two pieces move onto each others' square, no conflicts
        assertTrue(board.makeMyMove(0, 4, Direction.FORWARD));
        assertTrue(board.makeTheirMove(0, 5, Direction.BACKWARD));
        assertFalse(board.startRound());
        assertEquals(board.getPiece(0, 5), new Piece(PieceType.PAPER, Rank.FIVE, true));
        assertEquals(board.getPiece(0, 4), new Piece(PieceType.UNKNOWN, Rank.UNKNOWN, false));
    }
}
