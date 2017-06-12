package com.personal.beertaster.elements;

public class Coordinates {
    private static final double R = 6372.8; // Earth radius in kilometers
    public static final double MIN_LATITUDE = -90.0;
    public static final double MAX_LATITUDE = 90.0;
    public static final double MIN_LONGITUDE = -180.0;
    public static final double MAX_LONGITUDE = 180.0;

    private double latitude, longitude;
    private double x, y;

    public Coordinates() {
    }

    public Coordinates(final double latitude, final double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;

        this.x = getDistance(latitude, 0) * Math.signum(longitude);
        this.y = getDistance(0, longitude) * Math.signum(latitude);
    }

    public double getDistance(final Coordinates other) {
        return getDistance(this, other);
    }

    public double getDistance(final double otherLat, final double otherLon) {
        return getDistance(getLatitude(), getLongitude(), otherLat, otherLon);
    }

    public static double getDistance(final Coordinates coord1, final Coordinates coord2) {
        if (coord1 != null && coord2 != null) {
            return getDistance(coord1.getLatitude(), coord1.getLongitude(), coord2.getLatitude(), coord2.getLongitude());
        }
        return 0;
    }

    public static double getDistance(double lat1, final double lon1, double lat2, final double lon2) {
        final double dLat = Math.toRadians(lat2 - lat1);
        final double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        final double a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
        final double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }

    public void setLatitude(final double value) {
        this.latitude = value;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLongitude(final double value) {
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
        return String.format("%.5f, %.5f", getLatitude(), getLongitude());
    }
}
