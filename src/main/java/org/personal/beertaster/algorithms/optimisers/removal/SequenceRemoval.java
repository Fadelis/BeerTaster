package org.personal.beertaster.algorithms.optimisers.removal;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.personal.beertaster.elements.Tour;

/**
 * @author DATA-DOG Team
 */
public class SequenceRemoval extends RemovalType {

  private final float percentage;

  public SequenceRemoval(final float percentage) {
    this.percentage = percentage;
  }

  @Override
  public Tour remove(final Tour tour) {
    final int toRemove = Math.round(tour.breweriesCount() * (percentage / 100));
    int start = ThreadLocalRandom.current().nextInt(1, tour.tourSize() - 1);
    if (start + toRemove >= tour.tourSize()) {
      start = tour.tourSize() - toRemove - 1;
    }
    IntStream.range(start, tour.tourSize() - 1)
        .mapToObj(tour::getBrewery)
        .collect(Collectors.toList())
        .forEach(tour::removeBrewery);

    return tour;
  }

  @Override
  public String name() {
    return String.format("SEQUENCE %.0f%%", percentage);
  }
}
