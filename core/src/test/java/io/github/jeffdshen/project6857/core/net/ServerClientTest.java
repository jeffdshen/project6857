package io.github.jeffdshen.project6857.core.net;

import io.github.jeffdshen.project6857.core.net.Client;
import io.github.jeffdshen.project6857.core.net.Server;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import static org.testng.Assert.*;

/**
 * Created by chenp on 5/5/2015.
 */
public class ServerClientTest {
    @Test
    public void simpleConnection() throws InterruptedException, IOException {
        final Server server = new Server(4132);
        new Thread() {
            public void run(){
                try {
                    server.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        Thread.sleep(100);
        Client client = new Client("127.0.0.1", 4132);
        client.connect();
        Thread.sleep(100);
        BufferedReader serverIn = new BufferedReader(new InputStreamReader(
                server.socket.getInputStream()));
        PrintWriter serverOut = new PrintWriter(server.socket.getOutputStream(),
                true);
        BufferedReader clientIn = new BufferedReader(new InputStreamReader(
                client.socket.getInputStream()));
        PrintWriter clientOut = new PrintWriter(client.socket.getOutputStream(),
                true);
        serverOut.println("food");
        assertEquals(clientIn.readLine(), "food");
    }
}
