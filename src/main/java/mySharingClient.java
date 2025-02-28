//ideia o id do usuario é uma combiançao do ip:port:user
//O cliente mySharingClient é responsável por enviar e receber ficheiros para/do servidor. Este programa é
// executado em cada máquina cliente e requer a identificação de um utilizador único (por exemplo, o utilizador
// alan). Para um cliente enviar, receber ou apagar ficheiros do servidor, o utilizador deverá autenticar-se
// previamente no servidor.

import java.io.*;
import java.net.*;
import java.util.*;

public class mySharingClient {
    private static final int DEFAULT_PORT = 12345;
    
    private final String serverAddress;
    private final int serverPort;
    private final String userId;
    private final String password;
    
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean authenticated = false;


    public static void main(String[] args) {

        if (args.length < 3) {
            //System.out.println("Usage: mySharingClient <serverAddress> <user-id> <password>");
            //return;
        }
        
        // String serverAddressArg = args[0];
        // String userId = args[1];
        // String password = args[2];
        
        String serverAddress;
        int serverPort = DEFAULT_PORT;

        //tests
        String serverAddressArg = "localhost:12345";
        String userId = "alan";
        String password = "12345";
         

        // Parse server address and port from the first argument
        if (serverAddressArg.contains(":")) {
            String[] parts = serverAddressArg.split(":");
            serverAddress = parts[0];
            serverPort = Integer.parseInt(parts[1]);
        } else {
            serverAddress = serverAddressArg;
        }

        System.out.println("cliente: main");

        mySharingClient client = new mySharingClient(serverAddress, serverPort, userId, password);
        if (!client.connect()) {
            System.exit(1);
        }

        boolean authenticated = false;
        Scanner scanner = new Scanner(System.in);

        //authentication
        while (!authenticated) {
            authenticated = client.authenticate();
            
            if (!authenticated) {
                System.out.print("Enter password: ");
                password = scanner.nextLine();
                client.disconnect();
                client = new mySharingClient(serverAddress, serverPort, userId, password);
                if (!client.connect()) {
                    System.exit(1);
                }
            }

        }
        //client.processCommands();
        client.disconnect();
        scanner.close();
    }
    
    //construtor
    public mySharingClient(String serverAddress, int serverPort, String userId, String password) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.userId = userId;
        this.password = password;
    }

    public boolean connect() {
        try {
            socket = new Socket(serverAddress, serverPort);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            return true;
        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
            return false;
        }
    }

    public boolean authenticate() {
        try {
            // Send authentication request
            out.println("AUTH " + userId + " " + password);
            
            // Get response
            String response = in.readLine();
            
            if (response.equals("OK-USER") || response.equals("OK-NEW-USER")) {
                authenticated = true;
                if (response.equals("OK-NEW-USER")) {
                    System.out.println("New user registered successfully.");
                } else {
                    System.out.println("Authentication successful.");
                }
                return true;
            } else if (response.equals("WRONG-PWD")) {
                System.out.println("Wrong password. Please try again.");
                return false;
            } else {
                System.out.println("Authentication failed: " + response);
                return false;
            }
        } catch (IOException e) {
            System.err.println("Error during authentication: " + e.getMessage());
            return false;
        }
    }

    public void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }


}