/*
Pirma kilusi mintis - paprastas algorimas nuo pradzios iteruojantis per galimus bravorus
pasveriant kiekvieno kelio svori - kiek kilomentru vienai alaus rusiai sunaudojama.
Surandamas maziausias svoris ir keliaujama i si bravora, taip pat patikrinant ar uzteks
kuro grizti namo. Nukeliavus i nauja bravora algoritmas kartojasi, kol nebelieka galimu
bravoru, kuriem uztektu kuro ir alplankyti ir grizti namo.
*/

package org.personal.beertaster.algorithms.routers;

import static java.util.Comparator.comparing;
import static org.personal.beertaster.main.BreweryManager.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.personal.beertaster.algorithms.DBSCANClustering;
import org.personal.beertaster.algorithms.Router;
import org.personal.beertaster.algorithms.optimisers.BestReinsertion;
import org.personal.beertaster.elements.Brewery;
import org.personal.beertaster.elements.Tour;
import org.personal.beertaster.main.BreweryManager;

public class ClusterRouter implements Router {

  private static final int MIN_SIZE = 2;
  private static final double MAX_DISTANCE = TRAVEL_DISTANCE * 0.03;
  private static final DBSCANClustering CLUSTERING = new DBSCANClustering(MIN_SIZE, MAX_DISTANCE);
  private static final BestReinsertion OPTIMISER = new BestReinsertion();

  @Override
  public Tour planRoute() {
//    final Tour initial = CLUSTERING.clusterBreweries(getPossibleBreweries())
//        .entrySet().stream()
//        .sorted(comparingDouble(
//            e -> ORIGIN.getCoordinates().getEuclideanDistance(e.getKey().getCoordinates()) /
//                e.getValue().stream().mapToInt(Brewery::getBeerCount).sum()))
//        .map(Entry::getValue)
//        .flatMap(cluster -> cluster.stream()
//            .sorted(comparing(BreweryManager::distanceToOrigin))
//            .limit(2))
//        .reduce(
//            new Tour().withBrewery(ORIGIN).withBrewery(ORIGIN),
//            this::bestInsertion,
//            (tour1, tour2) -> tour1
//        );
//
//    return OPTIMISER.optimiseTour(initial);

    return CLUSTERING.clusterBreweries(getPossibleBreweries())
        .values().stream()
        .map(cluster -> cluster.stream()
            .min(comparing(BreweryManager::distanceToOrigin))
            .map(start -> new Tour().withBrewery(ORIGIN).withBrewery(start))
            .map(this::fillRoute))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .max(comparing(Tour::beerCount))
        .map(OPTIMISER::multipleOptimization)
        .orElseGet(() -> new Tour().withBrewery(ORIGIN));
  }

  /**
   * Fill tour using best fitting neighbour, which yields most beer per traveled kilometer.
   */
  private Tour fillRoute(final Tour initialTour) {
    final Tour solution = new Tour(initialTour);
    final List<Brewery> tempPossibleBreweries = new ArrayList<>(getPossibleBreweries());
    tempPossibleBreweries.removeAll(solution.breweries());
    tempPossibleBreweries.removeIf(brewery -> !solution.possibleToInsert(brewery));

    while (true) {
      final Brewery lastBrewery = solution.lastBrewery();
      final Brewery nextBrewery = tempPossibleBreweries.stream()
          .max(comparing(brew -> brew.getBeerCount() / distanceBetween(lastBrewery, brew)))
          .orElse(ORIGIN);

      solution.addBrewery(nextBrewery);
      tempPossibleBreweries.remove(nextBrewery);
      tempPossibleBreweries.removeIf(brewery -> !solution.possibleToInsert(brewery));

      if (Objects.equals(nextBrewery, ORIGIN)) {
        break;
      }
    }

    return solution;
  }

  private Tour bestInsertion(final Tour tour, final Brewery brewery) {
    return tour.bestInsertion(brewery)
        .map(position -> tour.insertAt(brewery, position))
        .orElse(tour);
  }

  @Override
  public String toString() {
    return "Cluster Router";
  }
}
