package io.github.jeffdshen.project6857.core.net;

import io.github.jeffdshen.project6857.core.board.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by chenp on 5/5/2015.
 */
public class Connection implements Runnable, PieceComparer {
    private Socket socket;
    private Board board;
    private Piece[][] initBoard;
    private FairplayComparer fairplay;
    private BufferedReader in;
    private PrintWriter out;

    private Commitment myInitBoard;
    private Commitment theirInitBoard;

    public Connection(
        Socket socket, Board board, Piece[][] initBoard, Fairplay fairplay
    ) throws IOException {
        this.socket = socket;
        this.board = board;
        this.initBoard = initBoard;
        this.fairplay = fairplay;
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

            while (!exchangeMoves()) ;

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

    @Override
    public Result compare(Piece piece) throws IOException {
        PieceType type = piece.getType();
        Rank rank = piece.getRank();
        boolean loss = type == PieceType.FLAG;
        out.println(loss);
        boolean won = Boolean.parseBoolean(in.readLine());
        Compare compare;
        if (loss) {
            compare = Compare.GAMELOSS;
        } else if (won) {
            compare = Compare.GAMEWIN;
        } else {
            compare = fairplay.compare(type, rank);
        }

        Piece yourPiece = null;
        Piece theirPiece = null;

        if (compare == Compare.GAMELOSS || compare == Compare.LOSS || compare == Compare.TIE) {
            out.println(EncodingProtocol.encodePiece(piece));
            yourPiece = piece;
        }

        if (compare == Compare.GAMEWIN || compare == Compare.WIN || compare == Compare.TIE) {
            theirPiece = EncodingProtocol.decodePiece(in.readLine());
        }

        return new Result(compare, yourPiece, theirPiece);
    }
}
