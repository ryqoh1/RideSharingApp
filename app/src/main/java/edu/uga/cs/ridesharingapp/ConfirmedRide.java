package edu.uga.cs.ridesharingapp;

public class ConfirmedRide {
    private String driverId;
    private String riderId;
    private String date;
    private int points;
    private String destination;

    // Default constructor required for Firebase
    public ConfirmedRide() {
    }

    // Constructor with all fields
    public ConfirmedRide(String driverId, String riderId, String date, int points, String destination) {
        this.driverId = driverId;
        this.riderId = riderId;
        this.date = date;
        this.points = points;
        this.destination = destination;
    }

    // Getters
    public String getDriverId() {
        return driverId;
    }

    public String getRiderId() {
        return riderId;
    }

    public String getDate() {
        return date;
    }

    public int getPoints() {
        return points;
    }

    public String getDestination() {
        return destination;
    }

    // Setters
    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public void setRiderId(String riderId) {
        this.riderId = riderId;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
