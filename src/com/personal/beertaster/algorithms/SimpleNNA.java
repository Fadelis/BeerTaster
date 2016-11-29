/*
Pirma kilusi mintis - paprastas algorimas nuo pradzios iteruojantis per galimus bravorus
pasveriant kiekvieno kelio svori - kiek kilomentru vienai alaus rusiai sunaudojama. 
Surandamas maziausias svoris ir keliaujama i si bravora, taip pat patikrinant ar uzteks
kuro grizti namo. Nukeliavus i nauja bravora algoritmas kartojasi, kol nebelieka galimu
bravoru, kuriem uztektu kuro ir alplankyti ir grizti namo.
*/

package com.personal.beertaster.algorithms;

import java.util.ArrayList;
import java.util.List;

import com.personal.beertaster.elements.Brewery;

public class SimpleNNA {

	public static Tour planSimpleNNA() {
		Tour tour = new Tour();
		List<Brewery> possibleBreweries = new ArrayList<>(BreweryManager.getPossibleBreweries());
		System.out.println("Total possible breweries - "+possibleBreweries.size());
		
		tour.addBrewery(BreweryManager.ORIGIN);
		Brewery currentBrewery = BreweryManager.ORIGIN;

		double totalDistance = 0;
		boolean canTravel = true;
		
		while (canTravel) {
			Brewery nextBrewery = null;
			double bestFit = Integer.MAX_VALUE;
			double nextDist = 0;
			
			for (Brewery brew : possibleBreweries) {
				if (brew != currentBrewery) {
					double dist = BreweryManager.getDistanceBetween(currentBrewery, brew);
					double distToHome = BreweryManager.getDistanceBetween(BreweryManager.ORIGIN, brew);
					double fitCoef = dist / brew.getBeerCount(); //Travel weight - The smaller the better; Zero if starting loc is at brewery or brewery has no beer
					
					//Check if brewery has beer and only then find the smallest travel weight
					//Also check if we have fuel to go back home afterwards
					if (brew.getBeerCount() > 0 && fitCoef < bestFit && totalDistance+dist+distToHome <= BreweryManager.TRAVEL_DISTANCE) {
						nextBrewery = brew;
						bestFit = fitCoef;
						nextDist = dist;
					}
				}
			}
			
			if (nextBrewery != null) { //Found new brewery and enough fuel to go home
				tour.addBrewery(nextBrewery);
				currentBrewery = nextBrewery;
				possibleBreweries.remove(nextBrewery);
				
				totalDistance += nextDist;			
			} else { //No brewery, enough fuel to go home only
				tour.addBrewery(BreweryManager.ORIGIN);
				
				double distToHome = BreweryManager.getDistanceBetween(currentBrewery, BreweryManager.ORIGIN);
				totalDistance += distToHome;
				
				canTravel = false;
			}
		}

		return tour;
	}
	
}
