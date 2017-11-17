package org.personal.beertaster.algorithms;

import static java.util.stream.Collectors.*;

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;
import org.personal.beertaster.elements.Brewery;
import org.personal.beertaster.elements.Coordinates;

/**
 * @author DATA-DOG Team
 */
public class KMeansClustering {

  private static final int CLUSTERS_NUMBER = 8;

  public Map<Brewery, Set<Brewery>> clusterBreweries(final List<Brewery> breweries) {
    // filter origin location
    final List<Brewery> possibleBreweries = breweries.stream()
        .filter(brewery -> brewery.getID() > 0)
        .filter(brewery -> Objects.nonNull(brewery.getCoordinates()))
        .collect(toList());
    // create centroids
    final Set<Brewery> centroids = IntStream.rangeClosed(1, CLUSTERS_NUMBER)
        .mapToObj(Brewery::new)
        .collect(toSet());
    // initialize at random brewery location
    final Random rng = new Random();
    centroids.forEach(centroid -> {
      final Brewery rngBrewery = possibleBreweries.get(rng.nextInt(possibleBreweries.size()));

      centroid.setName(String.format("%d_CENTROID", centroid.getID()));
      centroid.setCoordinates(rngBrewery.getCoordinates().getLatitude(),
          rngBrewery.getCoordinates().getLongitude());
    });

    Map<Brewery, Set<Brewery>> mappedCentroids = assignElements(centroids, possibleBreweries);

    while (recenterCentroids(mappedCentroids)) {
      mappedCentroids = assignElements(mappedCentroids.keySet(), possibleBreweries);
    }

    return mappedCentroids;
  }

  private Map<Brewery, Set<Brewery>> assignElements(
      final Set<Brewery> centroids,
      final List<Brewery> possibleBreweries
  ) {
    return possibleBreweries.stream()
        .map(brewery -> new AbstractMap.SimpleEntry<>(
            brewery,
            centroids.stream()
                .min(Comparator.comparing(centroid ->
                    centroid.getCoordinates().getEuclideanDistance(brewery.getCoordinates())
                )).orElseThrow(() -> new IllegalArgumentException("No centroid found"))
        )).collect(groupingBy(Map.Entry::getValue, mapping(Map.Entry::getKey, toSet())));
  }

  private boolean recenterCentroids(final Map<Brewery, Set<Brewery>> assignedCentroids) {
    return assignedCentroids.entrySet().stream().anyMatch(entry -> {
      final OptionalDouble avgX = entry.getValue().stream()
          .map(Brewery::getCoordinates)
          .mapToDouble(Coordinates::getX)
          .average();
      final OptionalDouble avgY = entry.getValue().stream()
          .map(Brewery::getCoordinates)
          .mapToDouble(Coordinates::getY)
          .average();

      if (avgX.isPresent() && avgY.isPresent()) {
        final double oldX = entry.getKey().getCoordinates().getX();
        final double oldY = entry.getKey().getCoordinates().getY();
        if (avgX.getAsDouble() != oldX || avgY.getAsDouble() != oldY) {
          // FAIL need to create spatial coordinates from cartesian coordinates
          final Coordinates newCoordinates = Coordinates
              .fromCartesianCoordinates(avgX.getAsDouble(), avgY.getAsDouble());
          entry.getKey().setCoordinates(newCoordinates);
          return true;
        }
        return false;
      }
      return true;
    });
  }
}
