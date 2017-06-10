package com.personal.beertaster.algorithms;

import com.personal.beertaster.elements.Brewery;
import com.personal.beertaster.elements.Coordinates;

import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.summarizingDouble;

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

        Tour best = initialSolution.breweries().stream()
                .filter(brewery -> !Objects.equals(BreweryManager.ORIGIN, brewery))
                .reduce(
                        new Tour(initialSolution),
                        (tour, brewery) -> bestInsertion(brewery, tour),
                        (tour1, tour2) -> tour1
                );

        final DoubleSummaryStatistics summaryLat = best.breweries().stream()
                .map(Brewery::getCoordinates)
                .collect(summarizingDouble(Coordinates::getLatitude));
        final DoubleSummaryStatistics summaryLng = best.breweries().stream()
                .map(Brewery::getCoordinates)
                .collect(summarizingDouble(Coordinates::getLongitude));
        final Predicate<Brewery> coordinatesFits = brewery -> {
            final double lat = brewery.getCoordinates().getLatitude();
            final double lng = brewery.getCoordinates().getLongitude();

            return lat < summaryLat.getMax() && lat > summaryLat.getMin() &&
                    lng < summaryLng.getMax() && lng > summaryLng.getMin();
        };

        best = BreweryManager.getPossibleBreweries().stream()
                .filter(brew -> !initialSolution.breweries().contains(brew))
                //.filter(coordinatesFits)
                .sorted(Comparator.comparing(Brewery::getBeerCount))
                .reduce(
                        new Tour(best),
                        (tour, brewery) -> bestInsertion(brewery, tour),
                        (tour1, tour2) -> tour1
                );

        return best;
    }

    private static Tour bestInsertion(
            final Brewery brewery,
            final Tour currentTour
    ) {
        final Tour slicedTour = new Tour(currentTour);
        slicedTour.removeBrewery(brewery);

        return IntStream.range(1, slicedTour.breweries().size() - 1)
                .mapToObj(id -> slicedTour.insertAt(id, brewery))
                .max(Comparator.comparing(tour -> tour.getBeerCount() / tour.getDistance()))
                .filter(currentTour::isBetterThan)
                .orElse(currentTour);
    }
}
