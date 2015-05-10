package io.github.jeffdshen.project6857.core.net;

import io.github.jeffdshen.project6857.core.board.*;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by jdshen on 5/10/15.
 */
public class EncodingProtocolTest {
    @Test
    public void testEncodeBoard() throws Exception {
        InitBoard initBoard = new InitBoard(10, 10, 4, InitBoard.getDefaultPieces());
        initBoard.setPiece(0, 3, new Piece(PieceType.PAPER, Rank.FIVE, true));
        initBoard.setPiece(5, 3, new Piece(PieceType.PAPER, Rank.FOUR, true));
        initBoard.setPiece(4, 3, new Piece(PieceType.PAPER, Rank.FOUR, true));
        Board board = new Board(initBoard.getBoard(), null);
        Piece[][] pieces = board.getBoard();
        String s = EncodingProtocol.encodeBoard(pieces);
        Piece[][] decoded = EncodingProtocol.decodeBoard(s);
        assertEquals(decoded.length, pieces.length);
        for (int i = 0; i < pieces.length; i++) {
            assertEquals(decoded[i], pieces[i]);
        }
    }

    @Test
    public void testEncodeMove() throws Exception {
        Move m = new Move(new Location(6, 3), Direction.FORWARD);
        String s = EncodingProtocol.encodeMove(m);
        Move d = EncodingProtocol.decodeMove(s);
        assertEquals(d, m);


        Move m2 = new Move(new Location(0, -1), Direction.BACKWARD);
        Move d2 = EncodingProtocol.decodeMove(EncodingProtocol.encodeMove(m2));
        assertEquals(d2, m2);
    }
}


