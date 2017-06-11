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

import java.util.Objects;
import java.util.Optional;

import static com.personal.beertaster.algorithms.BreweryManager.*;
import static java.util.Comparator.comparing;

public class SimpleNNA {

    public static Tour planSimpleNNA() {
        System.out.println("Total possible breweries - " + getPossibleBreweries().size());

        final Tour tour = new Tour();
        tour.addBrewery(ORIGIN);
        Brewery currentBrewery = ORIGIN;

        while (true) {
            currentBrewery = findBestNeighbour(currentBrewery, tour).orElse(ORIGIN);
            tour.addBrewery(currentBrewery);

            if (Objects.equals(currentBrewery, ORIGIN)) {
                break;
            }
        }

        return tour;
    }

    /**
     * Find best fitting neighbour, which yields most beer per traveled kilometer.
     */
    public static Optional<Brewery> findBestNeighbour(
            final Brewery brewery,
            final Tour currentTour
    ) {
        if (brewery == null) {
            return Optional.empty();
        }

        return getPossibleBreweries().stream()
                .filter(Brewery::containsBeer)
                .filter(brew -> !currentTour.breweries().contains(brew))
                .filter(currentTour::possibleToInsert)
                .max(comparing(brew -> brew.getBeerCount() / distanceBetween(brewery, brew)));
    }
}
