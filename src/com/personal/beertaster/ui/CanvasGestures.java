package com.personal.beertaster.ui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.transform.Scale;

/**
 * @author Fadel.
 */
public class CanvasGestures {

    public static final double MAX_SCALE = 1000.0D;
    public static final double MIN_SCALE = 0.01D;

    private final CanvasPanel panel;
    private final Group canvas;

    private final Scale transform = new Scale();
    private final SimpleDoubleProperty scale = new SimpleDoubleProperty(1);

    private double mouseAnchorX = 0;
    private double mouseAnchorY = 0;
    private double translateAnchorX = 0;
    private double translateAnchorY = 0;

    public CanvasGestures(final CanvasPanel panel) {
        this.panel = panel;
        this.canvas = panel.elementGroup();

        transform.xProperty().bind(scale);
        transform.yProperty().bind(scale);
        canvas.getTransforms().add(transform);

        panel.setOnScroll(event -> zoom(canvas, event));
        panel.setOnZoom(event -> zoom(canvas, event));
        panel.setOnMousePressed(onMousePressedEventHandler());
        panel.setOnMouseDragged(onMouseDraggedEventHandler());
        panel.setOnMouseClicked(onMouseClickedEventHandler());
    }

    private EventHandler<MouseEvent> onMousePressedEventHandler() {
        return event -> {
            // right mouse button => panning
            if (!event.isPrimaryButtonDown())
                return;

            mouseAnchorX = event.getX();
            mouseAnchorY = event.getY();

            translateAnchorX = canvas.getTranslateX();
            translateAnchorY = canvas.getTranslateY();
        };
    }

    private EventHandler<MouseEvent> onMouseDraggedEventHandler() {
        return event -> {
            // right mouse button => panning
            if (!event.isPrimaryButtonDown())
                return;

            canvas.setTranslateX(translateAnchorX + event.getX() - mouseAnchorX);
            canvas.setTranslateY(translateAnchorY + event.getY() - mouseAnchorY);

            event.consume();
        };
    }

    private EventHandler<MouseEvent> onMouseClickedEventHandler() {
        return event -> {
            // right mouse button => panning
            if (!event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                reset();
            }
        };
    }

    /**
     * Allow to zoom/scale any node with pivot at scene (x,y) coordinates.
     *
     * @param node
     * @param factor
     * @param x
     * @param y
     */
    public void zoom(final Node node, final double factor, final double x, final double y) {
        final double oldScale = getScale();
        double scale = oldScale * factor;
        if (scale < MIN_SCALE) scale = MIN_SCALE;
        if (scale > MAX_SCALE) scale = MAX_SCALE;
        setScale(scale);

        final double f = (scale / oldScale) - 1;

        final Bounds localBounds = node.getBoundsInLocal();
        final Bounds layoutBounds = node.getLayoutBounds();
        final double minX = localBounds.getMinX() - layoutBounds.getMinX();
        final double minY = localBounds.getMinY() - layoutBounds.getMinY();
        final double width = layoutBounds.getWidth();
        final double height = layoutBounds.getHeight();

        final Bounds mainBounds = new BoundingBox(minX, minY, width, height);
        final Bounds bounds = node.localToScene(mainBounds);

        final double dx = (x - bounds.getMinX());
        final double dy = (y - bounds.getMinY());

        final double pivotX = node.getTranslateX() - f * dx;
        final double pivotY = node.getTranslateY() - f * dy;

        node.setTranslateX(pivotX);
        node.setTranslateY(pivotY);

        scaleElementSize();
    }

    public void zoom(final Node node, final ScrollEvent event) {
        if (event.isDirect()) return;
        zoom(node, Math.pow(1.005, event.getDeltaY()), event.getSceneX(), event.getSceneY());
    }

    public void zoom(final Node node, final ZoomEvent event) {
        zoom(node, event.getZoomFactor(), event.getSceneX(), event.getSceneY());
    }

    void scaleElementSize() {
        canvas.getChildren().forEach(group -> {
            if (group instanceof Group) {
                ((Group) group).getChildren().forEach(node -> {
                    if (node instanceof Circle) {
                        ((Circle) node).setRadius(BreweryCircle.RADIUS / scale.get());
                        ((Circle) node).setStrokeWidth(BreweryCircle.STROKE_WIDTH / scale.get());
                    } else if (node instanceof Line) {
                        ((Line) node).setStrokeWidth(BreweryLine.LINE_WIDTH / scale.get());
                    }
                });
            }
        });
    }

    public void reset() {
        setScale(1D);
        this.canvas.setTranslateX(0D);
        this.canvas.setTranslateY(0D);

        scaleElementSize();
    }

    public void setScale(final double scale) {
        this.scale.set(scale);
    }

    public double getScale() {
        return this.scale.get();
    }

    public DoubleProperty scaleProperty() {
        return this.scale;
    }
}
