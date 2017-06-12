package com.personal.beertaster.ui;

import com.personal.beertaster.algorithms.BreweryManager;
import javafx.scene.control.*;

import java.util.Objects;
import java.util.function.BiConsumer;

import static com.personal.beertaster.elements.Coordinates.*;

/**
 * @author DATA-DOG Team
 */
public class MainToolBar extends ToolBar {

    private final TextField txtLatitude;
    private final TextField txtLongitude;
    private final Button btnRoute;
    private final Button btnOptimise;

    public MainToolBar() {
        txtLatitude = new TextField(Double.toString(BreweryManager.ORIGIN.getCoordinates().getLatitude()));
        txtLongitude = new TextField(Double.toString(BreweryManager.ORIGIN.getCoordinates().getLongitude()));
        btnRoute = new Button("Route");
        btnOptimise = new Button("Optimise");

        txtLatitude.setTooltip(new Tooltip("Origin latitude"));
        txtLatitude.setPromptText("Latitude");
        txtLongitude.setTooltip(new Tooltip("Origin longitude"));
        txtLongitude.setPromptText("Longitude");

        getItems().setAll(txtLatitude, txtLongitude, btnRoute, btnOptimise);
    }

    public void setOnRouteCallback(final BiConsumer<Double, Double> onRouteCallback) {
        btnRoute.setOnAction(e -> {
            final Double latitude = parse(txtLatitude.getText(), MIN_LATITUDE, MAX_LATITUDE, "Latitude");
            final Double longitude = parse(txtLongitude.getText(), MIN_LONGITUDE, MAX_LONGITUDE, "Longitude");

            if (Objects.nonNull(latitude) && Objects.nonNull(longitude)) {
                onRouteCallback.accept(latitude, longitude);
            }
        });
    }

    public void setOnOptimiseCallback(final Runnable action) {
        btnOptimise.setOnAction(e -> action.run());
    }

    private Double parse(final String text, final double min, final double max, final String label) {
        try {
            final double value = Double.parseDouble(text);
            if (value > min && value < max) {
                return value;
            }
            showErrorDialog(String.format("%s must be between %.0f and %.0f", label, min, max));
        } catch (final NumberFormatException ex) {
            showErrorDialog(String.format("Failed to parse to valid number: %s", text));
        }
        return null;
    }

    private void showErrorDialog(final String message) {
        final Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("Error occurred while parsing");
        alert.setContentText(message);

        alert.showAndWait();
    }
}
