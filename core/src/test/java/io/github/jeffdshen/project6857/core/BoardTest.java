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
        initBoard.setPiece(0, 5, new Piece(PieceType.PAPER, Rank.FIVE));
        Board board = new Board(initBoard.getBoard());
        assertEquals(board.getPiece(0, 5), new Piece(PieceType.PAPER, Rank.FIVE));
    }

    @Test
    public void moveTest(){
        InitBoard initBoard = new InitBoard(10, 10, 4, InitBoard.getDefaultPieces());
        initBoard.setPiece(0, 5, new Piece(PieceType.PAPER, Rank.FIVE));
        Board board = new Board(initBoard.getBoard());

        // Moves correctly
        board.makeMyMove(0, 5, Direction.RIGHT);
        board.makeTheirMove(8, 6, Direction.BACKWARD);
        board.startRound();
        assertEquals(board.getPiece(0, 5), null);
        assertEquals(board.getPiece(8, 6), null);
        assertEquals(board.getPiece(1, 5), new Piece(PieceType.PAPER, Rank.FIVE));
        assertEquals(board.getPiece(8, 5), new Piece(PieceType.UNKNOWN, Rank.UNKOWN));

        // Cannot move other player's pieces or move onto your own pieces
        assertEquals(board.makeMyMove(5, 6, Direction.BACKWARD), false);
        assertEquals(board.makeTheirMove(5, 6, Direction.RIGHT), false);
        assertEquals(board.makeTheirMove(1, 5, Direction.BACKWARD), false);
    }
}
