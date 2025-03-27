// /*TODO
//  *  notification 
//  * wait for drivers (when request should be available driver )
//  * login and logout 
//  * 
//  * 
//  */

// package classes;

// import java.io.*;
// import java.net.*;
// import java.util.Map;

// public class ClientHandler extends Thread {
//     private Socket socket;
//     private BufferedReader in;
//     private PrintWriter out;
//     private String username;
//     private String clientType; // "CUSTOMER" or "DRIVER"
//     private UberServer server;    
    
//     public ClientHandler(Socket socket, UberServer server) {
//         this.socket = socket;
//         this.server = server;
//     }
    
//     @Override
//     public void run() {
//         try {
//             in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//             out = new PrintWriter(socket.getOutputStream(), true);
            
//             String line;
//             while ((line = in.readLine()) != null) {
//                 String[] tokens = line.split(" ");
//                 if (tokens.length == 0)
//                     continue;
//                 String command = tokens[0];
                
//                 if (command.equalsIgnoreCase("LOGIN"))
//                     handleLogin(tokens);
//                 else if (command.equalsIgnoreCase("REGISTER"))
//                     handleRegister(tokens);
//                 else if (command.equalsIgnoreCase("REQUEST_RIDE"))
//                     handleRequestRide(tokens);
//                 else if (command.equalsIgnoreCase("CHECK_RIDE"))
//                     handleCheckRide();
//                 else if (command.equalsIgnoreCase("CANCEL_RIDE"))
//                     handleCancelRide(tokens);
//                 else if (command.equalsIgnoreCase("OFFER_RIDE"))
//                     handleOfferRide(tokens);
//                 else if (command.equalsIgnoreCase("VIEW_OFFERS"))
//                     handleViewOffers();
//                 else if (command.equalsIgnoreCase("ACCEPT_OFFER"))
//                     handleAcceptOffer(tokens);
//                 else if (command.equalsIgnoreCase("UPDATE_RIDE"))
//                     handleUpdateRide(tokens);
//                 else if (command.equalsIgnoreCase("STATISTICS"))
//                     handleStatistics(tokens);
//                 else if (command.equalsIgnoreCase("RATE_DRIVER"))
//                     handleRateDriver(tokens);
//                 else if (command.equalsIgnoreCase("DISCONNECT")) {
//                     out.println("Goodbye!");
//                     break;
//                 } else {
//                     out.println("ERROR: Unknown command.");
//                 }
//             }
//         } catch (IOException e) {
//             System.err.println("Client handler exception: " + e.getMessage());
//         } finally {
//             if (clientType != null && clientType.equals("CUSTOMER") && username != null) {
//                 server.getActiveCustomerSessions().remove(username);
//                 System.out.println("DEBUG: Removed session for customer: " + username);
//             }
//             try { socket.close(); } catch (IOException ex) { }
//         }
//     }
    
//     private void handleLogin(String[] tokens) {
//         if (tokens.length < 4) {
//             out.println("ERROR: Invalid LOGIN format.");
//             return;
//         }

        
//         clientType = tokens[1].toUpperCase();
//         username = tokens[2];
//         String pass = tokens[3];
        
//         if (clientType.equals("CUSTOMER")) {
//             Customer customer = server.getCustomers().get(username);
//             if (customer == null || !customer.authenticate(pass)) {
//                 out.println("ERROR: Invalid credentials.");
//             } else {
//                 out.println("SUCCESS: Logged in as CUSTOMER " + username);
//                 // Store customer's PrintWriter so notifications can be sent later.
//                 server.getActiveCustomerSessions().put(username, out);
//                 System.out.println("DEBUG: Added session for customer: " + username);
//             }
//         } else if (clientType.equals("DRIVER")) {
//             Driver driver = server.getDrivers().get(username);
//             if (driver == null || !driver.authenticate(pass)) {
//                 out.println("ERROR: Invalid credentials.");
//             } else {
//                 out.println("SUCCESS: Logged in as DRIVER " + username);
//             }
//         } else {
//             out.println("ERROR: Unknown user type.");
//         }
//     }
    
