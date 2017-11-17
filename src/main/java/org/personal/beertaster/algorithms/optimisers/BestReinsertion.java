package org.personal.beertaster.algorithms.optimisers;

import static java.util.Comparator.comparing;
import static org.personal.beertaster.main.BreweryManager.*;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;
import org.personal.beertaster.algorithms.Optimiser;
import org.personal.beertaster.elements.Brewery;
import org.personal.beertaster.elements.Tour;

/**
 * @author DATA-DOG Team
 */
public class BestReinsertion implements Optimiser {

  private static final double EPS = 0.0001;

  public Tour multipleOptimization(final Tour tour) {
    Tour optimisedTour = new Tour(tour);

    double lastDistance = 0;
    while (Math.abs(lastDistance - optimisedTour.distance()) < EPS) {
      lastDistance = optimisedTour.distance();
      optimisedTour = optimiseTour(optimisedTour);
    }

    return optimisedTour;
  }

  @Override
  public Tour optimiseTour(final Tour initialSolution) {
    System.out.println(String.format(
        "Initial solution distance: %.1f; beers: %d",
        initialSolution.distance(),
        initialSolution.beerCount()
    ));

    final Tour best = initialSolution.breweries().stream()
        .filter(brewery -> !Objects.equals(ORIGIN, brewery))
        .reduce(
            new Tour(initialSolution),
            (tour, brewery) -> bestInsertion(brewery, tour),
            (tour1, tour2) -> tour1
        );
    final double remainingDistance = TRAVEL_DISTANCE - best.distance();

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
        //.sorted(Comparator.comparing(brew -> brew.getValue() / brew.getKey().beerCount()))
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
        .max(comparing(tour -> tour.beerCount() / tour.distance()))
        .filter(currentTour::isBetterThan)
        .orElse(currentTour);
  }

  @Override
  public String toString() {
    return "Best Reinsertion";
  }
}