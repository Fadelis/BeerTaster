/*
 * NEPASITEISINO
 */

package org.personal.beertaster.algorithms.routers;

import static java.util.Comparator.comparing;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.personal.beertaster.algorithms.Router;
import org.personal.beertaster.elements.Brewery;
import org.personal.beertaster.elements.Tour;
import org.personal.beertaster.main.BreweryManager;

public class BruteForceRouter implements Router {

  private static final int BREWERIES_COUNT_CHECK = 2;
  private static final int MAX_ITERATIONS = 10000;

  private int counter;

  @Override
  public Tour planRoute() {
    final List<Brewery> possibleBreweries = BreweryManager.getPossibleBreweries().stream()
        .filter(Brewery::containsBeer)
        .collect(Collectors.toList());
    System.out.println("Total possible breweries - " + possibleBreweries.size());

    final Tour bestTour = new Tour();
    bestTour.addBrewery(BreweryManager.ORIGIN);

    counter = 0;
    recursivePlanner(bestTour, bestTour, possibleBreweries);

    System.out.println(String.format("Checked %s routes", counter));

    return bestTour;
  }

  private Tour recursivePlanner(
      final Tour bestTour,
      final Tour currentTour,
      final List<Brewery> possibleBreweries
  ) {
    if (counter >= MAX_ITERATIONS) {
      return bestTour;
    }

    final Brewery currentBrewery = currentTour.getBrewery(currentTour.tourSize() - 1);

    final Tour possibleBestTour = possibleBreweries.stream()
        .filter(brewery -> !currentTour.breweries().contains(brewery))
        .filter(currentTour::possibleToInsert)
        .sorted(comparing(
            brewery -> BreweryManager.distanceBetween(brewery, currentBrewery) / brewery
                .getBeerCount()))
        .limit(BREWERIES_COUNT_CHECK)
        .map(brewery -> recursivePlanner(
            bestTour,
            currentTour.withBrewery(brewery),
            without(brewery, possibleBreweries)
        )).max(comparing(Tour::beerCount))
        .orElse(currentTour);
    if (possibleBestTour.tourSize() > 1
        && possibleBestTour.getBrewery(possibleBestTour.tourSize() - 1) != BreweryManager.ORIGIN) {
      possibleBestTour.addBrewery(BreweryManager.ORIGIN);
    }

    if (bestTour.beerCount() < possibleBestTour.beerCount()) {
      bestTour.setTour(possibleBestTour);
      System.out.println(String.format(
          "[%s] Found new best route: %s breweries; %s beers; %.1f km;",
          counter,
          possibleBestTour.breweriesCount(),
          possibleBestTour.beerCount(),
          possibleBestTour.distance()
      ));
    }

    counter++;
    return possibleBestTour;
  }

  private static List<Brewery> without(final Brewery brewery, final List<Brewery> list) {
    final List<Brewery> newList = new ArrayList<>(list);
    newList.remove(brewery);

    return newList;
  }


  @Override
  public String toString() {
    return "Brute Force Router";
  }
}