//     private void handleRegister(String[] tokens) {
//         if (tokens.length < 4) {
//             out.println("ERROR: Invalid REGISTER format.");
//             return;
//         }
        
//         clientType = tokens[1].toUpperCase();
//         username = tokens[2];
//         String pass = tokens[3];
        
//         if (clientType.equals("CUSTOMER")) {
//             if (server.getCustomers().containsKey(username)) {
//                 out.println("ERROR: Username already exists.");
//             } else {
//                 Customer customer = new Customer(username, pass);
//                 server.getCustomers().put(username, customer);
//                 out.println("SUCCESS: Registered CUSTOMER " + username);
//             }
//         } else if (clientType.equals("DRIVER")) {
//             if (server.getDrivers().containsKey(username)) {
//                 out.println("ERROR: Username already exists.");
//             } else {
//                 Driver driver = new Driver(username, pass);
//                 server.addDriver(driver);

//                 out.println("SUCCESS: Registered DRIVER " + username);
//             }
//         } else {
//             out.println("ERROR: Unknown user type.");
//         }
//     }
    
//     private void handleRequestRide(String[] tokens) {
//         // Ensure at least one available driver
//         boolean driverAvailable = false;
//         // for (Driver driver : server.getDrivers().values()) {
//         //     if (driver.isAvailable()) {
//         //         driverAvailable = true;
//         //         break;
//         //     }
//         // }
//         if (!server.hasAvailableDrivers()) {
//             out.println("ERROR: No drivers available.");
//             return;
//         }
        
      
        
//         if (clientType == null || !clientType.equals("CUSTOMER")) {
//             out.println("ERROR: Only customers can request rides.");
//             return;
//         }
        
//         // Check if customer already has an active ride
//         for (RideInfo r : server.getActiveRides().values()) {
//             if (r.getCustomer().equals(username)) {
//                 out.println("ERROR: You already have an active ride. Cancel it before requesting another.");
//                 return;
//             }
//         }
        
//         if (tokens.length < 3) {
//             out.println("ERROR: Invalid REQUEST_RIDE format.");
//             return;
//         }
        
//         String pickup = tokens[1];
//         String destination = tokens[2];
//         RideInfo ride = new RideInfo(username, pickup, destination);
//         server.getActiveRides().put(ride.getRideId(), ride);
        
//         out.println("SUCCESS: Ride requested. Ride ID: " + ride.getRideId());
//     }
    
//     private void handleCancelRide(String[] tokens) {
//         if (clientType == null || !clientType.equals("CUSTOMER")) {
//             out.println("ERROR: Only customers can cancel rides.");
//             return;
//         }
        
//         RideInfo toRemove = null;
//         for (RideInfo r : server.getActiveRides().values()) {
//             if (r.getCustomer().equals(username)) {
//                 if (r.getStatus().equals("ACCEPTED")) {
//                     out.println("ERROR: Cannot cancel an accepted ride.");
//                     return;
//                 }
//                 toRemove = r;
//                 break;
//             }
//         }
//         if (toRemove == null) {
//             out.println("ERROR: No active ride to cancel.");
//         } else {
//             server.getActiveRides().remove(toRemove.getRideId());
//             out.println("SUCCESS: Ride " + toRemove.getRideId() + " canceled.");
//         }
//     }
    
//     private void handleCheckRide() {
//         if (clientType == null || !clientType.equals("CUSTOMER")) {
//             out.println("ERROR: Only customers can check rides.");
//             return;
//         }
        
//         boolean found = false;
//         for (RideInfo ride : server.getActiveRides().values()) {
//             if (ride.getCustomer().equals(username)) {
//                 out.println("Ride " + ride.getRideId() + ": " + ride.getStatus() +
//                           " | Driver: " + (ride.getDriver() == null ? "None" : ride.getDriver()) +
//                           " | Fare: " + ride.getFare());
//                 found = true;
//             }
//         }
        
//         if (!found) {
//             out.println("No rides found.");
//         }
//     }
    
//     // When a driver offers a ride, add the offer without immediately accepting the ride.
//     private void handleOfferRide(String[] tokens) {
//         if (clientType == null || !clientType.equals("DRIVER")) {
//             out.println("ERROR: Only drivers can offer rides.");
//             return;
//         }
        
