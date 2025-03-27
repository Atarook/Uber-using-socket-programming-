// package classes;

// import java.io.*;
// import java.net.Socket;
// import java.util.Scanner;

// public class ubclint {
//     private static final String SERVER_IP = "localhost";
//     private static final int SERVER_PORT = 12345;
//     public static String  type;  
//     public static boolean login;
//     public static String command;
    
//     public static void main(String[] args) {
//         try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
//              BufferedReader in = new BufferedReader(
//                  new InputStreamReader(socket.getInputStream()));
//              PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//              Scanner scanner = new Scanner(System.in)) {
            
//             System.out.println("Connected to UberServer.");
            
//             // Interactive client-side menu and logic
//             while (true) {
               
//                 if (type.isEmpty()){
//                 displayMainMenu();
//                 String choice = scanner.nextLine();
//                 String command = processUserChoice(choice, scanner);
//             }
//             else {
            
//                 if (type=="customer"){
//                     cust_menu();
//                     String choice = scanner.nextLine();
//                     String command=cust_menu(choice, scanner);
//                 }
//                 else{
//                     dr_menu();
//                     String choice = scanner.nextLine();
//                   String  command=driv_menu(choice, scanner);   
//                 }
                
//                 if (command.isEmpty()) {
//                     continue;
//                 }
//             }
//                 out.println(command);
            
//                 // Handle the server's response
//                 if (command.startsWith("OFFER_RIDE") && command.split(" ").length == 1) {
//                     // Special handling for viewing available rides
//                     System.out.println("Available Rides:");
//                     System.out.println("--------------------------------------------------");
//                     String response;
//                     while (!(response = in.readLine()).equals("END")) {
//                         System.out.println(response);
//                     }
//                     System.out.println("--------------------------------------------------");
                    
//                     // After displaying rides, allow the driver to make an offer
//                     System.out.print("Would you like to make an offer? (yes/no): ");
//                     String makeOffer = scanner.nextLine();
//                     if (makeOffer.equalsIgnoreCase("yes")) {
//                         System.out.print("Enter Ride ID to offer: ");
//                         String rideId = scanner.nextLine();
//                         System.out.print("Enter fare: ");
//                         String fare = scanner.nextLine();
                        
//                         command = "OFFER_RIDE " + rideId + " " + fare;
//                         out.println(command);
//                         String offerResponse = in.readLine();
//                         System.out.println("Server: " + offerResponse);
//                     }
//                 } else {
//                     // Regular command handling
//                     String response = in.readLine();
//                     System.out.println("Server: " + response);
                    
//                     if (response.equalsIgnoreCase("Goodbye!")) {
//                         break;
//                     }
//                 }
//             }
//         } catch (IOException e) {
//             System.err.println("Error connecting to server: " + e.getMessage());
//             e.printStackTrace();
//         }
//     }
    
//     private static void displayMainMenu() {
//         System.out.println("\n===== Main Menu =====");
//         System.out.println("1. Login");
//         System.out.println("2. Register");
//         // System.out.println("3. Request Ride (Customer)");
//         // System.out.println("4. Check Ride Status (Customer)");
//         // System.out.println("5. Offer Ride (Driver)");
//         // System.out.println("6. Update Ride (Driver)");
//         System.out.println("3. Disconnect");
//         System.out.print("Enter choice: ");
//     }
//     private static void cust_menu(){
//         System.out.println("1. Request Ride (Customer)");
//         System.out.println("2. Check Ride Status (Customer)");
//         System.out.println("3. Disconnect");
//         System.out.print("Enter choice: ");
//     }
//     private static void dr_menu(){
//         System.out.println("1. Offer Ride (Driver)");
//         System.out.println("2. Update Ride (Driver)");
//         System.out.println("3. Disconnect");
//         System.out.print("Enter choice: ");
//     }    
//     private static String processUserChoice(String choice, Scanner scanner) {
//         switch (choice) {
//             case "1":
//                 // Login
//                 System.out.print("Enter user type (CUSTOMER/DRIVER): ");
//                 type = scanner.nextLine();
//                 System.out.print("Enter username: ");
//                 String user = scanner.nextLine();
//                 System.out.print("Enter password: ");
//                 String pass = scanner.nextLine();
                
//                 return "LOGIN " + type + " " + user + " " + pass;
                
//             case "2":
//                 // Register
//                 System.out.print("Enter user type (CUSTOMER/DRIVER): ");
//                 type = scanner.nextLine();
//                 System.out.print("Enter desired username: ");
//                 user = scanner.nextLine();
//                 System.out.print("Enter desired password: ");
//                 pass = scanner.nextLine();
//                 return "REGISTER " + type + " " + user + " " + pass;
//             case "3":
//                 return "DISCONNECT";
//             default:
//                 System.out.println("Invalid choice. Try again.");
//                 return "";
//         }
//     }
//     private static String cust_menu(String choice, Scanner scanner){

        
//         switch (choice) {
            
//             case "1":
//                 // Request Ride (only for customers)
//                 System.out.print("Enter pickup location: ");
//                 String pickup = scanner.nextLine();
//                 System.out.print("Enter destination: ");
//                 String destination = scanner.nextLine();
//                 return "REQUEST_RIDE " + pickup + " " + destination;
                
//             case "2":
//                 // Check Ride Status
//                 return "CHECK_RIDE";
                
            
                
//             case "3":
//                 return "DISCONNECT";
                
//             default:
//                 System.out.println("Invalid choice. Try again.");
//                 return "";
//         }
//     }
//     private static String driv_menu(String choice, Scanner scanner){
//         switch (choice) {
            
//             case "1":
//             // Offer Ride (only for drivers)
//             System.out.println("1. View available rides");
//             System.out.println("2. Make an offer for a specific ride");
//             System.out.print("Enter choice: ");
//             String offerChoice = scanner.nextLine();
            
//             if (offerChoice.equals("1")) {
//                 return "OFFER_RIDE"; // Just view available rides
//             } else {
//                 System.out.print("Enter Ride ID to offer: ");
//                 String rideId = scanner.nextLine();
//                 System.out.print("Enter fare: ");
//                 String fare = scanner.nextLine();
//                 return "OFFER_RIDE " + rideId + " " + fare;
//             }
            
//         case "2":
//             // Update Ride (only for drivers)
//             System.out.print("Enter Ride ID to update: ");
//             String rideId = scanner.nextLine();
//             System.out.print("Enter new status (STARTED/FINISHED): ");
//             String status = scanner.nextLine();
//             return "UPDATE_RIDE " + rideId + " " + status;
                
//             case "3":
//                 return "DISCONNECT";
                
//             default:
//                 System.out.println("Invalid choice. Try again.");
//                 return "";
//         }
//     }
// }




