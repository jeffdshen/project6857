package io.github.jeffdshen.project6857.core.net;

import io.github.jeffdshen.project6857.core.board.Board;

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
    private BufferedReader in;
    private PrintWriter out;

    public Connection(Socket socket, Board board) throws IOException {
        this.socket = socket;
        this.board = board;
        in = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(),
                true);
    }

    @Override
    public void run() {

    }
}