//         Driver driver = server.getDrivers().get(username);
//         if (!driver.isAvailable()) {
//             out.println("ERROR: You are already assigned to a ride.");
//             return;
//         }
        
//         // If just OFFER_RIDE without arguments, show available rides.
//         if (tokens.length == 1) {
//             if (server.getActiveRides().isEmpty()) {
//                 out.println("ERROR: No rides available.");
//             } else {
//                 out.println("Available rides:");
//                 for (RideInfo ride : server.getActiveRides().values()) {
//                     if (ride.getStatus().equals("REQUESTED")) {
//                         out.println(ride.toString());
//                     }
//                 }
//                 out.println("END");
//             }
//             return;
//         }
        
//         if (tokens.length < 3) {
//             out.println("ERROR: Invalid OFFER_RIDE format.");
//             return;
//         }
        
//         String rideId = tokens[1];
//         double fare;
        
//         try {
//             fare = Double.parseDouble(tokens[2]);
//         } catch (NumberFormatException ex) {
//             out.println("ERROR: Invalid fare.");
//             return;
//         }
        
//         RideInfo ride = server.getActiveRides().get(rideId);
//         if (ride == null) {
//             out.println("ERROR: Ride not found.");
//         } else if (!ride.getStatus().equals("REQUESTED")) {
//             out.println("ERROR: Ride not available for offers.");
//         } else {
//             // Add the offer to the ride's list of offers
//             Offer offer = new Offer(username, fare);
//             ride.addOffer(offer);
//             out.println("SUCCESS: Offer submitted for ride " + rideId);
//             // Notify the customer if online
//             PrintWriter custOut = server.getActiveCustomerSessions().get(ride.getCustomer());
//             if (custOut != null) {
//                 custOut.println("NOTIFICATION: New offer for your ride " + rideId +
//                                " from driver " + username + " with fare " + fare);
//             }
//         }
//     }
    
//     // Allow the customer to view all offers made for their active ride.
//     private void handleViewOffers() {
//         if (clientType == null || !clientType.equals("CUSTOMER")) {
//             out.println("ERROR: Only customers can view offers.");
//             return;
//         }
//         RideInfo ride = null;
//         for (RideInfo r : server.getActiveRides().values()) {
//             if (r.getCustomer().equals(username)) {
//                 ride = r;
//                 break;
//             }
//         }
//         if (ride == null) {
//             out.println("ERROR: No active ride found.");
//             return;
//         }
//         if (ride.getOffers().isEmpty()) {
//             out.println("No offers available.");
//         } else {
//             int i = 1;
//             for (Offer offer : ride.getOffers()) {
//                 out.println("Offer " + i + ": Driver " + offer.getDriverUsername() +
//                            ", Fare " + offer.getFare());
//                 i++;
//             }
//             out.println("END");
//         }
//     }
    
//     // Allow the customer to accept one of the submitted offers.
//     private void handleAcceptOffer(String[] tokens) {
//         if (clientType == null || !clientType.equals("CUSTOMER")) {
//             out.println("ERROR: Only customers can accept offers.");
//             return;
//         }
//         if (tokens.length < 2) {
//             out.println("ERROR: Invalid ACCEPT_OFFER format. Usage: ACCEPT_OFFER <offerNumber>");
//             return;
//         }
//         int offerNumber;
//         try {
//             offerNumber = Integer.parseInt(tokens[1]);
//         } catch (NumberFormatException ex) {
//             out.println("ERROR: Offer number must be a valid number.");
//             return;
//         }
//         RideInfo ride = null;
//         for (RideInfo r : server.getActiveRides().values()) {
//             if (r.getCustomer().equals(username)) {
//                 ride = r;
//                 break;
//             }
//         }
//         if (ride == null) {
//             out.println("ERROR: No active ride found.");
//             return;
//         }
//         if (offerNumber < 1 || offerNumber > ride.getOffers().size()) {
//             out.println("ERROR: Offer number out of range.");
//             return;
//         }
//         Offer acceptedOffer = ride.getOffers().get(offerNumber - 1);
//         // Mark ride as accepted with the chosen offer.
//         ride.setDriver(acceptedOffer.getDriverUsername());
//         ride.setFare(acceptedOffer.getFare());
//         ride.setStatus("ACCEPTED");
//         // Update driver's status to BUSY.
//         Driver driver = server.getDrivers().get(acceptedOffer.getDriverUsername());
//         if (driver != null) {
//             driver.setStatus("BUSY");
//         }
//         // Clear all pending offers.
//         ride.clearOffers();
//         out.println("SUCCESS: Offer accepted. Ride " + ride.getRideId() +
//                     " is now assigned to driver " + acceptedOffer.getDriverUsername());
//         // Optionally, notify the driver if online.
//     }
    
