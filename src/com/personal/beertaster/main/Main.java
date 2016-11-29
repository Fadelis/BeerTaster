package com.personal.beertaster.main;

import java.util.Scanner;

import com.personal.beertaster.algorithms.BreweryManager;
import com.personal.beertaster.algorithms.SimpleNNA;
import com.personal.beertaster.algorithms.Tour;
import com.personal.beertaster.utilities.Converter;

public class Main {	

	public static void main(String[] args) {
		BreweryManager.initialize();
		
		planRoute();
		
//		setOriginLocation(DEFAULT_LAT, DEFAULT_LON);
//		setOriginLocation(51.742503, 19.432956);
		
		Scanner scanner = new Scanner(System.in);//--lat=51.742503 --lon=19.432956		
		while (true) {
			double oldLat = BreweryManager.ORIGIN.getCoordinates().getLatitude();
			double oldLon = BreweryManager.ORIGIN.getCoordinates().getLongitude();
			double newLat = oldLat;
			double newLon = oldLon;

			String userInput = scanner.nextLine();
			String[] values = userInput.split("--");
			
			if (values != null) {
				for (int i = 0; i < values.length; i++) {
					if (values[i].contains("lat")) {
						newLat = Converter.parseDouble(values[i]);
					} else if (values[i].contains("lon")) {
						newLon = Converter.parseDouble(values[i]);
					}
				}
			}

			if (oldLat != newLat || oldLon != newLon) {
				setOriginLocation(newLat, newLon);
			}
		}
	}

	private static void planRoute() {
		long start = System.currentTimeMillis();
		
		Tour tour = SimpleNNA.planSimpleNNA();
		//Tour tour = AdvancedNNA.planAdvancedNNA();
		//Tour tour = BruteForce.planBruteForce();
		
		System.out.println(tour.toString());
		
		long total = System.currentTimeMillis() - start;
		System.out.println("Calculated in "+total+" ms");
		
		System.out.println(getHelpText());
	}
	
	
	/**
	 * Set new starting location
	 */
	private static void setOriginLocation(double lat, double lon) {
		BreweryManager.setOriginLocation(lat, lon);
		
		planRoute();
	}

	
	private static String getHelpText() {
		StringBuilder sb = new StringBuilder();
		sb.append(System.lineSeparator());
		sb.append("To enter new starting location use this format:");
		sb.append(System.lineSeparator());
		sb.append("--lat=51.355468 --long=11.100790");
		
		return sb.toString();
	}
}
