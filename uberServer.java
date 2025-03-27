/*TODO
 * MODIFY THE CISTOMER MAP TRY TO MAKE A MABE TOCUSTOMER INTED OF TO STRING
 * SPLIT THE MENUE IF THE LOGIN IS A CUSTOMER PRINT CUSTOMER MENUE ...
 * SOLVE THE LOGIN ERRORS 
 * 
 * 
 */


import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class uberServer {
    public static final int PORT = 12345;
    public static final String ADMIN_USERNAME = "admin";
    
    // In‑memory user storage
    public static Map<String, customer> customers = new ConcurrentHashMap<>();
    public static Map<String, driver> drivers = new ConcurrentHashMap<>();
    // Active rides storage
    public static Map<String, RideInfo> activeRides = new ConcurrentHashMap<>();
    public static AtomicInteger rideIdCounter = new AtomicInteger(1);
    
    // Simple ride information class.
    public static class RideInfo {
        String rideId;
        String customer;
        String driver;
        String pickup;
        String destination;
        String status; // REQUESTED, ACCEPTED, STARTED, FINISHED
        double fare;
        
        public RideInfo(String customer, String pickup, String destination) {
            this.rideId = "R" + rideIdCounter.getAndIncrement();
            this.customer = customer;
            this.pickup = pickup;
            this.destination = destination;
            this.status = "REQUESTED";
        }
        @Override
        public String toString() {
            return "Ride ID: " + rideId +
                   " | Customer: " + customer +
                   " | Driver: " + (driver == null ? "None" : driver) +
                   " | Pickup: " + pickup +
                   " | Destination: " + destination +
                   " | Status: " + status +
                   " | Fare: " + fare;
        }
    }

    public static class customer{
        String customerName;
        String customerPass;
        public customer(String customerName, String customerPass){
            this.customerName = customerName;
            this.customerPass = customerPass;
        }

        @Override
        public String toString() {
            return "Customer Name: " + customerName +
                    " | Customer Password: " + customerPass;
        }
    }


    public static class driver{
        String driverName;
        String driverPass;
        String driverStatus;
        public driver(String driverName, String driverPass, String driverStatus){
            this.driverName = driverName;
            this.driverPass = driverPass;
            this.driverStatus = driverStatus;
        }

        @Override
        public String toString() {
            return "Driver Name: " + driverName +
                    " | Driver Password: " + driverPass +
                    " | Driver Status: " + driverStatus;
        }
    }

    public static void main(String[] args) {
        // Pre-register admin driver.
       // drivers.put(ADMIN_USERNAME, "admin123");
        System.out.println("UberServer started on port " + PORT);
        
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("New connection from: " + clientSocket.getInetAddress());
                    // Spawn a new thread to handle each client.
                    new ClientHandler(clientSocket).start();

                    // ClientHandler clientHandler = new ClientHandler(clientSocket);
                    // new Thread(clientHandler).start();


                } catch (IOException e) {
                    System.err.println("Error accepting connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // ClientHandler processes raw commands from a client.
    private static class ClientHandler extends Thread {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String username;
        private String clientType; // "CUSTOMER" or "DRIVER"
        
        public ClientHandler(Socket socket) {
            this.socket = socket;
        }
        
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                
                String line;
                while ((line = in.readLine()) != null) {
                    String[] tokens = line.split(" ");
                    if (tokens.length == 0)
                        continue;
                    String command = tokens[0];
                    
                    if (command.equalsIgnoreCase("LOGIN")) {
                        // Expected format: LOGIN <type> <username> <password>
                        if (tokens.length < 4) {
                            out.println("ERROR: Invalid LOGIN format.");
                            continue;
                        }
                        clientType = tokens[1].toUpperCase();
                        username = tokens[2];
                        String pass = tokens[3];
                        if (clientType.equals("CUSTOMER")) {
                            if (!customers.containsKey(username) || !customers.get(username).customerPass.equals(pass))
                                out.println("ERROR: Invalid credentials.");
                            else
                                out.println("SUCCESS: Logged in as CUSTOMER " + username);
                        } else if (clientType.equals("DRIVER")) {
                            if (!drivers.containsKey(username) || !drivers.get(username).driverPass.equals(pass))
                                out.println("ERROR: Invalid credentials.");
                            else
                                out.println("SUCCESS: Logged in as DRIVER " + username);
                        } else {
                            out.println("ERROR: Unknown user type.");
                        }
                    } else if (command.equalsIgnoreCase("REGISTER")) {
                        // Expected format: REGISTER <type> <username> <password>
                        if (tokens.length < 4) {
                            out.println("ERROR: Invalid REGISTER format.");
                            continue;
                        }
                        clientType = tokens[1].toUpperCase();
                        username = tokens[2];
                        String pass = tokens[3];
                        if (clientType.equals("CUSTOMER")) {
                            if (customers.containsKey(username))
                                out.println("ERROR: Username already exists.");
                            else {
                                customer customer = new customer(username, pass);
                                customers.put(customer.customerName, customer);
                                out.println("SUCCESS: Registered CUSTOMER " + username);
                            }
                        } else if (clientType.equals("DRIVER")) {
                            if (drivers.containsKey(username))
                                out.println("ERROR: Username already exists.");
                            else {
                                driver d = new driver(username, pass, "AVAILABLE");
                                drivers.put(username, d);
                                out.println("SUCCESS: Registered DRIVER " + username);
                            }
                        } else {
                            out.println("ERROR: Unknown user type.");
                        }
                    } else if (command.equalsIgnoreCase("REQUEST_RIDE")) {
                        System.out.println("REQUEST_RIDE");
                        for (Map.Entry<String, driver> entry : drivers.entrySet()) {
                            driver d = entry.getValue();
                            System.out.println("Driver: " + d.driverName + ", Status: " + d.driverStatus);
                        }
                        boolean found = false;
                        for (Map.Entry<String, driver> entry : drivers.entrySet()) {

                            if (entry.getValue().driverStatus.equals("AVAILABLE")) {
                                found   = true;
                                break;
                            }
                        }
                        if (drivers.size() == 0 || !found) {
                            out.println("ERROR: No drivers available.");
                            continue;
                        }
                        // Format: REQUEST_RIDE <pickup> <destination>
                        if (clientType == null || !clientType.equals("CUSTOMER")) {
                            out.println("ERROR: Only customers can request rides.");
                            continue;
                        }
                        if (tokens.length < 3) {
                            out.println("ERROR: Invalid REQUEST_RIDE format.");
                            continue;
                        }
                        String pickup = tokens[1];
                        String destination = tokens[2];
                        RideInfo ride = new RideInfo(username, pickup, destination);
                        activeRides.put(ride.rideId, ride);
                        System.out.println("activeRides: " + activeRides);
                        out.println("SUCCESS: Ride requested. Ride ID: " + ride.rideId);
                    } else if (command.equalsIgnoreCase("CHECK_RIDE")) {
                        // List all rides for the customer.
                        if (clientType == null || !clientType.equals("CUSTOMER")) {
                            out.println("ERROR: Only customers can check rides.");
                            continue;
                        }
                        boolean found = false;
                        for (RideInfo ride : activeRides.values()) {
                            if (ride.customer.equals(username)) {
                                out.println("Ride " + ride.rideId + ": " + ride.status +
                                            " | Driver: " + (ride.driver == null ? "None" : ride.driver) +
                                            " | Fare: " + ride.fare);
                                found = true;
                            }
                        }
                        if (!found)
                            out.println("No rides found.");
                    } else if (command.equalsIgnoreCase("OFFER_RIDE")) {
                        // Format: OFFER_RIDE <rideId> <fare>

                        

                        if (clientType == null || !clientType.equals("DRIVER")) {
                            out.println("ERROR: Only drivers can offer rides.");
                            continue;
                        }

                        for (Map.Entry<String, driver> entry : drivers.entrySet()) {
                            if (entry.getValue().driverName.equals(username)) {
                                if (entry.getValue().driverStatus.equals("AVAILABLE")) {
                                    entry.getValue().driverStatus = "BUSY";
                                } else {
                                    out.println("ERROR: You are already assigned to a ride.");
                                    continue;
                                }
                            }
                        }

                        if (activeRides.isEmpty()) {
                            out.println("ERROR: No rides available.");
                        }
                        else{
                            out.println("activeRides: ");
                            for (Map.Entry<String, RideInfo> entry : activeRides.entrySet()) {
                                RideInfo ride = entry.getValue();
                                if (ride.status.equals("REQUESTED")) {
                                    out.println("Ride ID: " + ride.rideId + 
                                              " | Customer: " + ride.customer +
                                              " | Pickup: " + ride.pickup + 
                                              " | Destination: " + ride.destination);
                                }
                            }
                        }
                        out.println(activeRides);
                        if (tokens.length < 3) {
                            out.println("ERROR: Invalid OFFER_RIDE format.");
                            continue;
                        }
                        String rideId = tokens[1];
                        double fare;
                        try {
                            fare = Double.parseDouble(tokens[2]);
                        } catch (NumberFormatException ex) {
                            out.println("ERROR: Invalid fare.");
                            continue;
                        }
                        RideInfo ride = activeRides.get(rideId);
                        if (ride == null) {
                            out.println("ERROR: Ride not found.");
                        } else if (!ride.status.equals("REQUESTED")) {
                            out.println("ERROR: Ride not available.");
                        } else {
                            ride.driver = username;
                            ride.fare = fare;
                            ride.status = "ACCEPTED";
                            out.println("SUCCESS: Offer accepted for ride " + ride.rideId);
                        }
                    } else if (command.equalsIgnoreCase("UPDATE_RIDE")) {
                        // Format: UPDATE_RIDE <rideId> <status> (status: STARTED or FINISHED)
                        if (clientType == null || !clientType.equals("DRIVER")) {
                            out.println("ERROR: Only drivers can update rides.");
                            continue;
                        }
                        if (tokens.length < 3) {
                            out.println("ERROR: Invalid UPDATE_RIDE format.");
                            continue;
                        }
                        String rideId = tokens[1];
                        String newStatus = tokens[2].toUpperCase();
                        RideInfo ride = activeRides.get(rideId);
                        if (ride == null) {
                            out.println("ERROR: Ride not found.");
                        } else if (!username.equals(ride.driver)) {
                            out.println("ERROR: You are not assigned to this ride.");
                        } else {
                            if (newStatus.equals("STARTED") || newStatus.equals("FINISHED")) {
                                ride.status = newStatus;
                                out.println("SUCCESS: Ride " + ride.rideId + " updated to " + newStatus);
                            } else {
                                out.println("ERROR: Invalid status update.");
                            }
                        }
                    } else if (command.equalsIgnoreCase("DISCONNECT")) {
                        out.println("Goodbye!");
                        break;
                    } else {
                        out.println("ERROR: Unknown command.");
                    }
                }
            } catch (IOException e) {
                System.err.println("Client handler exception: " + e.getMessage());
            } finally {
                try { socket.close(); } catch (IOException ex) { }
            }
        }
    }
}