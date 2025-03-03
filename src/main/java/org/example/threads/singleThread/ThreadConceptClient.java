package org.example.threads.singleThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ThreadConceptClient {

    public void run() throws UnknownHostException,IOException {
        int port = 8010;
        InetAddress address = InetAddress.getByName("localhost");
        Socket socket = new Socket(address,port);

        PrintWriter toServer = new PrintWriter(socket.getOutputStream());
        BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        toServer.println("Hello from Client");
        String Line = fromServer.readLine();
        System.out.println("Response from Server is "+Line);

        toServer.close();
        fromServer.close();
        socket.close();
    }

    public static void main(String[] args) {
        try{
            ThreadConceptClient client = new ThreadConceptClient();
            client.run();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
