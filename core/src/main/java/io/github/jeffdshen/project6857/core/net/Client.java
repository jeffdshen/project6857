package io.github.jeffdshen.project6857.core.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by chenp on 5/5/2015.
 */
public class Client {
    private String ip;
    private int port;
    public Socket socket;

    public Client(String ip, int port){
        this.ip = ip;
        this.port = port;
    }

    public void connect() throws IOException {
        socket = new Socket(ip, port);
    }
}
