package org.personal.beertaster.algorithms.optimisers.removal;

import static org.personal.beertaster.main.BreweryManager.ORIGIN;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import org.personal.beertaster.algorithms.DBSCANClustering;
import org.personal.beertaster.elements.Brewery;
import org.personal.beertaster.elements.Tour;

/**
 * @author DATA-DOG Team
 */
public class ClusterRemoval extends RemovalType {

  private final float percentage;
  private final double minDistance;

  public ClusterRemoval(final float percentage, final double minDistance) {
    this.percentage = percentage;
    this.minDistance = minDistance;
  }

  @Override
  public Tour remove(final Tour tour) {
    final DBSCANClustering clustering = new DBSCANClustering(2, minDistance);
    int toRemove = Math.round(tour.breweriesCount() * (percentage / 100));

    final Map<Brewery, Set<Brewery>> clusters = clustering
        .clusterBreweries(tour.breweries().stream()
            .filter(brewery -> !ORIGIN.equals(brewery))
            .collect(Collectors.toList()));
    while (toRemove > 0) {
      final int id = ThreadLocalRandom.current().nextInt(clusters.size());
      final Set<Brewery> removeCluster = clusters.keySet().stream()
          .skip(id)
          .findFirst()
          .map(clusters::remove)
          .orElseGet(() -> Collections.singleton(
              tour.getBrewery(ThreadLocalRandom.current().nextInt(1, tour.tourSize() - 1))
          ));
      removeCluster.forEach(tour::removeBrewery);
      toRemove -= removeCluster.size();
    }

    return tour;
  }

  @Override
  public String name() {
    return String.format("CLUSTER %.0f%%", percentage);
  }
}
