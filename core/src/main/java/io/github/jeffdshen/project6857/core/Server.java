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
public class Server implements Runnable {
    private int port;
    public BufferedReader in;
    public PrintWriter out;

    public Server(int port){
        this.port = port;
    }

    @Override
    public void run() {
        ServerSocket server = null;
        try{
            server = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Could not listen on port " + port);
            System.exit(-1);
        }

        Socket client = null;
        try{
            client = server.accept();
        } catch (IOException e) {
            System.out.println("Accept failed: " + port);
            System.exit(-1);
        }

        try{
            in = new BufferedReader(new InputStreamReader(
                    client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(),
                    true);
        } catch (IOException e) {
            System.out.println("Read failed");
            System.exit(-1);
        }
    }
}
