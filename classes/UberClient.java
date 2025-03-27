// package classes;

// import java.io.*;
// import java.net.Socket;
// import java.util.Scanner;

// public class UberClient {
//     private static final String SERVER_IP = "localhost";
//     private static final int SERVER_PORT = 12345;
//     public static String userType = null; // "CUSTOMER" or "DRIVER"
//     public static String username = null;
//     public static boolean loggedIn = false;
//     public static String command;

//     public static void main(String[] args) {
//         try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
//              BufferedReader in = new BufferedReader(
//                      new InputStreamReader(socket.getInputStream()));
//              PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//              Scanner scanner = new Scanner(System.in)) {

//             System.out.println("Connected to UberServer.");

//             // Start a dedicated listener thread for asynchronous notifications.
//             Thread listener = new Thread(() -> {
//                 try {
//                     String msg;
//                     while ((msg = in.readLine()) != null) {
//                         // Handle notification messages specially
//                         if (msg.startsWith("***") || msg.startsWith("\n***")) {
//                             System.out.println("\n" + msg);
//                             System.out.println("DEBUG: Received notification message");
//                         } else {
//                             // Normal message processing
//                             System.out.println("Server: " + msg);
//                         }
//                     }
//                 } catch (IOException ex) {
//                     System.err.println("Listener error: " + ex.getMessage());
//                 }
//             });
//             listener.setDaemon(true);
//             listener.start();

//             // Main loop: login or use available functions.
//             while (true) {
//                 if (!loggedIn) {
//                     displayMainMenu();
//                     String choice = scanner.nextLine();
//                     command = processMainMenuChoice(choice, scanner);
//                     if (command.isEmpty()) {
//                         continue;
//                     }
//                     out.println(command);
//                     // Pause briefly to allow server response.
//                     Thread.sleep(500);
//                     if (command.startsWith("LOGIN") || command.startsWith("REGISTER")) {
//                         loggedIn = true;
//                     }
//                     if (command.equals("DISCONNECT"))
//                         break;
//                 } else {
//                     if (userType.equalsIgnoreCase("CUSTOMER")) {
//                         displayCustomerMenu();
//                         String choice = scanner.nextLine();
//                         command = processCustomerChoice(choice, scanner);
//                         if (command.isEmpty())
//                             continue;
//                         out.println(command);
//                     } else if (userType.equalsIgnoreCase("DRIVER")) {
//                         displayDriverMenu();
//                         String choice = scanner.nextLine();
//                         command = processDriverChoice(choice, scanner);
//                         if (command.isEmpty())
//                             continue;
//                         out.println(command);
//                     } else {
//                         System.out.println("Unknown user type. Disconnecting.");
//                         out.println("DISCONNECT");
//                         break;
//                     }
//                     Thread.sleep(500);
//                 }
//             }
//         } catch (Exception e) {
//             System.err.println("Error: " + e.getMessage());
//             e.printStackTrace();
//         }
//     }

//     private static void displayMainMenu() {
//         System.out.println("\n===== Main Menu =====");
//         System.out.println("1. Login");
//         System.out.println("2. Register");
//         System.out.println("3. Disconnect");
//         System.out.print("Enter choice: ");
//     }

//     private static String processMainMenuChoice(String choice, Scanner scanner) {
//         switch (choice) {
//             case "1":
//                 System.out.print("Enter user type (CUSTOMER/DRIVER): ");
//                 userType = scanner.nextLine().toUpperCase();
//                 System.out.print("Enter username: ");
//                 username = scanner.nextLine();
//                 System.out.print("Enter password: ");
//                 String pass = scanner.nextLine();
//                 return "LOGIN " + userType + " " + username + " " + pass;
//             case "2":
//                 System.out.print("Enter user type (CUSTOMER/DRIVER): ");
//                 userType = scanner.nextLine().toUpperCase();
//                 System.out.print("Enter desired username: ");
//                 username = scanner.nextLine();
//                 System.out.print("Enter desired password: ");
//                 pass = scanner.nextLine();
//                 return "REGISTER " + userType + " " + username + " " + pass;
//             case "3":
//                 return "DISCONNECT";
//             default:
//                 System.out.println("Invalid choice. Try again.");
//                 return "";
//         }
//     }

