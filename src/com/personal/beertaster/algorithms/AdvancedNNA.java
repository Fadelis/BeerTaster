/*
NEPASITEISINO
Antra kilusi mintis - modifikuoti pirma algoritma taip, kad suradus geriausia
tolimesni bravora patikrinti ar nera geresnio varianto isnaudoti distancijai iki sio bravoro.

Pvz. turime bravora kuris yra uz 200km ir jame yra 10 alaus rusiu, taciau salia yra trys bravorai
vienas nuo kito issideste kas 40 km, kurie turi po 4 alaus rusys. Aplanke siuos tris bravorus
nukeliautume 160km ir surinktume 12 rusiu alaus.
*/

package com.personal.beertaster.algorithms;

import com.personal.beertaster.elements.Brewery;
import com.personal.beertaster.elements.Tour;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.personal.beertaster.algorithms.BreweryManager.*;
import static java.util.Comparator.comparing;

public class AdvancedNNA {

    private static final int LOOK_AHEAD = 2;

    public static Tour planAdvancedNNA() {
        final List<Brewery> possibleBreweries = BreweryManager.getPossibleBreweries().stream()
                .filter(Brewery::containsBeer)
                .collect(Collectors.toList());
        System.out.println("Total possible breweries - " + possibleBreweries.size());

        final Tour tour = new Tour();
        tour.addBrewery(ORIGIN);

        while (true) {
            final Brewery bestCandidate = possibleBreweries.stream()
                    .map(brewery -> new SimpleEntry<>(brewery, lookAheadTour(brewery, tour)))
                    .filter(entry -> Objects.nonNull(entry.getValue()))
                    .min(comparing(entry -> entry.getValue().getDistance() / entry.getValue().beerCount()))
                    .map(SimpleEntry::getKey)
                    .orElse(ORIGIN);

            tour.addBrewery(bestCandidate);
            possibleBreweries.remove(bestCandidate);

            if (Objects.equals(bestCandidate, ORIGIN)) {
                break;
            }
        }

        return tour;
    }

    private static Tour lookAheadTour(final Brewery startBrewery, final Tour currentTour) {
        if (!currentTour.possibleToInsert(startBrewery)) {
            return null;
        }

        final Tour lookAheadTour = new Tour(currentTour);
        lookAheadTour.addBrewery(startBrewery);

        Brewery currentBrewery = startBrewery;
        while (lookAheadTour.breweries().size() < currentTour.tourSize() + LOOK_AHEAD + 1) {
            final Optional<Brewery> maybeBrewery = findBestNeighbour(currentBrewery, lookAheadTour);

            if (maybeBrewery.isPresent()) {
                lookAheadTour.addBrewery(maybeBrewery.get());
                currentBrewery = maybeBrewery.get();
            } else {
                break;
            }
        }

        return lookAheadTour;
    }

    /**
     * Find best fitting neighbour, which yields most beer per traveled kilometer.
     */
    private static Optional<Brewery> findBestNeighbour(
            final Brewery brewery,
            final Tour currentTour
    ) {
        if (brewery == null) {
            return Optional.empty();
        }

        return getPossibleBreweries().stream()
                .filter(brew -> !currentTour.breweries().contains(brew))
                .filter(currentTour::possibleToInsert)
                .max(comparing(brew -> brew.getBeerCount() / distanceBetween(brewery, brew)));
    }
}
