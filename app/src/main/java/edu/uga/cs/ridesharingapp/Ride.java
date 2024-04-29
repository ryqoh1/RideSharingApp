package edu.uga.cs.ridesharingapp;

public class Ride {
    private String userId;
    private String destination;
    private String date;
    private int points;
    private String status; // "open", "accepted", "completed"
    private String acceptedBy; // Optional, only if accepted

    public Ride() {}

    public Ride(String userId, String destination, String date, int points, String status) {
        this.userId = userId;
        this.destination = destination;
        this.date = date;
        this.points = points;
        this.status = status;
    }

    // Getters
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

    // Setters
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
