package io.github.jeffdshen.project6857.core;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * Created by chenp on 5/5/2015.
 */
public class ServerClientTest {
    @Test
    public void simpleConnection(){
        Server server = new Server(4132);
        Client client = new Client("127.0.0.1", 4132);
    }
}
