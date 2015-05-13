package io.github.jeffdshen.project6857.core.net;

import io.github.jeffdshen.project6857.core.board.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Objects;

/**
 * Created by chenp on 5/5/2015.
 */
public class Connection implements Runnable, PieceComparer {
    private Socket socket;
    private Board board;
    private Piece[][] initBoard;
    private int playerHeight;
    private FairplayComparer fairplay;
    private BufferedReader in;
    private PrintWriter out;

    private Commitment myInitBoard;
    private Commitment theirInitBoard;
    private CommitmentProvider provider;

    public Connection(
        Socket socket, Piece[][] initBoard, int playerHeight, FairplayComparer fairplay, String id
    ) throws IOException {
        this.socket = socket;
        this.initBoard = initBoard;
        this.playerHeight = playerHeight;
        this.fairplay = fairplay;
        in = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(),
                true);
        fairplay.setReader(in);
        fairplay.setWriter(out);
        provider = Commitment.getCommitmentProvider(id);
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    private void exchangeInitBoard() throws IOException {
        System.out.println("exchanging boards...");
        String s = EncodingProtocol.encodeBoard(initBoard);
        myInitBoard = provider.makeCommitment(s);
        out.println(myInitBoard.getHash());
        String hash = in.readLine();
        theirInitBoard = provider.getCommitment(hash);
        System.out.println("commitment received");
    }

    private void verifyCondition(boolean condition) throws VerificationException {
        if (!condition) {
            throw new VerificationException();
        }
    }

    private void verifyInitBoard(Piece[][] theirInitBoard) throws VerificationException {
        InitBoard initVerifier = new InitBoard(
            board.getWidth(), board.getHeight(), playerHeight, InitBoard.getDefaultPieces()
        );

        // verify exact number of pieces are used
        for (int i = 0; i < theirInitBoard.length; i++) {
            for (int j = 0; j < theirInitBoard[i].length; j++) {
                Piece piece = theirInitBoard[j][i];
                if (piece != null && piece.getIsMine()) {
                    verifyCondition(initVerifier.setPiece(i, j, piece));
                }
            }
        }
        verifyCondition(initVerifier.noRemainingPieces());

        Piece[][] initBoardVerifier = initVerifier.getBoard();
        verifyCondition(initBoardVerifier.length == theirInitBoard.length);
        for (int i = 0; i < initBoardVerifier.length; i++) {
            verifyCondition(initBoardVerifier[i].length == theirInitBoard[i].length);
            for (int j = 0; j < initBoardVerifier[i].length; j++) {
                verifyCondition(Objects.equals(initBoardVerifier[i][j], theirInitBoard[i][j]));
            }
        }
    }

    private void verifyBoard(Piece[][] theirInitBoard) throws VerificationException, IOException {
        Piece[][] combinedBoard = new Piece[initBoard.length][initBoard[0].length];
        for (int i = 0; i < initBoard.length; i++) {
            for (int j = 0; j < initBoard[i].length; j++) {
                if (initBoard[i][j] != null && initBoard[i][j].getIsMine()) {
                    combinedBoard[i][j] = initBoard[i][j];
                }
                Location loc = board.getRotatedLocation(new Location(j, i));
                if (theirInitBoard[i][j]!= null && theirInitBoard[i][j].getIsMine()) {
                    Piece theirs = theirInitBoard[i][j];
                    combinedBoard[loc.getY()][loc.getX()] = theirs.flipSides();
                }
            }
        }
        Board verifier = new Board(combinedBoard, null, true);

        List<Round> rounds = board.getRounds();
        for (Round round : rounds) {
            Move myMove = round.getMyMove();
            Move theirMove = round.getTheirMove();
            verifyCondition(verifier.makeMyMove(myMove.getStart(), myMove.getDirection()));
            verifyCondition(verifier.makeTheirMove(theirMove.getStart(), theirMove.getDirection()));

            boolean over = verifier.startRound();

            Round last = verifier.getLastRound();
            verifyCondition(last.getMyStatus().equals(round.getMyStatus()));
            verifyCondition(last.getTheirStatus().equals(round.getTheirStatus()));

            verifyCondition(over == (rounds.get(rounds.size() - 1) == round));
        }
    }

    private void verifyGame() throws IOException, VerificationException {
        out.println(myInitBoard.getSecret());
        String secret = in.readLine();
        verifyCondition(theirInitBoard.update(secret));
        Piece[][] theirInitBoard = EncodingProtocol.decodeBoard(this.theirInitBoard.getData());
        verifyInitBoard(theirInitBoard);
        verifyBoard(theirInitBoard);
    }

    /**
     * Returns true if the game is over.
     */
    private boolean exchangeMoves() throws IOException, VerificationException {
        try {
            System.out.println("exchanging moves");
            Move myMove = board.awaitMyMove();
            System.out.println("got my move");
            Commitment myCommit = provider.makeCommitment(EncodingProtocol.encodeMove(myMove));
            out.println(myCommit.getHash());
            String hash = in.readLine();
            Commitment theirCommit = provider.getCommitment(hash);

            out.println(myCommit.getSecret());
            String secret = in.readLine();
            verifyCondition(theirCommit.update(secret));

            System.out.println("got their move");
            Move theirMove = EncodingProtocol.decodeMove(theirCommit.getData());

            Direction dir = theirMove.getDirection();
            verifyCondition(dir != null);
            verifyCondition(board.makeTheirMove(board.getRotatedLocation(theirMove.getStart()), dir.getOpposite()));

            System.out.println("starting round");
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
            board.setVerificationResult(new VerificationResult("Verified", null));
        } catch (IOException e) {
            board.setVerificationResult(new VerificationResult("Connection error", e));
        } catch (RuntimeException e) {
            board.setVerificationResult(new VerificationResult("Invalid move or encoding", e));
        } catch (VerificationException e) {
            board.setVerificationResult(new VerificationResult("Rule violation", e));
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
            theirPiece = EncodingProtocol.decodePiece(in.readLine()).flipSides();
        }

        return new Result(compare, yourPiece, theirPiece);
    }
}
