package client;

import client.sender.Sender;

import java.io.IOException;
import java.net.URISyntaxException;

public class Application {
    
    public static void main(String[] args) throws IOException, URISyntaxException {
        
        var sender = new Sender();
        sender.login("user31", "1231231sdf2341!");
        sender.invite("user31", 1L);

        
        }
}
