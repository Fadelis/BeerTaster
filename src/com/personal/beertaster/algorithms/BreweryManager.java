package com.personal.beertaster.algorithms;

import com.personal.beertaster.elements.Brewery;
import com.personal.beertaster.utilities.Converter;

import java.util.*;
import java.util.stream.Collectors;

public class BreweryManager {

    public final static double TRAVEL_DISTANCE = 2000;
    public final static double DEFAULT_LAT = 51.355468, DEFAULT_LONG = 11.100790;
    public final static Brewery ORIGIN = new Brewery(-1, "HOME", "N/A", "N/A", 0, 0);

    private static List<Brewery> breweryList;
    private static Map<Brewery, Integer> breweryMap;
    private static ArrayList<Brewery> possibleBreweries;
    private static double[][] distanceMatrix;

    public static void initialize() {
        breweryList = new ArrayList<>();
        breweryList.add(ORIGIN);
        breweryList.addAll(Converter.readCSV());

        System.out.println("Brewery count " + breweryList.size());
        System.out.println("Beer count " + breweryList.stream().mapToInt(Brewery::getBeerCount).sum());

        distanceMatrix = createDistanceMatrix(breweryList);
        breweryMap = new HashMap<>(breweryList.size());
        for (int i = 0; i < breweryList.size(); i++) {
            breweryMap.put(breweryList.get(i), i);
        }

        setOriginLocation(DEFAULT_LAT, DEFAULT_LONG);
    }

    /**
     * Set new starting location
     */
    public static void setOriginLocation(final double lat, final double lon) {
        ORIGIN.setCoordinates(lat, lon);

        //Update distance matrix for the origin location
        final int size = breweryList.size();

        for (int j = 1; j < size; j++) {
            final double dist = ORIGIN.getDistance(breweryList.get(j));
            distanceMatrix[0][j] = dist;
            distanceMatrix[j][0] = dist;
        }

        updatePossibleBreweries();
    }

    /**
     * Initialize or update list of breweries that are possible to visit with given travel distance
     * It is possible to visit a brewery if the distance to it is less than half the total travel distance
     */
    private static void updatePossibleBreweries() {
        final long start = System.currentTimeMillis();

        if (possibleBreweries == null) possibleBreweries = new ArrayList<>();

        possibleBreweries.clear();
        possibleBreweries.addAll(
                breweryList.stream()
                        .filter(Brewery::containsBeer)
                        .filter(brewery -> distanceToOrigin(brewery) < TRAVEL_DISTANCE / 2)
                        .collect(Collectors.toList())
        );

        final long total = System.currentTimeMillis() - start;
        System.out.println("Possible breweries in " + total + " ms");
    }

    /**
     * Create matrix of distances between each of the breweries
     */
    private static double[][] createDistanceMatrix(final List<Brewery> breweryList) {
        final long start = System.currentTimeMillis();

        final int size = breweryList.size();
        final double[][] distanceMatrix = new double[size][size];

        //Start from 1 because of origin brewery location is at 0 index
        for (int i = 1; i < size; i++) {
            final Brewery iBrewery = breweryList.get(i);
            for (int j = 1; j < size; j++) {
                if (distanceMatrix[i][j] == 0) {
                    final double dist = iBrewery.getDistance(breweryList.get(j));
                    distanceMatrix[i][j] = dist;
                    distanceMatrix[j][i] = dist;
                }
            }
        }

        final long total = System.currentTimeMillis() - start;
        System.out.println("Distance matrix in " + total + " ms");
        return distanceMatrix;
    }

    /**
     * Get distance between two breweries from precalculated distance matrix
     */
    public static double distanceBetween(final Brewery brew1, final Brewery brew2) {
        if (brew1 == null || brew2 == null) return Double.MAX_VALUE;

        return distanceMatrix[breweryMap.get(brew1)][breweryMap.get(brew2)];
    }

    /**
     * Find distance to origin node.
     */
    public static double distanceToOrigin(final Brewery brewery) {
        if (brewery == null) return Double.MAX_VALUE;

        return distanceMatrix[breweryMap.get(ORIGIN)][breweryMap.get(brewery)];
    }

    /**
     * @return Unmodifiable full brewery list
     */
    public static List<Brewery> getBreweryList() {
        return Collections.unmodifiableList(breweryList);
    }

    public static int getBreweryNumber() {
        return breweryList.size();
    }

    /**
     * @return Unmodifiable list of breweries that are possible to visit with the given travel distance
     */
    public static List<Brewery> getPossibleBreweries() {
        return Collections.unmodifiableList(possibleBreweries);
    }
}
