package com.personal.beertaster.ui;

import com.personal.beertaster.elements.Brewery;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import static com.personal.beertaster.algorithms.BreweryManager.distanceToOrigin;

/**
 * @author DATA-DOG Team
 */
public class BreweryCircle extends Circle {

    public static final double RADIUS = 10D;
    public static final double STROKE_WIDTH = 1D;

    public enum CircleStyle {
        NORMAL, VISITABLE, VISITED, ORIGIN
    }

    private final Brewery brewery;
    private CircleStyle style;

    public BreweryCircle(final Brewery brewery) {
        this(brewery, CircleStyle.NORMAL);
    }

    public BreweryCircle(final Brewery brewery, final CircleStyle style) {
        this.brewery = brewery;
        this.style = style;

        setCenterX(getX());
        setCenterY(getY());

        setRadius(RADIUS);
        setStroke(Color.BLACK);
        setStrokeWidth(STROKE_WIDTH);
        updateColorStyle();

        final Tooltip tooltip = new Tooltip();
        tooltip.setOnShowing(e -> tooltip.setText(getTooltip()));
        Tooltip.install(this, tooltip);
    }

    public Brewery brewery() {
        return this.brewery;
    }

    public double getX() {
        return brewery.getCoordinates().getX();
    }

    public double getY() {
        return brewery.getCoordinates().getY();
    }

    public String getTooltip() {
        return new StringBuilder()
                .append(brewery.toString())
                .append(System.lineSeparator())
                .append("Distance to origin: ")
                .append(String.format("%.1f km", distanceToOrigin(brewery)))
                .toString();
    }

    public void setVisited() {
        if (style != CircleStyle.VISITABLE) return;
        this.style = CircleStyle.VISITED;
        updateColorStyle();
    }

    public void setNormal() {
        if (style == CircleStyle.ORIGIN) return;
        this.style = CircleStyle.NORMAL;
        updateColorStyle();
    }

    public void setVisitable() {
        if (style == CircleStyle.ORIGIN) return;
        this.style = CircleStyle.VISITABLE;
        updateColorStyle();
    }

    public boolean isVisitable() {
        return style == CircleStyle.VISITABLE;
    }

    public boolean isVisited() {
        return style == CircleStyle.VISITED;
    }

    public boolean isOrigin() {
        return style == CircleStyle.ORIGIN;
    }

    private void updateColorStyle() {
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
