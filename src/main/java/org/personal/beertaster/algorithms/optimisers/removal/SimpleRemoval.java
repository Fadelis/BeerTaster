package org.personal.beertaster.algorithms.optimisers.removal;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import org.personal.beertaster.elements.Tour;

/**
 * @author DATA-DOG Team
 */
public class SimpleRemoval extends RemovalType {

  private final float percentage;

  public SimpleRemoval(final float percentage) {
    this.percentage = percentage;
  }

  @Override
  public Tour remove(final Tour tour) {
    final int toRemove = Math.round(tour.breweriesCount() * (percentage / 100));
    ThreadLocalRandom.current()
        .ints(1, tour.tourSize() - 1)
        .distinct()
        .limit(toRemove)
        .mapToObj(tour::getBrewery)
        .collect(Collectors.toList())
        .forEach(tour::removeBrewery);

    return tour;
  }

  @Override
  public String name() {
    return String.format("SIMPLE %.0f%%", percentage);
  }
}
