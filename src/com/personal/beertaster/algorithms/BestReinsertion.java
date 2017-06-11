package com.personal.beertaster.algorithms;

import com.personal.beertaster.elements.Brewery;
import com.personal.beertaster.elements.Tour;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

import static com.personal.beertaster.algorithms.BreweryManager.*;
import static java.util.Comparator.comparing;

/**
 * @author DATA-DOG Team
 */
public class BestReinsertion {

    public static Tour optimiseTour(final Tour initialSolution) {
        System.out.println(String.format(
                "Initial solution distance: %.1f; beers: %d",
                initialSolution.getDistance(),
                initialSolution.getBeerCount()
        ));

        final Tour best = initialSolution.breweries().stream()
                .filter(brewery -> !Objects.equals(ORIGIN, brewery))
                .reduce(
                        new Tour(initialSolution),
                        (tour, brewery) -> bestInsertion(brewery, tour),
                        (tour1, tour2) -> tour1
                );
        final double remainingDistance = TRAVEL_DISTANCE - best.getDistance();

        return getPossibleBreweries().stream()
                .filter(brew -> !initialSolution.breweries().contains(brew))
                .map(brew -> new AbstractMap.SimpleEntry<>(
                        brew,
                        best.breweries().stream()
                                .mapToDouble(visitedBrew -> distanceBetween(visitedBrew, brew))
                                .min().orElse(TRAVEL_DISTANCE)
                        //.sorted().limit(2).sum()
                )).filter(brew -> brew.getValue() <= remainingDistance)
                .sorted(comparing(Map.Entry::getValue))
                //.sorted(Comparator.comparing(brew -> brew.getValue() / brew.getKey().getBeerCount()))
                .map(Map.Entry::getKey)
                .reduce(
                        new Tour(best),
                        (tour, brewery) -> bestInsertion(brewery, tour),
                        (tour1, tour2) -> tour1
                );
    }

    private static Tour bestInsertion(
            final Brewery brewery,
            final Tour currentTour
    ) {
        final Tour slicedTour = new Tour(currentTour);
        slicedTour.removeBrewery(brewery);

        return IntStream.range(1, slicedTour.breweries().size() - 1)
                .mapToObj(id -> slicedTour.insertAt(id, brewery))
                .max(comparing(tour -> tour.getBeerCount() / tour.getDistance()))
                .filter(currentTour::isBetterThan)
                .orElse(currentTour);
    }
}
