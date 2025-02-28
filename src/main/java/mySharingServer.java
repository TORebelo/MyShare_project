// O servidor mySharingServer implementa o conceito de workspace, onde cada workspace pode pertencer a
// um único utilizador (exemplo do workspace001 e workspace002 representado na Figura 1) ou pode ser
// partilhado por vários utilizadores (exemplo do workspace003 representado na Figura 1)

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;


class mySharingServer {
    private static final int DEFAULT_PORT = 12345;
    private static final String USERS_FILE = "users.txt";
    private static final String WORKSPACES_FILE = "workspaces.txt";

    private ServerSocket serverSocket;
    private final int port;
    private boolean running;

    // File storage directory
    private final String fileStorageDir = "server_files";

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number. Using default port " + DEFAULT_PORT);
            }
        }
        
        mySharingServer server = new mySharingServer(port);
        
        // Add shutdown hook to save data when server is terminated
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
        
        server.start();
    }

    // Constructor
    public mySharingServer(int port) {
        this.port = port;
        // LOAD USERS 
        //load workspaces
        createFileStorageDir(); 
    }


    private void createFileStorageDir() {
        File dir = new File(fileStorageDir);
        // Create file storage directory if it doesn't exist	
        System.out.println("Creating file storage directory: " + fileStorageDir);
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                System.err.println("Failed to create file storage directory");
                System.exit(1);
            }
        }
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            running = true;
            System.out.println("Server started on port " + port);
            
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());
                    
                   

                } catch (IOException e) {
                    if (running) {
                        System.err.println("Error accepting client connection: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        } finally {
            stop();
        }
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing server socket: " + e.getMessage());
        }
        
        // Save data before shutting down
        // saveUsers();
        // saveWorkspaces();
    }

}


