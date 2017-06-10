package com.personal.beertaster.ui;

import com.personal.beertaster.elements.Brewery;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * @author DATA-DOG Team
 */
public class BreweryCircle extends Circle {

    public static final double RADIUS = 10D;
    public static final double STROKE_WIDTH = 1D;

    public enum CircleStyle {
        NORMAL, VISITABLE, VISITED, ORIGIN
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

        setRadius(RADIUS);
        setStroke(Color.BLACK);
        setStrokeWidth(STROKE_WIDTH);
        setColorStyle();
    }

    public Brewery brewery() {
        return this.brewery;
    }

    public double latitude() {
        return brewery.getCoordinates().getLatitude();
    }

    public double longitude() {
        return brewery.getCoordinates().getLongitude();
    }

    public void setVisited() {
        if (style != CircleStyle.VISITABLE) return;
        this.style = CircleStyle.VISITED;
        setColorStyle();
    }

    public void setNormal() {
        if (style == CircleStyle.ORIGIN) return;
        this.style = CircleStyle.NORMAL;
        setColorStyle();
    }

    public void setVisitable() {
        if (style == CircleStyle.ORIGIN) return;
        this.style = CircleStyle.VISITABLE;
        setColorStyle();
    }

    public boolean isVisitable() {
        return style == CircleStyle.VISITABLE;
    }

    public boolean isOrigin() {
        return style == CircleStyle.ORIGIN;
    }

    private void setColorStyle() {
        switch (style) {
            case ORIGIN: {
                setFill(Color.DARKGREEN);
                break;
            }
            case VISITABLE: {
                setFill(Color.CRIMSON);
                setOpacity(0.6D);
                break;
            }
            case VISITED: {
                setFill(Color.DODGERBLUE);
                setOpacity(0.6D);
                break;
            }
            default: {
                setFill(Color.LIGHTGRAY);
                setOpacity(0.6D);
                break;
            }
        }
    }
}