//     private static void displayCustomerMenu() {
//         System.out.println("\n===== Customer Menu =====");
//         System.out.println("1. Request Ride");
//         System.out.println("2. Check Ride Status");
//         System.out.println("3. View Ride Offers");
//         System.out.println("4. Accept Ride Offer");
//         System.out.println("5. Cancel Ride");
//         System.out.println("6. Rate Driver");
//         System.out.println("7. Disconnect");
//         System.out.print("Enter choice: ");
//     }

//     private static String processCustomerChoice(String choice, Scanner scanner) {
//         switch (choice) {
//             case "1":
//                 System.out.print("Enter pickup location: ");
//                 String pickup = scanner.nextLine();
//                 System.out.print("Enter destination: ");
//                 String destination = scanner.nextLine();
//                 return "REQUEST_RIDE " + pickup + " " + destination;
//             case "2":
//                 return "CHECK_RIDE";
//             case "3":
//                 return "VIEW_OFFERS";
//             case "4":
//                 System.out.print("Enter offer number to accept: ");
//                 String offerNumber = scanner.nextLine();
//                 return "ACCEPT_OFFER " + offerNumber;
//             case "5":
//                 return "CANCEL_RIDE";
//             case "6":
//                 System.out.print("Enter driver username: ");
//                 String driverUsername = scanner.nextLine();
//                 System.out.print("Enter rating: ");
//                 String rating = scanner.nextLine();
//                 return "RATE_DRIVER " + driverUsername + " " + rating;
//             case "7":
//                 return "DISCONNECT";
//             default:
//                 System.out.println("Invalid choice. Try again.");
//                 return "";
//         }
//     }

//     private static void displayDriverMenu() {
//         System.out.println("\n===== Driver Menu =====");
//         System.out.println("1. View Available Rides");
//         System.out.println("2. Offer Ride");
//         System.out.println("3. Update Ride");
//         System.out.println("4. Disconnect");
//         System.out.print("Enter choice: ");
//     }

//     private static String processDriverChoice(String choice, Scanner scanner) {
//         switch (choice) {
//             case "1":
//                 return "OFFER_RIDE";
//             case "2":
//                 System.out.print("Enter Ride ID to offer: ");
//                 String rideId = scanner.nextLine();
//                 System.out.print("Enter fare: ");
//                 String fare = scanner.nextLine();
//                 return "OFFER_RIDE " + rideId + " " + fare;
//             case "3":
//                 System.out.print("Enter Ride ID to update: ");
//                 rideId = scanner.nextLine();
//                 System.out.print("Enter new status (STARTED/FINISHED): ");
//                 String status = scanner.nextLine();
//                 return "UPDATE_RIDE " + rideId + " " + status;
//             case "4":
//                 return "DISCONNECT";
//             default:
//                 System.out.println("Invalid choice. Try again.");
//                 return "";
//         }
//     }
// }


