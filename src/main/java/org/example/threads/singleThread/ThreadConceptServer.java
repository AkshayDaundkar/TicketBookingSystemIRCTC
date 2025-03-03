package org.example.threads.singleThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ThreadConceptServer {

    public void run() throws IOException {
        int port =8010;
        ServerSocket socket = new ServerSocket(port);
        socket.setSoTimeout(100000);
        while (true){
            try {
                System.out.println("Server is Listening on Port:" + port);
                Socket acceptConnection = socket.accept();
                System.out.println("Connection accepted from client"+acceptConnection.getRemoteSocketAddress());


                PrintWriter toClient = new PrintWriter(acceptConnection.getOutputStream());
                BufferedReader fromClient = new BufferedReader(new InputStreamReader(acceptConnection.getInputStream()));

                toClient.println("Hello from Server");
                toClient.close();

                String Line = fromClient.readLine();
                System.out.println("Response from Client is "+Line);

                fromClient.close();
                acceptConnection.close();

            }catch (IOException ex){
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
            ThreadConceptServer server = new ThreadConceptServer();
            try{
                server.run();
            }catch (Exception ex){
                ex.printStackTrace();
            }
    }

}
