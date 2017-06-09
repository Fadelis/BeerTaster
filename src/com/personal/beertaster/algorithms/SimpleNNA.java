/*
Pirma kilusi mintis - paprastas algorimas nuo pradzios iteruojantis per galimus bravorus
pasveriant kiekvieno kelio svori - kiek kilomentru vienai alaus rusiai sunaudojama.
Surandamas maziausias svoris ir keliaujama i si bravora, taip pat patikrinant ar uzteks
kuro grizti namo. Nukeliavus i nauja bravora algoritmas kartojasi, kol nebelieka galimu
bravoru, kuriem uztektu kuro ir alplankyti ir grizti namo.
*/

package com.personal.beertaster.algorithms;

import com.personal.beertaster.elements.Brewery;

import java.util.Objects;

import static com.personal.beertaster.algorithms.BreweryManager.ORIGIN;
import static com.personal.beertaster.algorithms.BreweryManager.TRAVEL_DISTANCE;

public class SimpleNNA {

    public static Tour planSimpleNNA() {
        final Tour tour = new Tour();
        System.out.println("Total possible breweries - " + BreweryManager.getPossibleBreweries().size());

        tour.addBrewery(ORIGIN);
        Brewery currentBrewery = ORIGIN;
        double totalDistance = 0;

        while (totalDistance <= TRAVEL_DISTANCE) {
            currentBrewery = BreweryManager
                    .findBestNeighbour(currentBrewery, tour)
                    .orElse(ORIGIN);

            tour.addBrewery(currentBrewery);
            totalDistance = tour.getDistance();

            if (Objects.equals(currentBrewery, ORIGIN)) {
                break;
            }
        }

        return tour;
    }

}
