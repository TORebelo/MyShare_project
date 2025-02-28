import java.io.*;
import java.net.*;
import java.util.*;

public class ClientHandler {
    private final Socket clientSocket;
    private final mySharingServer server;
    private PrintWriter out;
    private BufferedReader in;
    private String userId;
    private boolean authenticated = false;
    
    public ClientHandler(Socket socket, mySharingServer server) {
        this.clientSocket = socket;
        this.server = server;
    }

    
    public void run() {
        try {
            // Initialize input and output streams
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            
            // Handle authentication
            if (!handleAuthentication()) {
                return;
            }
            
            // Process commands
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                String response = processCommand(inputLine);
                out.println(response);
            }
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }

    private boolean handleAuthentication() throws IOException {
        String inputLine = in.readLine();
        if (inputLine == null || !inputLine.startsWith("AUTH ")) {
            out.println("Invalid authentication request");
            return false;
        }
        
        String[] parts = inputLine.split(" ", 3);
        if (parts.length < 3) {
            out.println("Invalid authentication format");
            return false;
        }
        
        String user = parts[1];
        String password = parts[2];
        
        String result = server.authenticateUser(user, password);
        
        if (result.equals("WRONG-PWD")) {
            out.println(result);
            return false;
        }
        
        userId = user;
        authenticated = true;
        out.println(result);
        return true;
    }


}
