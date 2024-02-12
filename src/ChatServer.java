import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private List<ClientHandler> clients = new ArrayList<>();
    private int clientCounter = 0;

    public static void main(String[] args) {
        new ChatServer().runServer();
    }

    public void runServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(11111);
            System.out.println("Server is running...");

            while (true) {
                clientCounter++;

                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket, clientCounter);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void broadcastMessage(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private BufferedReader reader;
        private PrintWriter writer;
        private int clientNumber;

        public ClientHandler(Socket socket, int clientNumber) {
            try {
                this.clientNumber = clientNumber;
                clientSocket = socket;
                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                writer = new PrintWriter(clientSocket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            String message;
            try {
                while ((message = reader.readLine()) != null) {
                    System.out.println("Client " + clientNumber + " sent: " + message);
                    broadcastMessage("Client " + clientNumber + ": " + message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendMessage(String message) {
            writer.println(message);
        }
    }
}
