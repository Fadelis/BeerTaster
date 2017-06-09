/*
 * NEPASITEISINO
 */

package com.personal.beertaster.algorithms;

import com.personal.beertaster.elements.Brewery;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.personal.beertaster.algorithms.BreweryManager.*;
import static java.util.Comparator.comparing;

public class BruteForce {

    private static final int BREWERIES_COUNT_CHECK = 2;
    private static int counter;

    public static Tour planBruteForce() {
        final List<Brewery> possibleBreweries = getPossibleBreweries().stream()
                .filter(Brewery::containsBeer)
                .collect(Collectors.toList());
        System.out.println("Total possible breweries - " + possibleBreweries.size());

        final Tour bestTour = new Tour();
        bestTour.addBrewery(ORIGIN);

        recursivePlanner(bestTour, bestTour, possibleBreweries);

        System.out.println(String.format("Checked %s routes", counter));


        return bestTour;
    }

    private static Tour recursivePlanner(final Tour bestTour, final Tour currentTour, final List<Brewery> possibleBreweries) {
        final Brewery currentBrewery = currentTour.getBrewery(currentTour.tourSize() - 1);

        final Tour possibleBestTour = possibleBreweries.stream()
                .filter(brewery -> !currentTour.breweries().contains(brewery))
                .filter(currentTour::possibleToInsert)
                .sorted(comparing(brewery -> distanceBetween(brewery, currentBrewery) / brewery.getBeerCount()))
                .limit(BREWERIES_COUNT_CHECK)
                .map(brewery -> recursivePlanner(
                        bestTour,
                        currentTour.withBrewery(brewery),
                        without(brewery, possibleBreweries)
                )).max(Comparator.comparing(Tour::getBeerCount))
                .orElse(currentTour);
        possibleBestTour.addBrewery(ORIGIN);
        counter++;

        if (bestTour.getBeerCount() < possibleBestTour.getBeerCount()) {
            bestTour.setTour(possibleBestTour);
            System.out.println(String.format(
                    "[%s] Found new best route: %s factories; %s beers; %.1fkm;",
                    counter,
                    possibleBestTour.tourSize() - 2,
                    possibleBestTour.getBeerCount(),
                    possibleBestTour.getDistance()
            ));
        }

        return possibleBestTour;
    }

    private static List<Brewery> without(final Brewery brewery, final List<Brewery> list) {
        final List<Brewery> newList = new ArrayList<>(list);
        newList.remove(brewery);

        return newList;
    }
}
