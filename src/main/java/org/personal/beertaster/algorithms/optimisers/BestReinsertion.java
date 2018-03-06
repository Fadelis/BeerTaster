package org.personal.beertaster.algorithms.optimisers;

import static java.util.Comparator.comparing;
import static org.personal.beertaster.main.BreweryManager.*;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import org.personal.beertaster.algorithms.Optimiser;
import org.personal.beertaster.elements.Brewery;
import org.personal.beertaster.elements.Tour;

/**
 * @author DATA-DOG Team
 */
public class BestReinsertion implements Optimiser {

  private static final double EPS = 0.00001;

  public Tour multipleOptimization(final Tour tour) {
    Tour optimisedTour = new Tour(tour);

    double lastCost = 0;
    while (Math.abs(lastCost - optimisedTour.cost()) > EPS) {
      lastCost = optimisedTour.cost();
      optimisedTour = optimiseTour(optimisedTour);
    }

    return optimisedTour;
  }

  @Override
  public Tour optimiseTour(final Tour initialSolution) {
    final Tour best = initialSolution.breweries().stream()
        .filter(brewery -> !Objects.equals(ORIGIN, brewery))
        .reduce(new Tour(initialSolution), this::bestInsertion, (tour1, tour2) -> tour1);

    final double remainingDistance = TRAVEL_DISTANCE - best.distance();

    final Tour optimised = getPossibleBreweries().stream()
        .filter(brew -> !initialSolution.breweries().contains(brew))
        .map(brew -> new AbstractMap.SimpleEntry<>(
            brew,
            best.breweries().stream()
                .mapToDouble(visitedBrew -> distanceBetween(visitedBrew, brew))
                .min().orElse(TRAVEL_DISTANCE)
        )).filter(brew -> brew.getValue() <= remainingDistance)
        .sorted(comparing(Map.Entry::getValue))
        .map(Map.Entry::getKey)
        .reduce(new Tour(best), this::bestInsertion, (tour1, tour2) -> tour1);

    if (initialSolution.isBetter(optimised)) {
      System.out.println(String.format(
          "SUCCESS: distance: %.1f; Total beer: %d", optimised.distance(), optimised.beerCount()
      ));
    }
    return optimised;
  }

  private Tour bestInsertion(final Tour currentTour, final Brewery brewery) {
    final Tour slicedTour = new Tour(currentTour);
    slicedTour.removeBrewery(brewery);

    return slicedTour.bestInsertion(brewery)
        .map(position -> slicedTour.insertAt(brewery, position))
        .filter(currentTour::isBetter)
        .orElse(currentTour);
  }

  @Override
  public String toString() {
    return "Best Reinsertion";
  }
}
