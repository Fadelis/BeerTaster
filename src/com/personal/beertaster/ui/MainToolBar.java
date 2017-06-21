package com.personal.beertaster.ui;

import com.personal.beertaster.algorithms.Optimiser;
import com.personal.beertaster.algorithms.Router;
import com.personal.beertaster.elements.Coordinates;
import com.personal.beertaster.main.BreweryManager;
import javafx.scene.control.*;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.personal.beertaster.elements.Coordinates.*;

/**
 * @author DATA-DOG Team
 */
public class MainToolBar extends ToolBar {

    private final TextField txtLatitude;
    private final TextField txtLongitude;
    private final SplitMenuButton btnRoute;
    private final SplitMenuButton btnOptimise;
    private final Button btnCluster;

    private Router routePlanner;
    private Optimiser optimiserStrategy;

    public MainToolBar(final List<Router> routers, final List<Optimiser> optimisers) {
        txtLatitude = new TextField(Double.toString(BreweryManager.ORIGIN.getCoordinates().getLatitude()));
        txtLongitude = new TextField(Double.toString(BreweryManager.ORIGIN.getCoordinates().getLongitude()));

        txtLatitude.setTooltip(new Tooltip("Origin latitude"));
        txtLatitude.setPromptText("Latitude");
        txtLongitude.setTooltip(new Tooltip("Origin longitude"));
        txtLongitude.setPromptText("Longitude");

        btnRoute = new SplitMenuButton();
        btnRoute.getItems().setAll(routers.stream()
                .map(router -> {
                    final MenuItem item = new MenuItem(router.toString());
                    item.setOnAction(e -> {
                        setRoutePlanner(router);
                        btnRoute.setText(router.toString());
                        btnRoute.fire();
                    });
                    return item;
                }).toArray(MenuItem[]::new)
        );
        btnOptimise = new SplitMenuButton();
        btnOptimise.getItems().setAll(optimisers.stream()
                .map(optimiser -> {
                    final MenuItem item = new MenuItem(optimiser.toString());
                    item.setOnAction(e -> {
                        setOptimiserStrategy(optimiser);
                        btnOptimise.setText(optimiser.toString());
                        btnOptimise.fire();
                    });
                    return item;
                }).toArray(MenuItem[]::new)
        );

        btnRoute.setText("Router type");
        btnRoute.setTooltip(new Tooltip("Select and execute specific route planning type"));
        btnOptimise.setText("Optimisation type");
        btnOptimise.setTooltip(new Tooltip("Select and execute specific route optimisation strategy"));

        btnCluster = new Button("Cluster");

        getItems().setAll(txtLatitude, txtLongitude, btnRoute, btnOptimise, btnCluster);
    }

    public void setOnRouteCallback(final BiConsumer<Coordinates, Router> callback) {
        btnRoute.setOnAction(e -> {
            final Double latitude = parse(txtLatitude.getText(), MIN_LATITUDE, MAX_LATITUDE, "Latitude");
            final Double longitude = parse(txtLongitude.getText(), MIN_LONGITUDE, MAX_LONGITUDE, "Longitude");

            if (Objects.nonNull(latitude) && Objects.nonNull(longitude) && Objects.nonNull(routePlanner)) {
                callback.accept(new Coordinates(latitude, longitude), routePlanner);
            }
        });
    }

    public void setOnOptimiseCallback(final Consumer<Optimiser> callback) {
        btnOptimise.setOnAction(e -> {
            if (Objects.nonNull(optimiserStrategy)) {
                callback.accept(optimiserStrategy);
            }
        });
    }

    public void setOnClusterCallback(final Runnable callback) {
        btnCluster.setOnAction(e -> callback.run());
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

    private void setRoutePlanner(final Router routePlanner) {
        this.routePlanner = routePlanner;
    }

    private void setOptimiserStrategy(final Optimiser optimiserStrategy) {
        this.optimiserStrategy = optimiserStrategy;
    }
}
