package classes;
import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UberServer {
    public static final int PORT = 12345;
    public static final String ADMIN_USERNAME = "admin";
    
    // Singleton instance
    private static UberServer instance;
    
    // In-memory user storage
    private Map<String, Customer> customers = new ConcurrentHashMap<>();
    private Map<String, Driver> drivers = new ConcurrentHashMap<>();
    private Map<String, RideInfo> activeRides = new ConcurrentHashMap<>();
    private Map<String, PrintWriter> activeCustomerSessions = new ConcurrentHashMap<>();
    
    // Make constructor private to enforce singleton
    private UberServer() {
        // Pre-register admin driver for testing
        Driver adminDriver = new Driver(ADMIN_USERNAME, "admin123");
        adminDriver.setStatus(""); // Set status to AVAILABLE for testing
        drivers.put(ADMIN_USERNAME, adminDriver);
    }
    
    public void addDriver(Driver driver) {
        drivers.put(driver.getUsername(), driver);
    } 
    public static synchronized UberServer getInstance() {
        if (instance == null) {
            instance = new UberServer();
        }
        return instance;
    }
    
    // Getters
    public Map<String, Customer> getCustomers() {
        return customers;
    }
    
    public Map<String, Driver> getDrivers() {
        return drivers;
    }
    
    public Map<String, RideInfo> getActiveRides() {
        return activeRides;
    }
    
    public Map<String, PrintWriter> getActiveCustomerSessions() {
        return activeCustomerSessions;
    }
    public boolean hasAvailableDrivers() {
    for (Driver driver : drivers.values()) {
        if (driver.isAvailable()) {
            return true;
        }
    }
    return false;
}
    public static void main(String[] args) {
        // Use getInstance() instead of new UberServer()
        UberServer server = getInstance();
        server.start();
    }
    
    public void start() {
        System.out.println("UberServer started on port " + PORT);
        
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("New connection from: " + clientSocket.getInetAddress());
                    System.out.println("driver count: " + drivers.size());
                    System.out.println("customer count: " + customers.size());
                    // Spawn a new thread to handle each client
                    new ClientHandler(clientSocket, this).start();
                } catch (IOException e) {
                    System.err.println("Error accepting connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}