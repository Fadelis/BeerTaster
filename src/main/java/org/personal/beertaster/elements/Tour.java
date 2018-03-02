package org.personal.beertaster.elements;

import static java.lang.String.format;
import static java.util.Optional.of;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static org.personal.beertaster.main.BreweryManager.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.IntStream;

public class Tour {

  private final List<Brewery> breweries = new ArrayList<>();
  private double distance = 0;
  private int beerCount = 0;

  public Tour() {
  }

  public Tour(final Tour tour) {
    if (tour != null && tour.breweries != null) {
      this.breweries.addAll(tour.breweries);
    }
  }

  public Tour withBrewery(final Brewery brewery) {
    final Tour tour = new Tour(this);
    tour.addBrewery(brewery);
    return tour;
  }

  public void setTour(final Tour tour) {
    if (tour != null && tour.breweries != null) {
      this.breweries.clear();
      this.breweries.addAll(tour.breweries);
      distance = 0;
      beerCount = 0;
    }
  }

  public void addBrewery(final Brewery brewery) {
    if (brewery == null) {
      return;
    }
    breweries.add(brewery);
    distance = 0;
    beerCount = 0;
  }

  public void removeBrewery(final Brewery brewery) {
    breweries.remove(brewery);
    distance = 0;
    beerCount = 0;
  }

  public Brewery getBrewery(final int tourPosition) {
    if (tourPosition < 0 || tourPosition >= tourSize()) {
      return null;
    }
    return breweries.get(tourPosition);
  }

  public Brewery lastBrewery() {
    return breweries.get(breweries.size() - 1);
  }

  public void setBrewery(final int tourPosition, final Brewery brewery) {
    breweries.set(tourPosition, brewery);
    distance = 0;
    beerCount = 0;
  }

  public Tour insertAt(final Brewery brewery, final int position) {
    final Tour newTour = new Tour(this);
    newTour.breweries.add(position, brewery);
    return newTour;
  }

  public List<Brewery> breweries() {
    return breweries;
  }

  // Get the total distance of the tour
  public double distance() {
    if (distance == 0) {
      distance = IntStream.range(1, breweries.size())
          .mapToDouble(i -> distanceBetween(breweries.get(i - 1), breweries.get(i)))
          .sum();
    }
    return distance;
  }

  //Get total number of beer collected
  public int beerCount() {
    if (beerCount == 0) {
      beerCount = breweries.stream().mapToInt(Brewery::getBeerCount).sum();
    }
    return beerCount;
  }

  public double cost() {
    return beerCount() / distance();
  }

  public long breweriesCount() {
    return breweries.stream()
        .filter(brewery -> !Objects.equals(ORIGIN, brewery))
        .count();
  }

  /**
   * @return true if given tour is better that this tour.
   */
  public boolean isBetter(final Tour maybeBetterTour) {
    final double newDistance = maybeBetterTour.distance();
    final int newBeerCount = maybeBetterTour.beerCount();

    if (newDistance > TRAVEL_DISTANCE || newBeerCount < beerCount()) {
      return false;
    } else if (newBeerCount > beerCount()) {
      return true;
    }
    return newDistance < distance();
  }

  public int tourSize() {
    return breweries.size();
  }

  public boolean possibleToInsert(final Brewery brewery) {
    if (brewery == null) {
      return false;
    }

    final double totalDistance = distance() +
        distanceBetween(lastBrewery(), brewery) +
        distanceToOrigin(brewery);

    return totalDistance <= TRAVEL_DISTANCE;
  }

  public OptionalDouble estimatedCost(final Brewery brewery, final int position) {
    final double distFrom = distanceBetween(breweries.get(position - 1), brewery);
    final double distTo = position < tourSize() ?
        distanceBetween(breweries.get(position), brewery) :
        distanceToOrigin(brewery);
    final double oldFrom = distanceBetween(breweries.get(position - 1), breweries.get(position));
    final double totalDistance = distance() + distFrom + distTo - oldFrom;

    if (totalDistance > TRAVEL_DISTANCE) {
      return OptionalDouble.empty();
    }
    return OptionalDouble.of((beerCount() + brewery.getBeerCount()) / totalDistance);
  }

  public Optional<String> isValid() {
    if (breweries.get(0) != ORIGIN) {
      return of("ERROR: Start brewery must be HOME! " + breweries.get(0));
    }
    if (lastBrewery() != ORIGIN) {
      return of("ERROR: End brewery must be HOME! " + lastBrewery());
    }
    if (distance() > TRAVEL_DISTANCE) {
      return of(format("ERROR: Travel distance cannot exceed %.0f!", TRAVEL_DISTANCE));
    }

    return breweries().stream()
        .filter(brewery -> !Objects.equals(ORIGIN, brewery))
        .collect(groupingBy(Brewery::getID, counting()))
        .entrySet().stream()
        .filter(entry -> entry.getValue() > 1)
        .findFirst()
        .map(entry -> format("ERROR: Tour contains duplicate brewery: [%d]!", entry.getKey()));
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append(format("Visited %s breweries", breweriesCount()));
    for (int i = 0; i < breweries.size(); i++) {
      double distBetween = 0;
      if (i != 0) {
        distBetween = distanceBetween(getBrewery(i - 1), getBrewery(i));
      }
      sb.append(System.lineSeparator())
          .append(format("\t%s distance %.1fkm", breweries.get(i), distBetween));
    }
    sb.append(System.lineSeparator());
    sb.append(format("Total distance travelled: %.1fkm", distance()));
    sb.append(System.lineSeparator());
    sb.append(System.lineSeparator());
    sb.append(format("Collected %s beer types:", beerCount()));
    breweries.stream()
        .map(Brewery::getBeerList)
        .filter(Objects::nonNull)
        .flatMap(List::stream)
        .forEach(beer -> sb.append(System.lineSeparator()).append("\t").append(beer.toString()));
    sb.append(System.lineSeparator());

    return sb.toString();
  }
}
