package org.personal.beertaster.ui;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.scene.layout.BorderPane;
import org.personal.beertaster.algorithms.DBSCANClustering;
import org.personal.beertaster.algorithms.Optimiser;
import org.personal.beertaster.algorithms.Router;
import org.personal.beertaster.algorithms.optimisers.BestReinsertion;
import org.personal.beertaster.algorithms.optimisers.LargeNeighbourhoodSearch;
import org.personal.beertaster.algorithms.optimisers.SimulatedAnnealing;
import org.personal.beertaster.algorithms.routers.BruteForceRouter;
import org.personal.beertaster.algorithms.routers.LookAheadRouter;
import org.personal.beertaster.algorithms.routers.SimpleOptimisedRouter;
import org.personal.beertaster.algorithms.routers.SimpleRouter;
import org.personal.beertaster.elements.Brewery;
import org.personal.beertaster.elements.Coordinates;
import org.personal.beertaster.elements.Tour;
import org.personal.beertaster.main.BreweryManager;

/**
 * @author DATA-DOG Team
 */
public class MainPane extends BorderPane {

  private static final List<Router> ROUTERS = Arrays.asList(
      new SimpleRouter(),
      new SimpleOptimisedRouter(),
      new LookAheadRouter(),
      new BruteForceRouter()
  );
  private static final List<Optimiser> OPTIMISERS = Arrays.asList(
      new BestReinsertion(),
      new LargeNeighbourhoodSearch(),
      new SimulatedAnnealing()
  );

  private final CanvasPanel canvas = new CanvasPanel();
  private final MainToolBar toolBar = new MainToolBar(ROUTERS, OPTIMISERS);
  private final StatusBar statusBar = new StatusBar();

  private Tour currentTour;

  public MainPane() {
    toolBar.setOnRouteCallback(this::createRoute);
    toolBar.setOnOptimiseCallback(this::optimiseRoute);
    toolBar.setOnClusterCallback(this::clusterBreweries);

    setCenter(canvas);
    setTop(toolBar);
    setBottom(statusBar);

    canvas.setupAllBreweries(BreweryManager.ORIGIN, BreweryManager.getBreweryList());
    createRoute(BreweryManager.ORIGIN.getCoordinates(), new SimpleOptimisedRouter());
  }

  private void createRoute(final Coordinates coordinates, final Router routePlanner) {
    BreweryManager.setOriginLocation(coordinates.getLatitude(), coordinates.getLongitude());

    final long start = System.currentTimeMillis();
    currentTour = routePlanner.planRoute();
    currentTour.isValid().ifPresent(System.out::println);
    final long total = System.currentTimeMillis() - start;

    statusBar.factoriesText(String.format("Breweries visited: %d", currentTour.breweriesCount()));
    statusBar.beersText(String.format("Beers collected: %d", currentTour.beerCount()));
    statusBar.distanceText(String.format("Distance travelled: %.1f km", currentTour.distance()));
    statusBar.runtimeText(String.format("Created route in %d ms", total));

    canvas.setupNewOrigin(BreweryManager.ORIGIN, BreweryManager.getPossibleBreweries());
    canvas.setupRoute(currentTour);

    System.out.println("Created route in " + total + " ms");
    System.out.println(String.format(
        "Total distance: %.1f; Total beer: %d",
        currentTour.distance(),
        currentTour.beerCount()
    ));
  }

  private void optimiseRoute(final Optimiser optimiser) {
    final long start = System.currentTimeMillis();
    currentTour = optimiser.optimiseTour(currentTour);
    currentTour.isValid().ifPresent(System.out::println);
    final long total = System.currentTimeMillis() - start;

    statusBar.factoriesText(String.format("Breweries visited: %d", currentTour.breweriesCount()));
    statusBar.beersText(String.format("Beers collected: %d", currentTour.beerCount()));
    statusBar.distanceText(String.format("Distance travelled: %.1f km", currentTour.distance()));
    statusBar.runtimeText(String.format("Optimised route in %d ms", total));

    canvas.setupRoute(currentTour);

    System.out.println("Optimised route in " + total + " ms");
    System.out.println(String.format(
        "Total distance: %.1f; Total beer: %d",
        currentTour.distance(),
        currentTour.beerCount()
    ));
  }

  private void clusterBreweries() {
    final long start = System.currentTimeMillis();
    final Map<Brewery, Set<Brewery>> clusters = new DBSCANClustering()
        .clusterBreweries(BreweryManager.getBreweryList());
    final long total = System.currentTimeMillis() - start;

    canvas.setupClusters(clusters);

    statusBar.runtimeText(String.format("Clustered in %d ms", total));
  }
}