package classes;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class UberClient {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 12345;
    public static String userType = null; // "CUSTOMER" or "DRIVER"
    public static String username = null;
    public static boolean loggedIn = false;
    public static String command;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Connected to UberServer.");

            // Start a dedicated listener thread for asynchronous notifications.
            Thread listener = new Thread(() -> {
                try {
                    String msg;
                    while ((msg = in.readLine()) != null) {
                        // Handle notification messages specially
                        if (msg.startsWith("***") || msg.startsWith("\n***")) {
                            System.out.println("\n" + msg);
                            System.out.println("DEBUG: Received notification message");
                        } else {
                            // Normal message processing
                            System.out.println("Server: " + msg);
                        }
                    }
                } catch (IOException ex) {
                    System.err.println("Listener error: " + ex.getMessage());
                }
            });
            listener.setDaemon(true);
            listener.start();

            // Main loop: login or use available functions.
            while (true) {
                if (userType==null) {
                    
                    displayMainMenu();
                    String choice = scanner.nextLine();
                    command = processMainMenuChoice(choice, scanner);
                    if (command.isEmpty()) {
                        continue;
                    }
                    out.println(command);
                    // Pause briefly to allow server response.
                    Thread.sleep(500);
                  
                    if (command.equals("DISCONNECT"))
                        break;
                } else {
                    if (userType.equalsIgnoreCase("CUSTOMER")) {
                        displayCustomerMenu();
                        String choice = scanner.nextLine();
                        command = processCustomerChoice(choice, scanner);
                        if (command.isEmpty())
                            continue;
                        out.println(command);
                    } else if (userType.equalsIgnoreCase("DRIVER")) {
                        displayDriverMenu();
                        String choice = scanner.nextLine();
                        command = processDriverChoice(choice, scanner);
                        if (command.isEmpty())
                            continue;
                        out.println(command);
                    } else {
                        System.out.println("Unknown user type. Disconnecting.");
                        out.println("DISCONNECT");
                        break;
                    }
                    Thread.sleep(500);
                }
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void displayMainMenu() {
        System.out.println("\n===== Main Menu =====");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Disconnect");
        System.out.print("Enter choice: ");
    }

    private static String processMainMenuChoice(String choice, Scanner scanner) {
        switch (choice) {
            case "1":
                System.out.print("Enter user type (CUSTOMER/DRIVER): ");
                userType = scanner.nextLine().toUpperCase();
                System.out.print("Enter username: ");
                username = scanner.nextLine();
                System.out.print("Enter password: ");
                String pass = scanner.nextLine();
                return "LOGIN " + userType + " " + username + " " + pass;
            case "2":
                System.out.print("Enter user type (CUSTOMER/DRIVER): ");
                userType = scanner.nextLine().toUpperCase();
                System.out.print("Enter desired username: ");
                username = scanner.nextLine();
                System.out.print("Enter desired password: ");
                pass = scanner.nextLine();
                return "REGISTER " + userType + " " + username + " " + pass;
            case "3":
                return "DISCONNECT";
            default:
                System.out.println("Invalid choice. Try again.");
                return "";
        }
    }

    private static void displayCustomerMenu() {
        System.out.println("\n===== Customer Menu =====");
        System.out.println("1. Request Ride");
        System.out.println("2. Check Ride Status");
        System.out.println("3. View Ride Offers");
        System.out.println("4. Accept Ride Offer");
        System.out.println("5. Cancel Ride");
        System.out.println("6. Rate Driver");
        System.out.println("7. Disconnect");
        System.out.print("Enter choice: ");
    }

    private static String processCustomerChoice(String choice, Scanner scanner) {
        switch (choice) {
            case "1":
                System.out.print("Enter pickup location: ");
                String pickup = scanner.nextLine();
                System.out.print("Enter destination: ");
                String destination = scanner.nextLine();
                return "REQUEST_RIDE " + pickup + " " + destination;
            case "2":
                return "CHECK_RIDE";
            case "3":
                return "VIEW_OFFERS";
            case "4":
                System.out.print("Enter offer number to accept: ");
                String offerNumber = scanner.nextLine();
                return "ACCEPT_OFFER " + offerNumber;
            case "5":
                return "CANCEL_RIDE";
            case "6":
                System.out.print("Enter driver username: ");
                String driverUsername = scanner.nextLine();
                System.out.print("Enter rating: ");
                String rating = scanner.nextLine();
                return "RATE_DRIVER " + driverUsername + " " + rating;
            case "7":
            userType = null; // Reset session
            username = null;
            loggedIn = false;
            System.out.println("Logged out. Returning to main menu...");
                return "DISCONNECT";
            default:
                System.out.println("Invalid choice. Try again.");
                return "";
        }
    }

    private static void displayDriverMenu() {
        System.out.println("\n===== Driver Menu =====");
        System.out.println("1. View Available Rides");
        System.out.println("2. Offer Ride");
        System.out.println("3. Update Ride");
        System.out.println("4. Disconnect");
        System.out.print("Enter choice: ");
    }

    private static String processDriverChoice(String choice, Scanner scanner) {
        switch (choice) {
            case "1":
                return "OFFER_RIDE";
            case "2":
                System.out.print("Enter Ride ID to offer: ");
                String rideId = scanner.nextLine();
                System.out.print("Enter fare: ");
                String fare = scanner.nextLine();
                return "OFFER_RIDE " + rideId + " " + fare;
            case "3":
                System.out.print("Enter Ride ID to update: ");
                rideId = scanner.nextLine();
                System.out.print("Enter new status (STARTED/FINISHED): ");
                String status = scanner.nextLine();
                return "UPDATE_RIDE " + rideId + " " + status;
            case "4":
            userType = null; // Reset session
            username = null;
            loggedIn = false;
            System.out.println("Logged out. Returning to main menu...");
                return "DISCONNECT";
            default:
                System.out.println("Invalid choice. Try again.");
                return "";
        }
    }
}
