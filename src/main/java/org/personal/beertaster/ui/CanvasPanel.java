package org.personal.beertaster.ui;

import static java.util.stream.Collectors.summarizingDouble;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import org.personal.beertaster.elements.Brewery;
import org.personal.beertaster.elements.Tour;
import org.personal.beertaster.main.BreweryManager;

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

    final Rectangle clipRectangle = new Rectangle();
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
    circles.removeIf(
        circle -> circle.isOrigin() && !Objects.equals(circle.brewery(), BreweryManager.ORIGIN));
    circles.forEach(BreweryCircle::setNormal);
    circles.stream()
        .filter(circle -> visitableBreweries.contains(circle.brewery()))
        .forEach(BreweryCircle::setVisitable);

    circlesContainer.getChildren().setAll(circles);
    translateBasedOnOrigin(origin);
  }

  public void setupRoute(final Tour tour) {
    route.clear();
    circles.stream()
        .filter(BreweryCircle::isVisited)
        .forEach(BreweryCircle::setVisitable);

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
    scale();
  }

  public void setupClusters(final Map<Brewery, Set<Brewery>> clusters) {
    route.clear();
    circles.removeIf(
        circle -> circle.isOrigin() && !Objects.equals(circle.brewery(), BreweryManager.ORIGIN));

    clusters.forEach((centroid, elements) -> {
      final BreweryCircle centroidCircle = new BreweryCircle(centroid,
          BreweryCircle.CircleStyle.ORIGIN);
      circles.add(centroidCircle);

      elements.stream()
          .map(brewery -> circles.stream()
              .filter(circle -> Objects.equals(brewery, circle.brewery()))
              .findFirst()
              .orElseThrow(() -> new IllegalArgumentException("Could not find routed brewery!")))
          .forEach(brewery -> route.add(new BreweryLine(centroidCircle, brewery, 0)));
    });

    circlesContainer.getChildren().setAll(circles);
    routeContainer.getChildren().setAll(route);
    translateBasedOnOrigin(BreweryManager.ORIGIN);
    scale();
  }

  private void translateBasedOnOrigin(final Brewery origin) {
    final double translateX = origin.getCoordinates().getX();
    final double translateY = origin.getCoordinates().getY();
    circles.forEach(circle -> {
      circle.setCenterX(circle.getX() - translateX);
      circle.setCenterY(translateY - circle.getY());
    });
  }

  void scale() {
    gestures.reset();
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
