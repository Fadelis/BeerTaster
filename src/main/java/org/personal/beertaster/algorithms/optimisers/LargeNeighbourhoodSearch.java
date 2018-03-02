package org.personal.beertaster.algorithms.optimisers;

import static org.personal.beertaster.main.BreweryManager.*;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import org.personal.beertaster.algorithms.Optimiser;
import org.personal.beertaster.elements.Brewery;
import org.personal.beertaster.elements.Tour;

/**
 * @author DATA-DOG Team
 */
public class LargeNeighbourhoodSearch implements Optimiser {

  private static final float REMOVAL_PERCENTAGE = 15F;
  private static final int DEFAULT_RUNTIME_MILLIS = 1000;
  private static final double ALLOWED_COST_DECREASE = 10D;

  private double allowedDecrease = ALLOWED_COST_DECREASE;

  @Override
  public Tour optimiseTour(final Tour initialTour) {
    final Instant until = Instant.now().plusMillis(DEFAULT_RUNTIME_MILLIS);
    allowedDecrease = ALLOWED_COST_DECREASE;

    int iterations = 0;
    Tour best = new Tour(initialTour);
    Tour iteration = new Tour(initialTour);

    while (until.isAfter(Instant.now())) {
      iteration = singleIteration(best);
//      System.out.println(String.format(
//          "OPTIMISED: distance: %.1f; Total beer: %d", iteration.distance(), iteration.beerCount()
//      ));

//      if (optimised.beerCount() * (allowedDecrease / 100) > iteration.beerCount()) {
//        iteration = new Tour(optimised);
//        System.out.println(String.format(
//            "ITERATION: distance: %.1f; Total beer: %d", iteration.distance(), iteration.beerCount()
//        ));
//      }

      if (best.isBetter(iteration)) {
        best = new Tour(iteration);
        System.out.println(String.format(
            "SUCCESS: distance: %.1f; Total beer: %d", best.distance(), best.beerCount()
        ));
      }
      final long left = Duration.between(Instant.now(), until).toMillis();
      allowedDecrease = (double) left / DEFAULT_RUNTIME_MILLIS * ALLOWED_COST_DECREASE;
      iterations++;
    }

    System.out.println(String.format(
        "Performed %d iterations in %s",
        iterations,
        Duration.ofMillis(DEFAULT_RUNTIME_MILLIS)
    ));
    return best;
  }

  private Tour singleIteration(final Tour original) {
    // remove defined percentage of breweries from tour
    final Tour iterationTour = new Tour(original);
    final int toRemove = Math.round(iterationTour.breweriesCount() * (REMOVAL_PERCENTAGE / 100));
    ThreadLocalRandom.current()
        .ints(1, iterationTour.tourSize() - 1)
        .distinct()
        .limit(toRemove)
        .mapToObj(iterationTour::getBrewery)
        .collect(Collectors.toList())
        .forEach(iterationTour::removeBrewery);

    // possible new breweries to visit
    final List<Brewery> breweries = new ArrayList<>(getPossibleBreweries());
    breweries.removeAll(iterationTour.breweries());
    Collections.shuffle(breweries);

    return breweries.stream()
        .reduce(
            new Tour(iterationTour),
            this::bestInsertion,
            (tour1, tour2) -> tour1
        );
  }

  private Tour bestInsertion(final Tour initialTour, final Brewery brewery) {
    if (Math.abs(initialTour.distance() - TRAVEL_DISTANCE) < 0.001) {
      return initialTour;
    }

    Integer bestPosition = null;
    double bestCost = 0;
    double distFrom = distanceToOrigin(brewery);
    for (int i = 1; i < initialTour.tourSize() - 1; i++) {
      final double distTo = distanceBetween(brewery, initialTour.getBrewery(i));
      final double oldFrom = distanceBetween(initialTour.getBrewery(i - 1),
          initialTour.getBrewery(i));
      final double totalDistance = initialTour.distance() + distFrom + distTo - oldFrom;
      distFrom = distTo;

      if (totalDistance > TRAVEL_DISTANCE) {
        continue;
      }
      final double cost = (initialTour.beerCount() + brewery.getBeerCount()) / totalDistance;
      if (cost > bestCost) {
        bestCost = cost;
        bestPosition = i;
      }
    }

    return Optional.ofNullable(bestPosition)
        .map(position -> initialTour.insertAt(brewery, position))
        .orElse(initialTour);
  }

  @Override
  public String toString() {
    return "LNS";
  }
}
