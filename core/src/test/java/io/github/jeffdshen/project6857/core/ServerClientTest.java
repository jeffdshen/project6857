package io.github.jeffdshen.project6857.core;

import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.*;

/**
 * Created by chenp on 5/5/2015.
 */
public class ServerClientTest {
    @Test
    public void simpleConnection() throws InterruptedException, IOException {
        Server server = new Server(4132);
        Client client = new Client("127.0.0.1", 4132);
        new Thread(server).start();
        new Thread(client).start();
        Thread.sleep(1000);
        server.out.println("food");
        Thread.sleep(1000);
        assertEquals(client.in.readLine(), "food");
    }
}