//     private void handleUpdateRide(String[] tokens) {
//         if (clientType == null || !clientType.equals("DRIVER")) {
//             out.println("ERROR: Only drivers can update rides.");
//             return;
//         }
        
//         if (tokens.length < 3) {
//             out.println("ERROR: Invalid UPDATE_RIDE format.");
//             return;
//         }
        
//         String rideId = tokens[1];
//         String newStatus = tokens[2].toUpperCase();
        
//         RideInfo ride = server.getActiveRides().get(rideId);
//         if (ride == null) {
//             out.println("ERROR: Ride not found.");
//         } else if (!username.equals(ride.getDriver())) {
//             out.println("ERROR: You are not assigned to this ride.");
//         } else {
//             if (newStatus.equals("STARTED") || newStatus.equals("FINISHED")) {
//                 ride.setStatus(newStatus);
//                 System.out.println("DEBUG: Active customer sessions: " + server.getActiveCustomerSessions().keySet());
//                 System.out.println("DEBUG: Looking for customer: " + ride.getCustomer());

//                 // Update notification format to match client's expected format
//                 PrintWriter custOut = server.getActiveCustomerSessions().get(ride.getCustomer());
//                 if (custOut != null) {
//                     custOut.println("\n*** RIDE STATUS UPDATE ***");
//                     custOut.println("Your ride " + rideId + " has been " + newStatus + " by driver " + username);
                    
//                     if (newStatus.equals("STARTED")) {
//                         custOut.println("Your driver is on the way to your destination.");
//                     } else if (newStatus.equals("FINISHED")) {
//                         custOut.println("Your ride has been completed. Thank you for using our service!");
//                         custOut.println("You can rate your driver using: RATE_DRIVER " + username + " <rating 1-5>");
//                     }
//                 }
                
//                 if (newStatus.equals("FINISHED")) {
//                     // Update driver status to available when ride is finished.
//                     Driver driver = server.getDrivers().get(username);
//                     driver.setStatus("AVAILABLE");
                    
//                 }
                
//                 out.println("SUCCESS: Ride " + ride.getRideId() + " updated to " + newStatus);
//             } else {
//                 out.println("ERROR: Invalid status update.");
//             }
//         }
//     }
    
//     private void handleStatistics(String[] tokens) {
//         // Only admin (pre-registered as "admin") can view statistics.
//         if (!username.equalsIgnoreCase(UberServer.ADMIN_USERNAME)) {
//             out.println("ERROR: Only admin can view statistics.");
//             return;
//         }
//         int totalCustomers = server.getCustomers().size();
//         int totalDrivers = server.getDrivers().size();
        
//         int requested = 0, accepted = 0, started = 0, finished = 0;
//         for (RideInfo ride : server.getActiveRides().values()) {
//             switch (ride.getStatus()) {
//                 case "REQUESTED": requested++; break;
//                 case "ACCEPTED": accepted++; break;
//                 case "STARTED": started++; break;
//                 case "FINISHED": finished++; break;
//             }
//         }
        
//         String stats = "Overall Statistics:\n" +
//                        "Total Customers: " + totalCustomers + "\n" +
//                        "Total Drivers: " + totalDrivers + "\n" +
//                        "Rides Requested: " + requested + "\n" +
//                        "Rides Accepted: " + accepted + "\n" +
//                        "Rides Started: " + started + "\n" +
//                        "Rides Finished: " + finished;
//         out.println(stats);
//     }
    
