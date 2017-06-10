package com.personal.beertaster.ui;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

/**
 * @author DATA-DOG Team
 */
public class BreweryLine extends Line {

    public static final double LINE_WIDTH = 3D;

    private final BreweryCircle from;
    private final BreweryCircle to;
    private final int tripIndex;

    public BreweryLine(final BreweryCircle from, final BreweryCircle to, final int tripIndex) {
        this.from = from;
        this.to = to;
        this.tripIndex = tripIndex;

        setFill(Color.BLACK);
        setStrokeWidth(LINE_WIDTH);

        startXProperty().bind(from.centerXProperty().add(from.translateXProperty()));
        startYProperty().bind(from.centerYProperty().add(from.translateYProperty()));
        endXProperty().bind(to.centerXProperty().add(to.translateXProperty()));
        endYProperty().bind(to.centerYProperty().add(to.translateYProperty()));
    }
}
