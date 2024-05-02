package edu.uga.cs.ridesharingapp;

public class Ride {
    private String rideId;
    private String userId;
    private String destination;
    private String date;
    private int points;
    private String status; // "open", "accepted", "completed"
    private String acceptedBy; // Optional, only if accepted
    private String confirmedBy_1; // Optional, only if confirmed
    private String confirmedBy_2; // Optional, only if confirmed

    public Ride() {}

    public Ride(String userId, String destination, String date, int points, String status) {
        this.userId = userId;
        this.destination = destination;
        this.date = date;
        this.points = points;
        this.status = status;
    }

    // Getters
    public String getConfirmedBy_1() {
        return confirmedBy_1;
    }

    public String getConfirmedBy_2() {
        return confirmedBy_2;
    }

    public String getRideId() { // Getter for rideId
        return rideId;
    }

    public String getUserId() {
        return userId;
    }

    public String getDestination() {
        return destination;
    }

    public String getDate() {
        return date;
    }

    public int getPoints() {
        return points;
    }

    public String getStatus() {
        return status;
    }

    public String getAcceptedBy() {
        return acceptedBy;
    }

    @Override
    public String toString() {
        // Format the string as you'd like to display in the ListView
        return "Date: " + date + "\nDestination: " + destination + "\nStatus: " + status;
    }

    // Setters
    public void setRideId(String rideId) { 
        this.rideId = rideId; 
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAcceptedBy(String acceptedBy) {
        this.acceptedBy = acceptedBy;
    }
}
