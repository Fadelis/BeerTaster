package com.personal.beertaster.ui;

import com.personal.beertaster.algorithms.BreweryManager;
import com.personal.beertaster.algorithms.Tour;
import com.personal.beertaster.elements.Brewery;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.summarizingDouble;

/**
 * @author DATA-DOG Team
 */
public class CanvasPanel extends Pane {

    private static final double PADDING = 20D;

    private final List<BreweryCircle> circles = new ArrayList<>();
    private final List<BreweryLine> route = new ArrayList<>();
    private final Group circlesContainer = new Group();
    private final Group routeContainer = new Group();

    public CanvasPanel() {
        circlesContainer.translateXProperty().bind(widthProperty().divide(2));
        circlesContainer.translateYProperty().bind(heightProperty().divide(2));
        routeContainer.translateXProperty().bind(widthProperty().divide(2));
        routeContainer.translateYProperty().bind(heightProperty().divide(2));

        getChildren().addAll(circlesContainer, routeContainer);

        widthProperty().addListener(e -> scale());
        heightProperty().addListener(e -> scale());
    }

    public void setupNewOrigin(final Brewery origin, final List<Brewery> breweries) {
        circles.clear();
        circles.addAll(breweries.stream()
                .map(BreweryCircle::new)
                .collect(Collectors.toList()));
        circles.add(new BreweryCircle(origin, BreweryCircle.CircleStyle.ORIGIN));

        circlesContainer.getChildren().setAll(circles);

        translateBasedOnOrigin();
        scale();
    }

    public void setupRoute(final Tour tour) {
        route.clear();

        BreweryCircle current = circles.stream()
                .filter(circle -> Objects.equals(tour.breweries().get(0), circle.brewery()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No start brewery present!"));

        for (int i = 1; i < tour.breweries().size(); i++) {
            final Brewery brewery = tour.breweries().get(i);
            final BreweryCircle next = circles.stream()
                    .filter(circle -> Objects.equals(brewery, circle.brewery()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Could not find routed brewery!"));

            route.add(new BreweryLine(current, next));
            current = next;
        }

        routeContainer.getChildren().setAll(route);
    }

    private void translateBasedOnOrigin() {
        final double translateX = -BreweryManager.ORIGIN.getCoordinates().getLatitude();
        final double translateY = -BreweryManager.ORIGIN.getCoordinates().getLongitude();
        circles.forEach(circle -> {
            circle.setCenterX(circle.getCenterX() + translateX);
            circle.setCenterY(circle.getCenterY() + translateY);
        });
    }

    private void scale() {
        final double width = getWidth() / 2 - PADDING;
        final double height = getHeight() / 2 - PADDING;

        final DoubleSummaryStatistics summaryX = circles.stream().collect(summarizingDouble(Circle::getCenterX));
        final DoubleSummaryStatistics summaryY = circles.stream().collect(summarizingDouble(Circle::getCenterY));

        final double groupMaxX = Math.max(Math.abs(summaryX.getMax()), Math.abs(summaryX.getMin()));
        final double groupMaxY = Math.max(Math.abs(summaryY.getMax()), Math.abs(summaryY.getMin()));
        final double scale = Math.min(width / groupMaxX, height / groupMaxY);

        circles.forEach(circle -> {
            circle.setTranslateX(circle.getCenterX() * scale - circle.getCenterX());
            circle.setTranslateY(circle.getCenterY() * scale - circle.getCenterY());
        });
    }
}
