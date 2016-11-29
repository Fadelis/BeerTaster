/*
 * NEPASITEISINO
 */

package com.personal.beertaster.algorithms;

import java.util.ArrayList;
import java.util.List;

import com.personal.beertaster.elements.Brewery;

public class BruteForce {
	
	private static int counter;

	public static Tour planBruteForce() {
		Tour bestTour = new Tour();
		List<Brewery> possibleBreweries = new ArrayList<>(BreweryManager.getPossibleBreweries());
		possibleBreweries.remove(BreweryManager.ORIGIN);
		counter = 0;
		System.out.println("Total possible breweries - "+possibleBreweries.size());
		
		bestTour.addBrewery(BreweryManager.ORIGIN);
		recursivePlanner(bestTour, bestTour, possibleBreweries);
		
		System.out.println(String.format("Checked %s routes", counter));
		
		
		return bestTour;
	}
	
	private static Tour recursivePlanner(Tour bestTour, Tour currentTour, List<Brewery> possibleBreweries) {
		List<Brewery> tempPossibleBreweries = new ArrayList<>(possibleBreweries);
		Brewery currentBrewery = currentTour.getBrewery(currentTour.tourSize()-1);
		double currentTotalDistance = currentTour.getDistance();
		boolean noNewBreweries = true;
		
		for(int i = 0; i < tempPossibleBreweries.size();) {
			Brewery nextInTour = tempPossibleBreweries.get(i);
			double dist = BreweryManager.getDistanceBetween(currentBrewery, nextInTour);
			double distToHome = BreweryManager.getDistanceBetween(BreweryManager.ORIGIN, nextInTour);
			
			if (currentTotalDistance+dist+distToHome <= BreweryManager.TRAVEL_DISTANCE) {
				noNewBreweries = false;
				Tour newCurrentTour = new Tour(currentTour);
				newCurrentTour.addBrewery(nextInTour);
				tempPossibleBreweries.remove(nextInTour);
				
				recursivePlanner(bestTour, newCurrentTour, tempPossibleBreweries);			
			} else {
				 i++;
			}
		}

		if (noNewBreweries) {
			counter++;
			currentTour.addBrewery(BreweryManager.ORIGIN);

			if (bestTour.getBeerCount() < currentTour.getBeerCount()) {
				bestTour.setTour(currentTour);
				System.out.println(String.format("[%s] Found new best route: %s factories; %s beers; %.1fkm;", counter, currentTour.tourSize()-2, currentTour.getBeerCount(), currentTour.getDistance()));
			}
		}
        
		return bestTour;
	}
}
