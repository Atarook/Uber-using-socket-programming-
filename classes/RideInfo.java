package classes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RideInfo {
    private String rideId;
    private String customer;
    private String pickup;
    private String destination;
    private String driver;
    private double fare;
    private String status; // "REQUESTED", "ACCEPTED", "STARTED", "FINISHED"
    private List<Offer> offers;
    
    public RideInfo(String customer, String pickup, String destination) {
        this.rideId = UUID.randomUUID().toString();
        this.customer = customer;
        this.pickup = pickup;
        this.destination = destination;
        this.status = "REQUESTED";
        this.offers = new ArrayList<>();
    }
    
    public String getRideId() {
        return rideId;
    }
    
    public String getCustomer() {
        return customer;
    }
    
    public String getPickup() {
        return pickup;
    }
    
    public String getDestination() {
        return destination;
    }
    
    public String getDriver() {
        return driver;
    }
    
    public double getFare() {
        return fare;
    }
    
    public String getStatus() {
        return status;
    }
    
    public List<Offer> getOffers() {
        return offers;
    }
    
    public void setDriver(String driver) {
        this.driver = driver;
    }
    
    public void setFare(double fare) {
        this.fare = fare;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public void addOffer(Offer offer) {
        offers.add(offer);
    }
    
    public void clearOffers() {
        offers.clear();
    }
    
    @Override
    public String toString() {
        return "RideID: " + rideId + ", Customer: " + customer +
               ", Pickup: " + pickup + ", Destination: " + destination +
               ", Status: " + status;
    }
}
