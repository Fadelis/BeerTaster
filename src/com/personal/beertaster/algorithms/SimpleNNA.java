/*
Pirma kilusi mintis - paprastas algorimas nuo pradzios iteruojantis per galimus bravorus
pasveriant kiekvieno kelio svori - kiek kilomentru vienai alaus rusiai sunaudojama.
Surandamas maziausias svoris ir keliaujama i si bravora, taip pat patikrinant ar uzteks
kuro grizti namo. Nukeliavus i nauja bravora algoritmas kartojasi, kol nebelieka galimu
bravoru, kuriem uztektu kuro ir alplankyti ir grizti namo.
*/

package com.personal.beertaster.algorithms;

import com.personal.beertaster.elements.Brewery;
import com.personal.beertaster.elements.Tour;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.personal.beertaster.algorithms.BreweryManager.*;
import static java.util.Comparator.comparing;

public class SimpleNNA {

    private static final int NUMBER_OF_FIRST_POINTS = 50;

    public static Tour planSimpleNNA() {
        System.out.println("Total possible breweries - " + getPossibleBreweries().size());

        final Tour initialRoute = new Tour().withBrewery(ORIGIN);

        return getPossibleBreweries().parallelStream()
                //.sorted(comparing(BreweryManager::distanceToOrigin))
                .sorted(comparing(brewery -> distanceToOrigin(brewery) / brewery.getBeerCount()))
                .limit(NUMBER_OF_FIRST_POINTS)
                .map(initialRoute::withBrewery)
                .map(SimpleNNA::fillRoute)
                .map(BestReinsertion::optimiseTour)
                //.map(SimpleNNA::forceOptimize)
                .max(comparing(Tour::beerCount))
                .map(BestReinsertion::multipleOptimization)
                .orElseGet(() -> initialRoute.withBrewery(ORIGIN));
    }

    /**
     * Fill tour using best fitting neighbour, which yields most beer per traveled kilometer.
     */
    private static Tour fillRoute(final Tour initialTour) {
        final Tour solution = new Tour(initialTour);
        final List<Brewery> tempPossibleBreweries = new ArrayList<>(getPossibleBreweries());
        tempPossibleBreweries.removeAll(solution.breweries());
        tempPossibleBreweries.removeIf(brewery -> !solution.possibleToInsert(brewery));

        while (true) {
            final Brewery lastBrewery = solution.lastBrewery();
            final Brewery nextBrewery = tempPossibleBreweries.stream()
                    .max(comparing(brew -> brew.getBeerCount() / distanceBetween(lastBrewery, brew)))
                    .orElse(ORIGIN);

            solution.addBrewery(nextBrewery);
            tempPossibleBreweries.remove(nextBrewery);
            tempPossibleBreweries.removeIf(brewery -> !solution.possibleToInsert(brewery));

            if (Objects.equals(nextBrewery, ORIGIN)) {
                break;
            }
        }

        return solution;
    }
}
