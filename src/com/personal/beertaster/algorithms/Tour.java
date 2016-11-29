package com.personal.beertaster.algorithms;
import java.util.ArrayList;
import java.util.Collections;

import com.personal.beertaster.elements.Beer;
import com.personal.beertaster.elements.Brewery;

public class Tour{


    private ArrayList<Brewery> tour = new ArrayList<>();

    private double fitness = 0;
    private double distance = 0;
    private int beerCount = 0;
    

    public Tour(){ }
    
    public Tour(ArrayList<Brewery> tour){
        this.tour = tour;
    }
    
    public Tour(Tour tour){
    	if (tour != null && tour.tour != null)
    		this.tour.addAll(tour.tour);
    }
    
    public void setTour(Tour tour) {
    	if (tour != null && tour.tour != null) {
    		this.tour.clear();
            this.tour.addAll(tour.tour);
            fitness = 0;
            distance = 0;
            beerCount = 0;
    	}
    }

    // Creates a random individual
    public void generateIndividual() {
        // Loop through all our destination cities and add them to our tour
        for (int breweryIndex = 0; breweryIndex < BreweryManager.getBreweryNumber(); breweryIndex++) {
          setBrewery(breweryIndex, BreweryManager.getBrewery(breweryIndex));
        }
        // Randomly reorder the tour
        Collections.shuffle(tour);
    }
    
    public void addBrewery(Brewery brewery) {
    	if (brewery == null) return;
        tour.add(brewery);
        fitness = 0;
        distance = 0;
        beerCount = 0;
    }
    
    public void removeBrewery(Brewery brewery) {
    	if (brewery == null) return;
        tour.remove(brewery);
        fitness = 0;
        distance = 0;
        beerCount = 0;
    }

    public Brewery getBrewery(int tourPosition) {
    	if (tourPosition < 0 || tourPosition >= tourSize()) return null;
        return tour.get(tourPosition);
    }

    // Sets a brewery in a certain position within a tour
    public void setBrewery(int tourPosition, Brewery brewery) {
        tour.set(tourPosition, brewery);
        // If the tours been altered we need to reset the fitness and distance
        fitness = 0;
        distance = 0;
        beerCount = 0;
    }
    
    // Gets the tours fitness
    public double getFitness() {
        if (fitness == 0) {
            fitness = 1/(double)getDistance();
        }
        return fitness;
    }
    
    // Get the total distance of the tour
    public double getDistance(){
        if (distance == 0) {
            double tourDistance = 0;
            for (int breweryIndex=0; breweryIndex < tourSize(); breweryIndex++) {
                // Get brewery we're travelling from
                Brewery fromBrewery = getBrewery(breweryIndex);
                // Brewery we're travelling to
                Brewery destinationBrewery;
                // Check we're not on our tour's last brewery, if we are set our
                // tour's final destination brewery to our starting brewery
                if(breweryIndex+1 < tourSize()){
                    destinationBrewery = getBrewery(breweryIndex+1);
                }
                else{
                    destinationBrewery = getBrewery(0);
                }
                // Get the distance between the two cities
                tourDistance += BreweryManager.getDistanceBetween(fromBrewery, destinationBrewery);
            }
            distance = tourDistance;
        }
        return distance;
    }
    
    //Get total number of beer collected
    public int getBeerCount() {
    	if (beerCount == 0) {
    		beerCount = tour.stream().mapToInt(Brewery::getBeerCount).sum();
    	}
    	return beerCount;
    }

    public int tourSize() {
        return tour.size();
    }
    
    // Check if the tour contains a brewery
    public boolean containsBrewery(Brewery brewery){
        return tour.contains(brewery);
    }
    
    @Override
    public String toString() {
    	if (tour == null) return "";
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("Found %s beer factories", tour.size()-2));
		for (int i = 0; i < tour.size(); i++) {
			double distBetween = 0;
			if (i != 0) distBetween = BreweryManager.getDistanceBetween(getBrewery(i-1), getBrewery(i));
			sb.append(System.lineSeparator());
			sb.append(String.format("\t%s distance %.1fkm", tour.get(i), distBetween));
		}
		sb.append(System.lineSeparator());
		sb.append(String.format("Total distance travelled: %.1fkm", getDistance()));
		sb.append(System.lineSeparator());
		sb.append(System.lineSeparator());
		sb.append(String.format("Collected %s beer types:", tour.stream().mapToInt(Brewery::getBeerCount).sum()));
		for (Brewery brew : tour) {
			if (brew == null || brew.getBeerList() == null) continue;
			for (Beer beer : brew.getBeerList()) {
				sb.append(System.lineSeparator());
				sb.append("\t"+beer.toString());
			}
		}
		sb.append(System.lineSeparator());
		
		return sb.toString();
    }
}
