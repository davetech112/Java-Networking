package org.example;
import org.junit.Assert;
import org.junit.Test;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerTest {
    private static final int SERVER_PORT = 8080;
    @Test
    public void testStartServer() throws IOException {
        Thread serverThread = new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
                serverSocket.accept();
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        serverThread.start();


        try {
            Socket clientSocket = new Socket("localhost", SERVER_PORT);
            Assert.assertTrue(clientSocket.isConnected());
            System.out.println("connected successfully");
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail("Socket connection failed");
        }
    }
}