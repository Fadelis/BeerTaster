package com.personal.beertaster.ui;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

/**
 * @author DATA-DOG Team
 */
public class BreweryLine extends Line {

    private final BreweryCircle from;
    private final BreweryCircle to;

    public BreweryLine(final BreweryCircle from, final BreweryCircle to) {
        this.from = from;
        this.to = to;

        setFill(Color.BLACK);
        setStrokeWidth(2D);

        startXProperty().bind(from.centerXProperty().add(from.translateXProperty()));
        startYProperty().bind(from.centerYProperty().add(from.translateYProperty()));
        endXProperty().bind(to.centerXProperty().add(to.translateXProperty()));
        endYProperty().bind(to.centerYProperty().add(to.translateYProperty()));
    }
}
