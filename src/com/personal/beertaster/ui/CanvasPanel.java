package com.personal.beertaster.ui;

import com.personal.beertaster.algorithms.Tour;
import com.personal.beertaster.elements.Brewery;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

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
    private final Group elementGroup = new Group(circlesContainer, routeContainer);
    private final CanvasGestures gestures;

    public CanvasPanel() {
        circlesContainer.translateXProperty().bind(widthProperty().divide(2));
        circlesContainer.translateYProperty().bind(heightProperty().divide(2));
        routeContainer.translateXProperty().bind(widthProperty().divide(2));
        routeContainer.translateYProperty().bind(heightProperty().divide(2));

        getChildren().addAll(elementGroup);

        Rectangle clipRectangle = new Rectangle();
        clipRectangle.widthProperty().bind(widthProperty());
        clipRectangle.heightProperty().bind(heightProperty());
        setClip(clipRectangle);

        widthProperty().addListener(e -> scale());
        heightProperty().addListener(e -> scale());
        gestures = new CanvasGestures(this);
    }

    public void setupAllBreweries(final Brewery origin, final List<Brewery> breweries) {
        circles.clear();
        circles.addAll(breweries.stream()
                .filter(brewery -> Objects.nonNull(brewery.getCoordinates()))
                .map(BreweryCircle::new)
                .collect(Collectors.toList()));
        circles.add(new BreweryCircle(origin, BreweryCircle.CircleStyle.ORIGIN));

        circlesContainer.getChildren().setAll(circles);
    }

    public void setupNewOrigin(final Brewery origin, final List<Brewery> visitableBreweries) {
        circles.forEach(BreweryCircle::setNormal);
        circles.stream()
                .filter(circle -> visitableBreweries.contains(circle.brewery()))
                .forEach(BreweryCircle::setVisitable);

        translateBasedOnOrigin(origin);
        scale();
    }

    public void setupRoute(final Tour tour) {
        route.clear();

        BreweryCircle current = null;
        for (int i = 0; i < tour.breweries().size(); i++) {
            final Brewery brewery = tour.breweries().get(i);
            final BreweryCircle next = circles.stream()
                    .filter(circle -> Objects.equals(brewery, circle.brewery()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Could not find routed brewery!"));

            if (!next.isOrigin()) {
                next.setVisited();
            }
            if (current != null) {
                route.add(new BreweryLine(current, next, i));
            }

            current = next;
        }

        routeContainer.getChildren().setAll(route);
        gestures.scaleElementSize();
    }

    private void translateBasedOnOrigin(final Brewery origin) {
        final double translateX = -origin.getCoordinates().getLatitude();
        final double translateY = -origin.getCoordinates().getLongitude();
        circles.forEach(circle -> {
            circle.setCenterX(circle.latitude() + translateX);
            circle.setCenterY(circle.longitude() + translateY);
        });
    }

    void scale() {
        final List<BreweryCircle> visitableCircles = circles.stream()
                .filter(BreweryCircle::isVisitable)
                .collect(Collectors.toList());

        final double width = getWidth() / 2 - PADDING;
        final double height = getHeight() / 2 - PADDING;

        final DoubleSummaryStatistics summaryX = visitableCircles.stream()
                .collect(summarizingDouble(Circle::getCenterX));
        final DoubleSummaryStatistics summaryY = visitableCircles.stream()
                .collect(summarizingDouble(Circle::getCenterY));

        final double groupMaxX = Math.max(Math.abs(summaryX.getMax()), Math.abs(summaryX.getMin()));
        final double groupMaxY = Math.max(Math.abs(summaryY.getMax()), Math.abs(summaryY.getMin()));
        final double scale = Math.min(width / groupMaxX, height / groupMaxY);

        circles.forEach(circle -> {
            circle.setTranslateX(circle.getCenterX() * scale - circle.getCenterX());
            circle.setTranslateY(circle.getCenterY() * scale - circle.getCenterY());
        });
    }

    public Group elementGroup() {
        return elementGroup;
    }
}
