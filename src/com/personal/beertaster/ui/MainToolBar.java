package com.personal.beertaster.ui;

import com.personal.beertaster.algorithms.BreweryManager;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;

import java.util.function.BiConsumer;

/**
 * @author DATA-DOG Team
 */
public class MainToolBar extends ToolBar {

    private final TextField txtLatitude;
    private final TextField txtLongitude;
    private final Button btnRoute;

    public MainToolBar() {
        txtLatitude = new TextField(Double.toString(BreweryManager.ORIGIN.getCoordinates().getLatitude()));
        txtLongitude = new TextField(Double.toString(BreweryManager.ORIGIN.getCoordinates().getLongitude()));
        btnRoute = new Button("Route");

        getItems().setAll(txtLatitude, txtLongitude, btnRoute);
    }

    public void setOnRouteCallback(final BiConsumer<Double, Double> onRouteCallback) {
        btnRoute.setOnAction(e -> {
            final double latitude = Double.parseDouble(txtLatitude.getText());
            final double longitude = Double.parseDouble(txtLongitude.getText());

            onRouteCallback.accept(latitude, longitude);
        });
    }
}
