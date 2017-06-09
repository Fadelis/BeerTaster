package com.personal.beertaster.algorithms;

import com.personal.beertaster.elements.Brewery;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static com.personal.beertaster.algorithms.BreweryManager.ORIGIN;
import static com.personal.beertaster.algorithms.BreweryManager.distanceBetween;

public class Tour {


    private final List<Brewery> breweries = new ArrayList<>();
    private double distance = 0;
    private int beerCount = 0;


    public Tour() {
    }

    public Tour(final Tour tour) {
        if (tour != null && tour.breweries != null)
            this.breweries.addAll(tour.breweries);
    }

    public Tour withBrewery(final Brewery brewery) {
        final Tour tour = new Tour(this);
        tour.addBrewery(brewery);
        return tour;
    }

    public void setTour(final Tour tour) {
        if (tour != null && tour.breweries != null) {
            this.breweries.clear();
            this.breweries.addAll(tour.breweries);
            distance = 0;
            beerCount = 0;
        }
    }

    public void addBrewery(final Brewery brewery) {
        if (brewery == null) return;
        breweries.add(brewery);
        distance = 0;
        beerCount = 0;
    }

    public void removeBrewery(final Brewery brewery) {
        if (brewery == null) return;
        breweries.remove(brewery);
        distance = 0;
        beerCount = 0;
    }

    public Brewery getBrewery(final int tourPosition) {
        if (tourPosition < 0 || tourPosition >= tourSize()) return null;
        return breweries.get(tourPosition);
    }

    // Sets a brewery in a certain position within a tour
    public void setBrewery(final int tourPosition, final Brewery brewery) {
        breweries.set(tourPosition, brewery);
        // If the tours been altered we need to reset the fitness and distance
        distance = 0;
        beerCount = 0;
    }

    public List<Brewery> breweries() { return breweries; }

    // Get the total distance of the tour
    public double getDistance() {
        if (distance == 0) {
            distance = IntStream.range(1, breweries.size())
                    .mapToDouble(i -> distanceBetween(breweries.get(i - 1), breweries.get(i))).sum();
        }
        return distance;
    }

    //Get total number of beer collected
    public int getBeerCount() {
        if (beerCount == 0) {
            beerCount = breweries.stream().mapToInt(Brewery::getBeerCount).sum();
        }
        return beerCount;
    }

    public int tourSize() {
        return breweries.size();
    }

    public boolean possibleToInsert(final Brewery brewery) {
        if (brewery == null) {
            return false;
        }

        final Brewery lastBrewery = breweries.get(breweries.size() - 1);
        final double totalDistance = getDistance() +
                distanceBetween(lastBrewery, brewery) +
                distanceBetween(ORIGIN, brewery);

        return totalDistance <= BreweryManager.TRAVEL_DISTANCE;
    }

    @Override
    public String toString() {
        if (breweries == null) return "";
        final StringBuilder sb = new StringBuilder();
        sb.append(String.format("Found %s beer factories", breweries.size() - 2));
        for (int i = 0; i < breweries.size(); i++) {
            double distBetween = 0;
            if (i != 0) distBetween = distanceBetween(getBrewery(i - 1), getBrewery(i));
            sb.append(System.lineSeparator())
                    .append(String.format("\t%s distance %.1fkm", breweries.get(i), distBetween));
        }
        sb.append(System.lineSeparator());
        sb.append(String.format("Total distance travelled: %.1fkm", getDistance()));
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(String.format("Collected %s beer types:", breweries.stream().mapToInt(Brewery::getBeerCount).sum()));
        breweries.stream()
                .map(Brewery::getBeerList)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .forEach(beer -> sb.append(System.lineSeparator()).append("\t").append(beer.toString()));
        sb.append(System.lineSeparator());

        return sb.toString();
    }
}
