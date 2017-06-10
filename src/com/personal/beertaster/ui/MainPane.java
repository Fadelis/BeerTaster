package com.personal.beertaster.ui;

import com.personal.beertaster.algorithms.BruteForce;
import com.personal.beertaster.algorithms.Tour;
import javafx.scene.layout.BorderPane;

import static com.personal.beertaster.algorithms.BreweryManager.*;

/**
 * @author DATA-DOG Team
 */
public class MainPane extends BorderPane {

    private final CanvasPanel canvas = new CanvasPanel();
    private final MainToolBar toolBar = new MainToolBar();

    public MainPane() {
        toolBar.setOnRouteCallback(this::createRoute);

        setCenter(canvas);
        setTop(toolBar);

        canvas.setupAllBreweries(ORIGIN, getBreweryList());
        createRoute(ORIGIN.getCoordinates().getLatitude(), ORIGIN.getCoordinates().getLongitude());
    }

    private void createRoute(final double latitude, final double longitude) {
        setOriginLocation(latitude, longitude);

        canvas.setupNewOrigin(ORIGIN, getPossibleBreweries());
        canvas.setupRoute(planRoute());
    }

    private static Tour planRoute() {
        final long start = System.currentTimeMillis();

        //final Tour tour = SimpleNNA.planSimpleNNA();
        //final Tour tour = AdvancedNNA.planAdvancedNNA();
        final Tour tour = BruteForce.planBruteForce();
        //final Tour tour = SimulatedAnnealing.optimiseTour(SimpleNNA.planSimpleNNA());

        System.out.println(tour.toString());

        final long total = System.currentTimeMillis() - start;
        System.out.println("Calculated in " + total + " ms");
        System.out.println("Total beer " + tour.getBeerCount());
        System.out.println("Total distance " + tour.getDistance());

        return tour;
    }
}
