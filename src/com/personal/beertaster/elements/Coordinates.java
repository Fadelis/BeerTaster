package com.personal.beertaster.elements;

public class Coordinates {
    private static final double R = 6372.8; // Earth radius in kilometers

    private double latitude, longitude;
    private double x, y;

    public Coordinates() {
    }

    public Coordinates(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;

        this.x = R * Math.cos(latitude) * Math.cos(longitude);
        this.y = R * Math.cos(latitude) * Math.sin(longitude);
    }

    public double getDistance(Coordinates other) {
        return getDistance(this, other);
    }

    public double getDistance(double otherLat, double otherLon) {
        return getDistance(getLatitude(), getLongitude(), otherLat, otherLon);
    }

    public static double getDistance(Coordinates coord1, Coordinates coord2) {
        if (coord1 != null && coord2 != null) {
            return getDistance(coord1.getLatitude(), coord1.getLongitude(), coord2.getLatitude(), coord2.getLongitude());
        }
        return 0;
    }

    public static double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }

    public void setLatitude(double value) {
        this.latitude = value;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLongitude(double value) {
        this.longitude = value;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    @Override
    public String toString() {
        return String.format("%s, %s", getLatitude(), getLongitude());
    }
}
