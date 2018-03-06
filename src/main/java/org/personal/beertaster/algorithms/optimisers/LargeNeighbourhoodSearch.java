package org.personal.beertaster.algorithms.optimisers;

import static org.personal.beertaster.main.BreweryManager.TRAVEL_DISTANCE;
import static org.personal.beertaster.main.BreweryManager.distanceBetween;
import static org.personal.beertaster.main.BreweryManager.getPossibleBreweries;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import org.personal.beertaster.algorithms.Optimiser;
import org.personal.beertaster.algorithms.optimisers.removal.ClusterRemoval;
import org.personal.beertaster.algorithms.optimisers.removal.RemovalType;
import org.personal.beertaster.algorithms.optimisers.removal.SimpleRemoval;
import org.personal.beertaster.elements.Brewery;
import org.personal.beertaster.elements.Tour;

/**
 * @author DATA-DOG Team
 */
public class LargeNeighbourhoodSearch implements Optimiser {

  private static final int DEFAULT_RUNTIME_MILLIS = 1000;
  private static final double ALLOWED_COST_DECREASE = 15D;

  private final List<RemovalType> removals = Arrays.asList(
      new SimpleRemoval(5F),
      new SimpleRemoval(7.5F),
      new SimpleRemoval(10F),
      new SimpleRemoval(15F),
      new SimpleRemoval(20F),
      // new SequenceRemoval(15F),
      // new SequenceRemoval(20F),
      new ClusterRemoval(15F, TRAVEL_DISTANCE * 0.005),
      new ClusterRemoval(10F, TRAVEL_DISTANCE * 0.015)
  );
  private RemovalType selected;

  @Override
  public Tour optimiseTour(final Tour initialTour) {
    final Instant until = Instant.now().plusMillis(DEFAULT_RUNTIME_MILLIS);

    double allowedDecrease = ALLOWED_COST_DECREASE;
    int iterations = 0;
    Tour best = new Tour(initialTour);
    Tour iteration = new Tour(initialTour);

    while (until.isAfter(Instant.now())) {
      final Tour optimised = singleIteration(iteration);

      if (optimised.cost() * ((100 + allowedDecrease) / 100) > best.cost() ||
          iteration.isBetter(optimised)) {
        iteration = new Tour(optimised);
      }

      if (best.isBetter(iteration)) {
        best = new Tour(iteration);
        Optional.ofNullable(selected).ifPresent(RemovalType::success);
        System.out.println(String.format(
            "SUCCESS: distance: %.1f; Total beer: %d [%s]", best.distance(), best.beerCount(),
            Optional.ofNullable(selected).map(RemovalType::name).orElse("NONE")
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
    System.out.println(removals);
    return best;
  }

  private Tour singleIteration(final Tour original) {
    // remove defined percentage of breweries from tour
    final Tour iterationTour = new Tour(original);
    if (iterationTour.breweriesCount() > 0) {
      selected = removals.get(ThreadLocalRandom.current().nextInt(removals.size()));
      selected.remove(iterationTour);
    }

    // possible new breweries to visit
    final double remainingDistance = TRAVEL_DISTANCE - iterationTour.distance();
    final List<Brewery> breweries = getPossibleBreweries().stream()
        .filter(brew -> !iterationTour.breweries().contains(brew))
        .filter(brew -> iterationTour.breweries().stream()
            .mapToDouble(visitedBrew -> distanceBetween(visitedBrew, brew))
            .min().orElse(TRAVEL_DISTANCE) <= remainingDistance)
        .collect(Collectors.toList());
    Collections.shuffle(breweries);

    return breweries.stream()
        .reduce(
            new Tour(iterationTour),
            this::bestInsertion,
            (tour1, tour2) -> tour1
        );
  }

  private Tour bestInsertion(final Tour tour, final Brewery brewery) {
    return tour.bestInsertion(brewery)
        .map(position -> tour.insertAt(brewery, position))
        .orElse(tour);
  }

  @Override
  public String toString() {
    return "LNS";
  }
}
