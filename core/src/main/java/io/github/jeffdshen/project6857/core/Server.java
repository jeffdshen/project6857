package io.github.jeffdshen.project6857.core;

import io.github.jeffdshen.project6857.core.board.Direction;
import io.github.jeffdshen.project6857.core.board.Location;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by chenp on 5/5/2015.
 */
public class Server {
    private int port;
    public Socket socket;

    public Server(int port){
        this.port = port;
    }

    public void connect() throws IOException {
        ServerSocket server = new ServerSocket(port);
        socket = server.accept();
    }
}
