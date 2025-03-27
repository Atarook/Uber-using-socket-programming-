import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class uberClient {
    public static void main(String[] args) {
        String serverIp = "localhost";  // Adjust if needed.
        int port = 12345;
        
        try (Socket socket = new Socket(serverIp, port);
             BufferedReader in = new BufferedReader(
                 new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {
            
            System.out.println("Connected to UberServer.");
            
            // Interactive client-side menu and logic.
            while (true) {
                System.out.println("\n===== Main Menu =====");
                System.out.println("1. Login");
                System.out.println("2. Register");
                System.out.println("3. Request Ride (Customer)");
                System.out.println("4. Check Ride Status (Customer)");
                System.out.println("5. Offer Ride (Driver)");
                System.out.println("6. Update Ride (Driver)");
                System.out.println("7. Disconnect");
                System.out.print("Enter choice: ");
                String choice = scanner.nextLine();
                String command = "";
                switch (choice) {
                    case "1":
                        // Login
                        System.out.print("Enter user type (CUSTOMER/DRIVER): ");
                        String type = scanner.nextLine();
                        System.out.print("Enter username: ");
                        String user = scanner.nextLine();
                        System.out.print("Enter password: ");
                        String pass = scanner.nextLine();
                        command = "LOGIN " + type + " " + user + " " + pass;
                        break;
                    case "2":
                        // Register
                        System.out.print("Enter user type (CUSTOMER/DRIVER): ");
                        type = scanner.nextLine();
                        System.out.print("Enter desired username: ");
                        user = scanner.nextLine();
                        System.out.print("Enter desired password: ");
                        pass = scanner.nextLine();
                        command = "REGISTER " + type + " " + user + " " + pass;
                        break;
                    case "3":
                        // Request Ride (only for customers)
                        System.out.print("Enter pickup location: ");
                        String pickup = scanner.nextLine();
                        System.out.print("Enter destination: ");
                        String destination = scanner.nextLine();
                        command = "REQUEST_RIDE " + pickup + " " + destination;
                        break;
                    case "4":
                        // Check Ride Status
                        command = "CHECK_RIDE";
                        break;
                    case "5":
                        // Offer Ride (only for drivers)
                        
                        
                        System.out.print("Enter Ride ID to offer: ");
                        String rideId = scanner.nextLine();
                        System.out.print("Enter fare: ");
                        String fare = scanner.nextLine();
                        command = "OFFER_RIDE " + rideId + " " + fare;
                        break;
                    case "6":
                        // Update Ride (only for drivers)
                        System.out.print("Enter Ride ID to update: ");
                        rideId = scanner.nextLine();
                        System.out.print("Enter new status (STARTED/FINISHED): ");
                        String status = scanner.nextLine();
                        command = "UPDATE_RIDE " + rideId + " " + status;
                        break;
                    case "7":
                        command = "DISCONNECT";
                        break;
                    default:
                        System.out.println("Invalid choice. Try again.");
                        continue;
                }
                out.println(command);
                String response = in.readLine();
                if (choice.equals("5")) {  // If user selected "Check Ride Status"
                System.out.println("Active Ride Details:");
                while ((response = in.readLine()) != null && !response.equals("END")) { 
                    System.out.println(response);
                }
            } else {
                System.out.println("Server: " + response);
            }                if (response.equalsIgnoreCase("Goodbye!"))
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}