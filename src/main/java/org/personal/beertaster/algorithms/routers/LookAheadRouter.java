/*
NEPASITEISINO
Antra kilusi mintis - modifikuoti pirma algoritma taip, kad suradus geriausia
tolimesni bravora patikrinti ar nera geresnio varianto isnaudoti distancijai iki sio bravoro.

Pvz. turime bravora kuris yra uz 200km ir jame yra 10 alaus rusiu, taciau salia yra trys bravorai
vienas nuo kito issideste kas 40 km, kurie turi po 4 alaus rusys. Aplanke siuos tris bravorus
nukeliautume 160km ir surinktume 12 rusiu alaus.
*/

package org.personal.beertaster.algorithms.routers;

import static java.util.Comparator.comparing;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.personal.beertaster.algorithms.Router;
import org.personal.beertaster.elements.Brewery;
import org.personal.beertaster.elements.Tour;
import org.personal.beertaster.main.BreweryManager;

public class LookAheadRouter implements Router {

  private static final int LOOK_AHEAD = 2;

  @Override
  public Tour planRoute() {
    final List<Brewery> possibleBreweries = BreweryManager.getPossibleBreweries().stream()
        .filter(Brewery::containsBeer)
        .collect(Collectors.toList());
    System.out.println("Total possible breweries - " + possibleBreweries.size());

    final Tour tour = new Tour();
    tour.addBrewery(BreweryManager.ORIGIN);

    while (true) {
      final Brewery bestCandidate = possibleBreweries.stream()
          .map(brewery -> new SimpleEntry<>(brewery, lookAheadTour(brewery, tour)))
          .filter(entry -> Objects.nonNull(entry.getValue()))
          .min(comparing(entry -> entry.getValue().distance() / entry.getValue().beerCount()))
          .map(SimpleEntry::getKey)
          .orElse(BreweryManager.ORIGIN);

      tour.addBrewery(bestCandidate);
      possibleBreweries.remove(bestCandidate);

      if (Objects.equals(bestCandidate, BreweryManager.ORIGIN)) {
        break;
      }
    }

    return tour;
  }

  private Tour lookAheadTour(final Brewery startBrewery, final Tour currentTour) {
    if (!currentTour.possibleToInsert(startBrewery)) {
      return null;
    }

    final Tour lookAheadTour = new Tour(currentTour);
    lookAheadTour.addBrewery(startBrewery);

    while (lookAheadTour.breweries().size() < currentTour.tourSize() + LOOK_AHEAD + 1) {
      final Brewery lastBrewery = lookAheadTour.lastBrewery();
      final Optional<Brewery> maybeBrewery = BreweryManager.getPossibleBreweries().stream()
          .filter(brew -> !lookAheadTour.breweries().contains(brew))
          .filter(lookAheadTour::possibleToInsert)
          .max(comparing(brew -> brew.getBeerCount() / BreweryManager
              .distanceBetween(lastBrewery, brew)));

      if (maybeBrewery.isPresent()) {
        lookAheadTour.addBrewery(maybeBrewery.get());
      } else {
        break;
      }
    }

    return lookAheadTour;
  }


  @Override
  public String toString() {
    return "Look Ahead Router";
  }
}
