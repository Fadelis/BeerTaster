package com.personal.beertaster.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.personal.beertaster.elements.Brewery;
import com.personal.beertaster.utilities.Converter;

public class BreweryManager {

	public final static double TRAVEL_DISTANCE = 2000;
	public final static double DEFAULT_LAT = 51.355468, DEFAULT_LONG = 11.100790;
	public final static Brewery ORIGIN = new Brewery(-1, "HOME", "N/A", "N/A", 0, 0);
	
	private static ArrayList<Brewery> breweryList;
	private static ArrayList<Brewery> possibleBreweries;
	private static double[][] distanceMatrix;
	
	public static void initialize() {
		breweryList = new ArrayList<>();
		breweryList.add(ORIGIN);
		breweryList.addAll(Converter.readCSV());
		
		System.out.println("Brewery count "+breweryList.size());
		System.out.println("Beer count "+breweryList.stream().mapToInt(Brewery::getBeerCount).sum());
	
		distanceMatrix = createDistanceMatrix(breweryList);
		
		setOriginLocation(DEFAULT_LAT, DEFAULT_LONG);
	}

	/**
	 * Set new starting location
	 */
	public static void setOriginLocation(double lat, double lon) {
		if (ORIGIN != null && distanceMatrix != null) {
			ORIGIN.setCoordinates(lat, lon);
			
			//Update distance matrix for the origin location
			int size = breweryList.size();
			
			for (int j = 1; j < size; j++) {
				double dist = ORIGIN.getDistance(breweryList.get(j));
				distanceMatrix[0][j] = dist;
				distanceMatrix[j][0] = dist;
			}
			
			updatePossibleBreweries();
		}
	}
	
	/**
	 * Initialize or update list of breweries that are possible to visit with given travel distance
	 * It is possible to visit a brewery if the distance to it is less than half the total travel distance
	 */
	private static void updatePossibleBreweries() {
		long start = System.currentTimeMillis();
		
		if (possibleBreweries == null) possibleBreweries = new ArrayList<>();
		
//		List<Brewery> possibleBreweries = breweryList.stream().filter(brewery -> brewery.getDistance(ORIGIN) < TRAVEL_DISTANCE/2).collect(Collectors.toList());
		
		possibleBreweries.clear();
		int size = breweryList.size();
		for (int j = 0; j < size; j++) {
			//Dividing by 2 because we need to go back after the visit
			if (distanceMatrix[0][j] < TRAVEL_DISTANCE/2) {
				possibleBreweries.add(breweryList.get(j));
			}
		}
		
		long total = System.currentTimeMillis() - start;
		System.out.println("Possible breweries in "+total+" ms");
	}
	
	/**
	 * Create matrix of distances between each of the breweries
	 */
	private static double[][] createDistanceMatrix(List<Brewery> breweryList) {
		long start = System.currentTimeMillis();
		
		int size = breweryList.size();
		double[][] distanceMatrix = new double[size][size];
		
		//Start from 1 because of origin brewery location is at 0 index
		for (int i = 1; i < size; i++) {
			Brewery iBrewery = breweryList.get(i);
			for (int j = 1; j < size; j++) {
				if (distanceMatrix[i][j] == 0) {
					double dist = iBrewery.getDistance(breweryList.get(j));
					distanceMatrix[i][j] = dist;
					distanceMatrix[j][i] = dist;
				}
			}
		}
		
		long total = System.currentTimeMillis() - start;
		System.out.println("Distance matrix in "+total+" ms");
		return distanceMatrix;
	}
	
	/**
	 * Get distance between two breweries from precalculated distance matrix
	 */
	public static double getDistanceBetween(Brewery brew1, Brewery brew2) {
		if (breweryList == null || distanceMatrix == null || brew1 == null || brew2 == null) return Double.MAX_VALUE;
		
		int index1 = breweryList.indexOf(brew1);
		int index2 = breweryList.indexOf(brew2);
		
		return distanceMatrix[index1][index2];
	}
	
	/**
	 * @return Unmodifiable full brewery list
	 */
	public static List<Brewery> getBreweryList() {
		return Collections.unmodifiableList(breweryList);
	}
	
	public static Brewery getBrewery(int index) {
		if (index >= getBreweryNumber()) return null;
		return breweryList.get(index);
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
