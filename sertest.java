
import java.io.*;
import java.net.*;

/**
 * MultiThreadedServer: A TCP server that listens for multiple clients and handles each one in a separate thread.
 */
public class sertest {
    public static void main(String[] args) {
        final int PORT = 12345; // Port on which the server listens for incoming connections

        try (ServerSocket serverSocket = new ServerSocket(PORT)) { // Create a server socket
            System.out.println("Server is running on port " + PORT);

            while (true) { // Infinite loop to accept multiple client connections
                Socket clientSocket = serverSocket.accept(); // Accept a new client connection
                System.out.println("New client connected: " + clientSocket.getInetAddress());

                // Create a new thread to handle this client
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start(); // Start the thread
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

/**
 * ClientHandler: A separate thread that handles communication with a single client.
 */
class ClientHandler implements Runnable {
    private final Socket clientSocket; // The socket representing the connected client

    public ClientHandler(Socket socket) {
        this.clientSocket = socket; // Assign the client's socket to the class variable
    }

    @Override
    public void run() {
        // Try-with-resources to automatically close resources after execution
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // Input stream to receive data from the client
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true) // Output stream to send data to the client
        ) {
            String message;
            while ((message = in.readLine()) != null) { // Read client messages line by line
                System.out.println("Received: " + message); // Print received message to server console
                out.println("Server received: " + message); // Send response back to client
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close(); // Close the client connection when finished
                System.out.println("Client disconnected: " + clientSocket.getInetAddress());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
