package org.personal.beertaster.main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.personal.beertaster.ui.MainPane;

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
