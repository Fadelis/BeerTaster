package org.personal.beertaster.ui;

import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * @author DATA-DOG Team
 */
public class StatusBar extends ToolBar {

  private final Label txtFactories;
  private final Label txtBeers;
  private final Label txtDistance;
  private final Label txtRuntime;

  public StatusBar() {
    txtFactories = labelInstance();
    txtBeers = labelInstance();
    txtDistance = labelInstance();
    txtRuntime = new Label();
    final Pane spacer = new Pane();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    getItems().addAll(txtFactories, vSeparator(), txtBeers, vSeparator(), txtDistance, vSeparator(),
        spacer, txtRuntime);
  }

  public void factoriesText(final String label) {
    txtFactories.setText(label);
  }

  public void beersText(final String label) {
    txtBeers.setText(label);
  }

  public void distanceText(final String label) {
    txtDistance.setText(label);
  }

  public void runtimeText(final String label) {
    txtRuntime.setText(label);
  }

  private Label labelInstance() {
    final Label label = new Label();
    label.setFont(
        Font.font(Font.getDefault().getName(), FontWeight.BOLD, Font.getDefault().getSize()));
    return label;
  }

  private Separator vSeparator() {
    return new Separator(Orientation.VERTICAL);
  }
}
