/*
NEPASITEISINO
Antra kilusi mintis - modifikuoti pirma algoritma taip, kad suradus geriausia
tolimesni bravora patikrinti ar nera geresnio varianto isnaudoti distancijai iki sio bravoro.

Pvz. turime bravora kuris yra uz 200km ir jame yra 10 alaus rusiu, taciau salia yra trys bravorai
vienas nuo kito issideste kas 40 km, kurie turi po 4 alaus rusys. Aplanke siuos tris bravorus
nukeliautume 160km ir surinktume 12 rusiu alaus.
*/

package com.personal.beertaster.algorithms;

import java.util.ArrayList;
import java.util.List;

import com.personal.beertaster.elements.Brewery;

public class AdvancedNNA {

	public static Tour planAdvancedNNA() {
		Tour tour = new Tour();
		List<Brewery> possibleBreweries = new ArrayList<>(BreweryManager.getPossibleBreweries());
		System.out.println("Total possible breweries - "+possibleBreweries.size());
		
		tour.addBrewery(BreweryManager.ORIGIN);
		Brewery currentBrewery = BreweryManager.ORIGIN;

		double totalDistance = 0;
		boolean canTravel = true;
		
		while (canTravel) {
			Brewery nextBrewery = null;
			double fittest = Integer.MAX_VALUE;
			double nextDist = 0;
			
			for (Brewery brew : possibleBreweries) {
				if (brew != currentBrewery) {
					double dist = BreweryManager.getDistanceBetween(currentBrewery, brew);
					double distToHome = BreweryManager.getDistanceBetween(BreweryManager.ORIGIN, brew);
					double fitCoef = dist / brew.getBeerCount(); //Travel weight - The smaller the better; Zero if starting loc is at brewery or brewery has no beer
					
					//Check if brewery has beer and only then find the smallest travel weight
					//Also check if we have fuel to go back home afterwards
					if (brew.getBeerCount() > 0 && fitCoef < fittest && totalDistance+dist+distToHome <= BreweryManager.TRAVEL_DISTANCE) {
						nextBrewery = brew;
						fittest = fitCoef;
						nextDist = dist;
					}
				}
			}
			
			if (nextBrewery != null) { //Found new brewery and enough fuel to go home
				Tour betterTour = null;
				for (Brewery brew : possibleBreweries) {
					if (brew != nextBrewery && brew != currentBrewery) {
						Tour testTour = checkDistanceEfficiency(brew, nextBrewery, nextDist, totalDistance, currentBrewery, possibleBreweries);
						if (testTour != null) {
							if (betterTour == null || betterTour.getBeerCount() < testTour.getBeerCount()) {
								betterTour = testTour;
							}
						}
					}
				}
				
				//Check if found tour collected more beer than the best fit brewery
				if (betterTour != null && betterTour.getBeerCount() > nextBrewery.getBeerCount()) {
					System.out.println(String.format("Found beter tour: %s beers instead of %s beers", betterTour.getBeerCount(), nextBrewery.getBeerCount()));
					nextBrewery = betterTour.getBrewery(0);
					
					nextDist = BreweryManager.getDistanceBetween(currentBrewery, nextBrewery);
				}
				
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
	
	/**
	 * Check if selected best brewery distance is efficiently used
	 * or maybe it is possible to use the same distance to collect more beer
	 */
	private static Tour checkDistanceEfficiency(Brewery breweryToTest, Brewery breweryToBeat,double distanceToBeat, double currentTotalDistance, Brewery tourCurrentBrewery, List<Brewery> possibleBreweries) {
		Brewery currentBrewery = breweryToTest;
		double totalDistance = BreweryManager.getDistanceBetween(tourCurrentBrewery, breweryToTest);
		if (totalDistance > distanceToBeat) return null;
		
		Tour possibleTour = new Tour();
		possibleTour.addBrewery(breweryToTest);
		List<Brewery> possibleBreweriesTemp = new ArrayList<>(possibleBreweries);
		
		
		boolean canTravel = true;
		
		while (canTravel) {
			Brewery nextBrewery = null;
			double fittest = Integer.MAX_VALUE;
			double nextDist = 0;
			
			for (Brewery brew : possibleBreweriesTemp) {
				if (brew != currentBrewery ) {
					double dist = BreweryManager.getDistanceBetween(currentBrewery, brew);
					double distToHome = BreweryManager.getDistanceBetween(BreweryManager.ORIGIN, brew);
					double fitCoef = dist / brew.getBeerCount(); //Travel weight - The smaller the better; Zero if starting loc is at brewery or brewery has no beer
					
					//Check if brewery has beer and only then find the smallest travel weight
					//Also check if we have fuel to go back home afterwards
					if (brew.getBeerCount() > 0 && fitCoef < fittest && totalDistance+dist <= distanceToBeat 
							&& currentTotalDistance+totalDistance+dist+distToHome < BreweryManager.TRAVEL_DISTANCE) {
						nextBrewery = brew;
						fittest = fitCoef;
						nextDist = dist;
					}
				}
			}
			
			if (nextBrewery != null) { //Found new brewery and enough fuel to go home
				possibleTour.addBrewery(nextBrewery);
				currentBrewery = nextBrewery;
				possibleBreweriesTemp.remove(nextBrewery);
				
				totalDistance += nextDist;			
			} else { //Reached limit of possible distance to beat
				if (breweryToBeat.getBeerCount() < possibleTour.getBeerCount())
					return possibleTour;
				canTravel = false;
			}
		}
		
		return null;
	}
}
