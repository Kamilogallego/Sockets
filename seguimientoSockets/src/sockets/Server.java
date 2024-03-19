package sockets;

import java.io.*;
import java.net.*;
import java.lang.*;
import java.util.*;
import java.net.*;

public class Server {
    private ServerSocket serverSocket;
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private Scanner scanner = new Scanner(System.in);
    private final String TERMINATION_COMMAND = "Exit";
    public void connection(int puerto) {
        //is responsible for waiting and accepting incoming connections on a server
        try {
            serverSocket = new ServerSocket(puerto);
            showTest("Waiting for incoming connection on port " + puerto + "...");
            socket = serverSocket.accept();
            showTest("Connection established with" + socket.getInetAddress().getHostName());
        } catch (IOException e) {
            showTest("Exception when lifting: " + e.getMessage());
            System.exit(0);
        }
    }
    public static void showTest(String text){
        System.out.println(text);
    }

    public void flows() {
        try {
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.flush();
        } catch (IOException e) {
            showTest("Flow opening error: " + e.getMessage());
        }
    }

    public void send(String s){
        try {
            outputStream.writeUTF(s);
            outputStream.flush();
        } catch (IOException e) {
            showTest("IOException on send");
        }
    }
    public void closeConnection(){
        try {
            inputStream.close();
            outputStream.close();
            socket.close();
            showTest("Connection close");
        } catch (IOException e) {
            showTest("IOException on close" + e.getMessage());
        }finally {
            System.exit(0);
        }
    }
    public void runConnection(int p){
        Thread h = new Thread(() ->{
            while (true) {
                try {
                    connection(p);
                    flows();
                    receiveData();
                }
                finally{
                    closeConnection();
                }
            }
        });
        h.start();
    }
    public void receiveData(){
        String st = "";
        try {
            do {
                st = inputStream.readUTF();
                showTest("\n[Client] =>" + st);
                System.out.println("\n[You] =>");
            }while (!st.equals(TERMINATION_COMMAND));
        } catch (IOException e) {
            closeConnection();
        }
    }
    public void writeData(){
        while (true){
            System.out.println("[You] =>");
            send(scanner.nextLine());
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        Scanner sc = new Scanner(System.in);

        showTest("Enter the port [7769 by default]:");
        String port = sc.nextLine();
        if (port.length() <= 0) port = "7769";
        server.runConnection(Integer.parseInt(port));
        server.writeData();
    }

}
