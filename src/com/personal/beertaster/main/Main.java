package com.personal.beertaster.main;

import com.personal.beertaster.ui.MainPane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(final String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {
        BreweryManager.initialize();

        primaryStage.setTitle("Beer Taster");
        primaryStage.setScene(new Scene(new MainPane(), 800, 500));
        primaryStage.show();
    }
}
