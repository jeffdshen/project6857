package io.github.jeffdshen.project6857.core.net;

import io.github.jeffdshen.project6857.core.board.Board;
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

    private void exchangeInitBoard() {

    }

    private void verifyGame() {

    }

    /**
     * Returns true if the game is over.
     */
    private boolean exchangeMoves() {
        return false;
    }

    @Override
    public void run() {
        exchangeInitBoard();

        while(exchangeMoves());

        verifyGame();

        // end
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
