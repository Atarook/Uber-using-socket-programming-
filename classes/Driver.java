package classes;
public class Driver extends User {
    private String status; // AVAILABLE, BUSY
    private double totalRating = 0;
    private int ratingCount = 0;
    
    public Driver(String username, String password) {
        super(username, password);
        this.status = "AVAILABLE"; // Default status
    }
    
    // Called when a customer rates the driver
    public void addRating(double rating) {
        totalRating += rating;
        ratingCount++;
    }
    
    // Returns average rating (0 if no rating yet)
    public double getAverageRating() {
        return ratingCount == 0 ? 0 : totalRating / ratingCount;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }

    
    public boolean isAvailable() {

        return "AVAILABLE".equals(status);
    }
    
    @Override
    public String toString() {
        return "Driver: " + username + " | Status: " + status +
               " | Avg Rating: " + String.format("%.2f", getAverageRating());
    }
}