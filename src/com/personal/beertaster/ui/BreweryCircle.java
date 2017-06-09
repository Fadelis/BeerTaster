package com.personal.beertaster.ui;

import com.personal.beertaster.elements.Brewery;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * @author DATA-DOG Team
 */
public class BreweryCircle extends Circle {

    public enum CircleStyle {
        NORMAL, VISITED, ORIGIN
    }

    private static final Tooltip TOOLTIP = new Tooltip();

    private final Brewery brewery;
    private CircleStyle style;

    public BreweryCircle(final Brewery brewery) {
        this(brewery, CircleStyle.NORMAL);
    }

    public BreweryCircle(final Brewery brewery, final CircleStyle style) {
        this.brewery = brewery;
        this.style = style;

        setCenterX(brewery.getCoordinates().getLatitude());
        setCenterY(brewery.getCoordinates().getLongitude());

        setRadius(10D);
        setStroke(Color.BLACK);
        setStrokeWidth(1D);
        switch (style) {
            case ORIGIN: {
                setFill(Color.DARKGREEN);
                break;
            }
            default: {
                setFill(Color.CRIMSON);
                setOpacity(0.6D);
                break;
            }
        }
    }

    public Brewery brewery() { return this.brewery; }

    public void setVisited() {
        this.style = CircleStyle.VISITED;
        setFill(Color.CORNFLOWERBLUE);
    }
}
