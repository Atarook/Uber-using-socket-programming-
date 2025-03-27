package classes;

public class Offer {
    private String driverUsername;
    private double fare;
    
    public Offer(String driverUsername, double fare) {
        this.driverUsername = driverUsername;
        this.fare = fare;
    }
    
    public String getDriverUsername() {
        return driverUsername;
    }
    
    public double getFare() {
        return fare;
    }
}
