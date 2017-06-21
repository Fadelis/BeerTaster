package com.personal.beertaster.ui;

import com.personal.beertaster.algorithms.KMeansClustering;
import com.personal.beertaster.algorithms.Optimiser;
import com.personal.beertaster.algorithms.Router;
import com.personal.beertaster.algorithms.optimisers.BestReinsertion;
import com.personal.beertaster.algorithms.optimisers.SimulatedAnnealing;
import com.personal.beertaster.algorithms.routers.BruteForceRouter;
import com.personal.beertaster.algorithms.routers.LookAheadRouter;
import com.personal.beertaster.algorithms.routers.SimpleOptimisedRouter;
import com.personal.beertaster.algorithms.routers.SimpleRouter;
import com.personal.beertaster.elements.Brewery;
import com.personal.beertaster.elements.Coordinates;
import com.personal.beertaster.elements.Tour;
import com.personal.beertaster.main.BreweryManager;
import javafx.scene.layout.BorderPane;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.personal.beertaster.main.BreweryManager.*;

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

        canvas.setupAllBreweries(ORIGIN, getBreweryList());
        createRoute(ORIGIN.getCoordinates(), new SimpleRouter());
    }

    private void createRoute(final Coordinates coordinates, final Router routePlanner) {
        setOriginLocation(coordinates.getLatitude(), coordinates.getLongitude());

        final long start = System.currentTimeMillis();
        currentTour = routePlanner.planRoute();
        final long total = System.currentTimeMillis() - start;

        statusBar.factoriesText(String.format("Breweries visited: %d", currentTour.breweriesCount()));
        statusBar.beersText(String.format("Beers collected: %d", currentTour.beerCount()));
        statusBar.distanceText(String.format("Distance travelled: %.1f km", currentTour.distance()));
        statusBar.runtimeText(String.format("Created route in %d ms", total));

        canvas.setupNewOrigin(ORIGIN, getPossibleBreweries());
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
        final Map<Brewery, Set<Brewery>> clusters = new KMeansClustering()
                .clusterBreweries(BreweryManager.getBreweryList());
        final long total = System.currentTimeMillis() - start;

        canvas.setupClusters(clusters);

        statusBar.runtimeText(String.format("Clustered in %d ms", total));
    }
}
