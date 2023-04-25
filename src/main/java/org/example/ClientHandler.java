package org.example;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ClientHandler implements Runnable{
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine();
            clientHandlers.add(this);
            broadcastMessage("SERVER : " + clientUsername + " has entered the chat!");
        } catch (Exception e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    @Override
    public void run() {


        String messageFromClient;
        while(socket.isConnected()){
            try{
                messageFromClient = bufferedReader.readLine();
                if(messageFromClient == null || messageFromClient.equals("quit"))
                    throw new IOException();
                broadcastMessage(messageFromClient);
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }

    }


    public void broadcastMessage(String messageToEveryone){

        for(ClientHandler clientHandler: clientHandlers){
            try{
                if(clientHandler != this){
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    clientHandler.bufferedWriter.write(dtf.format(now) + " " +messageToEveryone);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }

            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }

    }
    public void removeClientHandler(){
        clientHandlers.remove(this);
        broadcastMessage("SERVER : " + clientUsername + " has left the chat!");
    }
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        removeClientHandler();
        try{
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
            if(socket != null){
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
