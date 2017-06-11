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

    private Tour currentTour;

    public MainPane() {
        toolBar.setOnRouteCallback(this::createRoute);
        toolBar.setOnOptimiseCallback(this::optimiseRoute);

        setCenter(canvas);
        setTop(toolBar);

        canvas.setupAllBreweries(ORIGIN, getBreweryList());
        createRoute(ORIGIN.getCoordinates().getLatitude(), ORIGIN.getCoordinates().getLongitude());
    }

    private void createRoute(final double latitude, final double longitude) {
        final long start = System.currentTimeMillis();

        setOriginLocation(latitude, longitude);

        currentTour = planRoute();

        canvas.setupNewOrigin(ORIGIN, getPossibleBreweries());
        canvas.setupRoute(currentTour);

        final long total = System.currentTimeMillis() - start;
        System.out.println("Created route in " + total + " ms");
        System.out.println(String.format(
                "Total distance: %.1f; Total beer: %d",
                currentTour.getDistance(),
                currentTour.getBeerCount()
        ));
    }

    private void optimiseRoute() {
        final long start = System.currentTimeMillis();

        currentTour = BestReinsertion.optimiseTour(currentTour);

        canvas.setupRoute(currentTour);

        final long total = System.currentTimeMillis() - start;
        System.out.println("Optimised route in " + total + " ms");
        System.out.println(String.format(
                "Total distance: %.1f; Total beer: %d",
                currentTour.getDistance(),
                currentTour.getBeerCount()
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
