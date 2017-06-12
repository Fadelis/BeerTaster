package com.personal.beertaster.ui;

import com.personal.beertaster.algorithms.BestReinsertion;
import com.personal.beertaster.algorithms.SimpleNNA;
import com.personal.beertaster.elements.Tour;
import javafx.scene.layout.BorderPane;

import static com.personal.beertaster.algorithms.BreweryManager.*;

/**
 * @author DATA-DOG Team
 */
public class MainPane extends BorderPane {

    private final CanvasPanel canvas = new CanvasPanel();
    private final MainToolBar toolBar = new MainToolBar();
    private final StatusBar statusBar = new StatusBar();

    private Tour currentTour;

    public MainPane() {
        toolBar.setOnRouteCallback(this::createRoute);
        toolBar.setOnOptimiseCallback(this::optimiseRoute);

        setCenter(canvas);
        setTop(toolBar);
        setBottom(statusBar);

        canvas.setupAllBreweries(ORIGIN, getBreweryList());
        createRoute(ORIGIN.getCoordinates().getLatitude(), ORIGIN.getCoordinates().getLongitude());
    }

    private void createRoute(final double latitude, final double longitude) {
        setOriginLocation(latitude, longitude);

        final long start = System.currentTimeMillis();
        currentTour = planRoute();
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

    private void optimiseRoute() {
        final long start = System.currentTimeMillis();
        currentTour = BestReinsertion.optimiseTour(currentTour);
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

    private static Tour planRoute() {
        final Tour tour = SimpleNNA.planSimpleNNA();
        //final Tour tour = AdvancedNNA.planAdvancedNNA();
        //final Tour tour = BruteForce.planBruteForce();

        System.out.println(tour.toString());

        return tour;
    }
}
