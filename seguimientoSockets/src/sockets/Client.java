package sockets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private Scanner scanner = new Scanner(System.in);
    private final String TERMINATION_COMMAND = "Exit";

    public void connection(String ip, int puerto) {
        try {
            // Try to establish a TCP connection with the server
            socket = new Socket(ip, puerto);
            // Displays a message indicating the attempt to connect to the server
            showTest("connecting to " + socket.getInetAddress().getHostName());
        } catch (IOException e) {
            showTest("Exception when connecting:" + e.getMessage());
            System.exit(0);
        }
    }
    public static void showTest(String text){
        System.out.println(text);
    }

    public void openFlows() {
        try {
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.flush();
        } catch (IOException e) {
            showTest("IOException when sending data: " + e.getMessage());
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

    //serves to correctly close the resources of the established network connection,
    public void closeConnection(){
        try {
            inputStream.close();
            outputStream.close();
            socket.close();
            showTest("Connection close");
        } catch (IOException e) {
            showTest("IOException on close");
        }finally {
            System.exit(0);
        }
    }
    public void runConnection(String ip,int p){
        Thread h = new Thread(() ->{
            try {
                connection(ip,p);
                openFlows();
                receiveData();
            }finally {
                closeConnection();
            }
        });
        h.start();
    }
    public void receiveData(){
        String st = "";
        try {
            do {
                st = inputStream.readUTF();
                showTest("\n[Server] =>" + st);
                System.out.println("\n[You] =>");
            }while (!st.equals(TERMINATION_COMMAND));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void writeData(){
        String s = "";
        while (true){
            System.out.println("[You] =>");
            s = scanner.nextLine();
            if (!s.isEmpty()) send(s);
        }
    }
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Client client = new Client();

        showTest("Enter the IP (localhost by default): ");
        String ip = scanner.nextLine();
        if (ip.length() <= 0) ip = "localhost";

        showTest("Port (7769 for defect): ");
        String port = scanner.nextLine();
        if (port.length() <= 0) port = "7769";
        client.runConnection(ip,Integer.parseInt(port));
        client.writeData();

    }
}
