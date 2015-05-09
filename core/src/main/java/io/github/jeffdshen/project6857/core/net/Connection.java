package io.github.jeffdshen.project6857.core.net;

import io.github.jeffdshen.project6857.core.board.Board;
import io.github.jeffdshen.project6857.core.board.Direction;
import io.github.jeffdshen.project6857.core.board.Move;
import io.github.jeffdshen.project6857.core.board.Piece;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by chenp on 5/5/2015.
 */
public class Connection implements Runnable {
    private Socket socket;
    private Board board;
    private Piece[][] initBoard;
    private BufferedReader in;
    private PrintWriter out;

    private Commitment myInitBoard;
    private Commitment theirInitBoard;

    public Connection(Socket socket, Board board, Piece[][] initBoard) throws IOException {
        this.socket = socket;
        this.board = board;
        this.initBoard = initBoard;
        in = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(),
                true);
    }

    private void exchangeInitBoard() throws IOException {
        String s = EncodingProtocol.encodeBoard(initBoard);
        myInitBoard = Commitment.makeCommitment(s);
        out.println(myInitBoard.getHash());
        String hash = in.readLine();
        theirInitBoard = Commitment.getCommitment(hash);
    }

    private void verifyGame() throws IOException, VerificationException {
        out.println(myInitBoard.getSecret());
        String secret = in.readLine();
        boolean verified = theirInitBoard.update(secret);
        if (!verified) {
            throw new VerificationException();
        }

        Piece[][] theirInitBoard = EncodingProtocol.decodeBoard(this.theirInitBoard.getData());
        // TODO MAKE A NEW BOARD
        // remember that their view is flipped


        // TODO REPLAY ALL MOVES
    }

    /**
     * Returns true if the game is over.
     */
    private boolean exchangeMoves() throws IOException, VerificationException {
        try {
            Move myMove = board.awaitMyMove();
            Commitment myCommit = Commitment.makeCommitment(EncodingProtocol.encodeMove(myMove));
            out.println(myCommit.getHash());
            String hash = in.readLine();
            Commitment theirCommit = Commitment.getCommitment(hash);

            out.println(myCommit.getSecret());
            String secret = in.readLine();
            if (!theirCommit.update(secret)) {
                throw new VerificationException();
            }

            Move theirMove = EncodingProtocol.decodeMove(theirCommit.getData());

            Direction direction = null;
            for (Direction d : Direction.values()) {
                if (theirMove.getStart().add(d).equals(theirMove.getEnd())) {
                    direction = d;
                }
            }

            if (direction == null) {
                throw new VerificationException();
            }

            if (board.makeTheirMove(board.getRotatedLocation(theirMove.getStart()), direction)) {
                throw new VerificationException();
            }

            return board.startRound();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void run() {
        try {
            exchangeInitBoard();

            while(!exchangeMoves());

            verifyGame();

        } catch (IOException e) {
            // TODO bad connection
        } catch (RuntimeException e) {
            // TODO bad encoding, invalid game (or possibly cheater)
        } catch (VerificationException e) {
            // TODO CHEATER (probably)
        }

        // end
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