//     private void handleRateDriver(String[] tokens) {
//         // Expected format: RATE_DRIVER <driverUsername> <rating>
//         if (clientType == null || !clientType.equals("CUSTOMER")) {
//             out.println("ERROR: Only customers can rate drivers.");
//             return;
//         }
//         if (tokens.length < 3) {
//             out.println("ERROR: Invalid RATE_DRIVER format.");
//             return;
//         }
//         String driverUsername = tokens[1];
//         double rating;
//         try {
//             rating = Double.parseDouble(tokens[2]);
//         } catch (NumberFormatException ex) {
//             out.println("ERROR: Rating must be a number.");
//             return;
//         }
        
//         Driver driver = server.getDrivers().get(driverUsername);
//         if (driver == null) {
//             out.println("ERROR: Driver not found.");
//             return;
//         }
//         driver.addRating(rating);
//         out.println("SUCCESS: Rated driver " + driverUsername +
//                     ". New average rating: " + String.format("%.2f", driver.getAverageRating()));
//     }
// }


package classes;

import java.io.*;
import java.net.*;
import java.util.Map;

public class ClientHandler extends Thread {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String username;
    private String clientType; // "CUSTOMER" or "DRIVER"
    private UberServer server;

    public ClientHandler(Socket socket, UberServer server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
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

                if (command.equalsIgnoreCase("LOGIN"))
                    handleLogin(tokens);
                else if (command.equalsIgnoreCase("REGISTER"))
                    handleRegister(tokens);
                else if (command.equalsIgnoreCase("REQUEST_RIDE"))
                    handleRequestRide(tokens);
                else if (command.equalsIgnoreCase("CHECK_RIDE"))
                    handleCheckRide();
                else if (command.equalsIgnoreCase("CANCEL_RIDE"))
                    handleCancelRide(tokens);
                else if (command.equalsIgnoreCase("OFFER_RIDE"))
                    handleOfferRide(tokens);
                else if (command.equalsIgnoreCase("VIEW_OFFERS"))
                    handleViewOffers();
                else if (command.equalsIgnoreCase("ACCEPT_OFFER"))
                    handleAcceptOffer(tokens);
                else if (command.equalsIgnoreCase("UPDATE_RIDE"))
                    handleUpdateRide(tokens);
                else if (command.equalsIgnoreCase("STATISTICS"))
                    handleStatistics(tokens);
                else if (command.equalsIgnoreCase("RATE_DRIVER"))
                    handleRateDriver(tokens);
                else if (command.equalsIgnoreCase("DISCONNECT")) {
                    out.println("Goodbye!");
                    break;
                } else {
                    out.println("ERROR: Unknown command.");
                }
            }
        } catch (IOException e) {
            System.err.println("Client handler exception: " + e.getMessage());
        } finally {
            if (clientType != null && clientType.equals("CUSTOMER") && username != null) {
                server.getActiveCustomerSessions().remove(username);
                System.out.println("DEBUG: Removed session for customer: " + username);
            }
            try {
                socket.close();
            } catch (IOException ex) { }
        }
    }

    private void handleLogin(String[] tokens) {
        if (tokens.length < 4) {
            out.println("ERROR: Invalid LOGIN format.");
            return;
        }

        clientType = tokens[1].toUpperCase();
        username = tokens[2];
        String pass = tokens[3];

        if (clientType.equals("CUSTOMER")) {
            Customer customer = server.getCustomers().get(username);
            if (customer == null || !customer.authenticate(pass)) {
                out.println("ERROR: Invalid credentials.");
            } else {
                out.println("SUCCESS: Logged in as CUSTOMER " + username);
                // Store customer's PrintWriter so notifications can be sent later.
                server.getActiveCustomerSessions().put(username, out);
                System.out.println("DEBUG: Added session for customer: " + username);
            }
        } else if (clientType.equals("DRIVER")) {
            Driver driver = server.getDrivers().get(username);
            if (driver == null || !driver.authenticate(pass)) {
                out.println("ERROR: Invalid credentials.");
            } else {
                out.println("SUCCESS: Logged in as DRIVER " + username);
            }
        } else {
            out.println("ERROR: Unknown user type.");
        }
    }

    private void handleRegister(String[] tokens) {
        if (tokens.length < 4) {
            out.println("ERROR: Invalid REGISTER format.");
            return;
        }

        clientType = tokens[1].toUpperCase();
        username = tokens[2];
        String pass = tokens[3];

        if (clientType.equals("CUSTOMER")) {
            if (server.getCustomers().containsKey(username)) {
                out.println("ERROR: Username already exists.");
            } else {
                Customer customer = new Customer(username, pass);
                server.getCustomers().put(username, customer);
                
                out.println("SUCCESS: Registered CUSTOMER " + username);
                out.println("SUCCESS: Logged in as CUSTOMER " + username);
                server.getActiveCustomerSessions().put(username, out);

            }
        } else if (clientType.equals("DRIVER")) {
            if (server.getDrivers().containsKey(username)) {
                out.println("ERROR: Username already exists.");
            } else {
                Driver driver = new Driver(username, pass);
                server.addDriver(driver);
                out.println("SUCCESS: Registered DRIVER " + username);
            }
        } else {
            out.println("ERROR: Unknown user type.");
        }
    }

    private void handleRequestRide(String[] tokens) {
        if (!server.hasAvailableDrivers()) {
            out.println("ERROR: No drivers available.");
            return;
        }

        if (clientType == null || !clientType.equals("CUSTOMER")) {
            out.println("ERROR: Only customers can request rides.");
            return;
        }

        // Check if customer already has an active ride
        for (RideInfo r : server.getActiveRides().values()) {
            if (r.getCustomer().equals(username)) {
                out.println("ERROR: You already have an active ride. Cancel it before requesting another.");
                return;
            }
        }

        if (tokens.length < 3) {
            out.println("ERROR: Invalid REQUEST_RIDE format.");
            return;
        }

        String pickup = tokens[1];
        String destination = tokens[2];
        RideInfo ride = new RideInfo(username, pickup, destination);
        server.getActiveRides().put(ride.getRideId(), ride);

        out.println("SUCCESS: Ride requested. Ride ID: " + ride.getRideId());
    }

    private void handleCancelRide(String[] tokens) {
        if (clientType == null || !clientType.equals("CUSTOMER")) {
            out.println("ERROR: Only customers can cancel rides.");
            return;
        }

        RideInfo toRemove = null;
        for (RideInfo r : server.getActiveRides().values()) {
            if (r.getCustomer().equals(username)) {
                if (r.getStatus().equals("ACCEPTED")) {
                    out.println("ERROR: Cannot cancel an accepted ride.");
                    return;
                }
                toRemove = r;
                break;
            }
        }
        if (toRemove == null) {
            out.println("ERROR: No active ride to cancel.");
        } else {
            server.getActiveRides().remove(toRemove.getRideId());
            out.println("SUCCESS: Ride " + toRemove.getRideId() + " canceled.");
        }
    }

    private void handleCheckRide() {
        if (clientType == null || !clientType.equals("CUSTOMER")) {
            out.println("ERROR: Only customers can check rides.");
            return;
        }

        boolean found = false;
        for (RideInfo ride : server.getActiveRides().values()) {
            if (ride.getCustomer().equals(username)) {
                out.println("Ride " + ride.getRideId() + ": " + ride.getStatus() +
                        " | Driver: " + (ride.getDriver() == null ? "None" : ride.getDriver()) +
                        " | Fare: " + ride.getFare());
                found = true;
            }
        }

        if (!found) {
            out.println("No rides found.");
        }
    }

    // When a driver offers a ride, add the offer without immediately accepting the ride.
    private void handleOfferRide(String[] tokens) {
        if (clientType == null || !clientType.equals("DRIVER")) {
            out.println("ERROR: Only drivers can offer rides.");
            return;
        }

        Driver driver = server.getDrivers().get(username);
        if (!driver.isAvailable()) {
            out.println("ERROR: You are already assigned to a ride.");
            return;
        }

        // If just OFFER_RIDE without arguments, show available rides.
        if (tokens.length == 1) {
            if (server.getActiveRides().isEmpty()) {
                out.println("ERROR: No rides available.");
            } else {
                out.println("Available rides:");
                for (RideInfo ride : server.getActiveRides().values()) {
                    if (ride.getStatus().equals("REQUESTED")) {
                        out.println(ride.toString());
                    }
                }
                out.println("END");
            }
            return;
        }

        if (tokens.length < 3) {
            out.println("ERROR: Invalid OFFER_RIDE format.");
            return;
        }

        String rideId = tokens[1];
        double fare;
        try {
            fare = Double.parseDouble(tokens[2]);
        } catch (NumberFormatException ex) {
            out.println("ERROR: Invalid fare.");
            return;
        }

        RideInfo ride = server.getActiveRides().get(rideId);
        if (ride == null) {
            out.println("ERROR: Ride not found.");
        } else if (!ride.getStatus().equals("REQUESTED")) {
            out.println("ERROR: Ride not available for offers.");
        } else {
            // Add the offer to the ride's list of offers
            Offer offer = new Offer(username, fare);
            ride.addOffer(offer);
            out.println("SUCCESS: Offer submitted for ride " + rideId);
            // Notify the customer if online using a special notification format
            PrintWriter custOut = server.getActiveCustomerSessions().get(ride.getCustomer());
            if (custOut != null) {
                custOut.println("\n*** NEW RIDE OFFER ***");
                custOut.println("Your ride " + rideId + " has received an offer from driver " 
                                + username + " with fare " + fare);
            }
        }
    }

    // Allow the customer to view all offers made for their active ride.
    private void handleViewOffers() {
        if (clientType == null || !clientType.equals("CUSTOMER")) {
            out.println("ERROR: Only customers can view offers.");
            return;
        }
        RideInfo ride = null;
        for (RideInfo r : server.getActiveRides().values()) {
            if (r.getCustomer().equals(username)) {
                ride = r;
                break;
            }
        }
        if (ride == null) {
            out.println("ERROR: No active ride found.");
            return;
        }
        if (ride.getOffers().isEmpty()) {
            out.println("No offers available.");
        } else {
            int i = 1;
            for (Offer offer : ride.getOffers()) {
                out.println("Offer " + i + ": Driver " + offer.getDriverUsername() +
                        ", Fare " + offer.getFare());
                i++;
            }
            out.println("END");
        }
    }

    // Allow the customer to accept one of the submitted offers.
    private void handleAcceptOffer(String[] tokens) {
        if (clientType == null || !clientType.equals("CUSTOMER")) {
            out.println("ERROR: Only customers can accept offers.");
            return;
        }
        if (tokens.length < 2) {
            out.println("ERROR: Invalid ACCEPT_OFFER format. Usage: ACCEPT_OFFER <offerNumber>");
            return;
        }
        int offerNumber;
        try {
            offerNumber = Integer.parseInt(tokens[1]);
        } catch (NumberFormatException ex) {
            out.println("ERROR: Offer number must be a valid number.");
            return;
        }
        RideInfo ride = null;
        for (RideInfo r : server.getActiveRides().values()) {
            if (r.getCustomer().equals(username)) {
                ride = r;
                break;
            }
        }
        if (ride == null) {
            out.println("ERROR: No active ride found.");
            return;
        }
        if (offerNumber < 1 || offerNumber > ride.getOffers().size()) {
            out.println("ERROR: Offer number out of range.");
            return;
        }
        Offer acceptedOffer = ride.getOffers().get(offerNumber - 1);
        // Mark ride as accepted with the chosen offer.
        ride.setDriver(acceptedOffer.getDriverUsername());
        ride.setFare(acceptedOffer.getFare());
        ride.setStatus("ACCEPTED");
        // Update driver's status to BUSY.
        Driver driver = server.getDrivers().get(acceptedOffer.getDriverUsername());
        if (driver != null) {
            driver.setStatus("BUSY");
        }
        // Clear all pending offers.
        ride.clearOffers();
        out.println("SUCCESS: Offer accepted. Ride " + ride.getRideId() +
                " is now assigned to driver " + acceptedOffer.getDriverUsername());
        // Optionally, notify the driver if online.
    }

    private void handleUpdateRide(String[] tokens) {
        if (clientType == null || !clientType.equals("DRIVER")) {
            out.println("ERROR: Only drivers can update rides.");
            return;
        }

        if (tokens.length < 3) {
            out.println("ERROR: Invalid UPDATE_RIDE format.");
            return;
        }

        String rideId = tokens[1];
        String newStatus = tokens[2].toUpperCase();

        RideInfo ride = server.getActiveRides().get(rideId);
        if (ride == null) {
            out.println("ERROR: Ride not found.");
        } else if (!username.equals(ride.getDriver())) {
            out.println("ERROR: You are not assigned to this ride.");
        } else {
            if (newStatus.equals("STARTED") || newStatus.equals("FINISHED")) {
                ride.setStatus(newStatus);
                System.out.println("DEBUG: Active customer sessions: " + server.getActiveCustomerSessions().keySet());
                System.out.println("DEBUG: Looking for customer: " + ride.getCustomer());

                // Update notification format to match client's expected format
                PrintWriter custOut = server.getActiveCustomerSessions().get(ride.getCustomer());
                if (custOut != null) {
                    custOut.println("\n*** RIDE STATUS UPDATE ***");
                    custOut.println("Your ride " + rideId + " has been " + newStatus + " by driver " + username);
                    if (newStatus.equals("STARTED")) {
                        custOut.println("Your driver is on the way to your destination.");
                    } else if (newStatus.equals("FINISHED")) {
                        custOut.println("Your ride has been completed. Thank you for using our service!");
                        custOut.println("You can rate your driver using: RATE_DRIVER " + username + " <rating 1-5>");
                    }
                }

                if (newStatus.equals("FINISHED")) {
                    // Update driver status to available when ride is finished.
                    Driver driver = server.getDrivers().get(username);
                    driver.setStatus("AVAILABLE");
                }

                out.println("SUCCESS: Ride " + ride.getRideId() + " updated to " + newStatus);
            } else {
                out.println("ERROR: Invalid status update.");
            }
        }
    }

    private void handleStatistics(String[] tokens) {
        // Only admin (pre-registered as "admin") can view statistics.
        if (!username.equalsIgnoreCase(UberServer.ADMIN_USERNAME)) {
            out.println("ERROR: Only admin can view statistics.");
            return;
        }
        int totalCustomers = server.getCustomers().size();
        int totalDrivers = server.getDrivers().size();

        int requested = 0, accepted = 0, started = 0, finished = 0;
        for (RideInfo ride : server.getActiveRides().values()) {
            switch (ride.getStatus()) {
                case "REQUESTED": requested++; break;
                case "ACCEPTED": accepted++; break;
                case "STARTED": started++; break;
                case "FINISHED": finished++; break;
            }
        }

        String stats = "Overall Statistics:\n" +
                "Total Customers: " + totalCustomers + "\n" +
                "Total Drivers: " + totalDrivers + "\n" +
                "Rides Requested: " + requested + "\n" +
                "Rides Accepted: " + accepted + "\n" +
                "Rides Started: " + started + "\n" +
                "Rides Finished: " + finished;
        out.println(stats);
    }

    private void handleRateDriver(String[] tokens) {
        // Expected format: RATE_DRIVER <driverUsername> <rating>
        if (clientType == null || !clientType.equals("CUSTOMER")) {
            out.println("ERROR: Only customers can rate drivers.");
            return;
        }
        if (tokens.length < 3) {
            out.println("ERROR: Invalid RATE_DRIVER format.");
            return;
        }
        String driverUsername = tokens[1];
        double rating;
        try {
            rating = Double.parseDouble(tokens[2]);
        } catch (NumberFormatException ex) {
            out.println("ERROR: Rating must be a number.");
            return;
        }

        Driver driver = server.getDrivers().get(driverUsername);
        if (driver == null) {
            out.println("ERROR: Driver not found.");
            return;
        }
        driver.addRating(rating);
        out.println("SUCCESS: Rated driver " + driverUsername +
                ". New average rating: " + String.format("%.2f", driver.getAverageRating()));
    }
}
