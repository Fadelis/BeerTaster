package org.personal.beertaster.elements;

public class Beer {

  private final int id;
  private String name;
  private int breweryID;
  private int styleID;
  private int categoryID;

  public Beer(final int id) {
    this.id = id;
  }

  public Beer(final int id, final String name, final int breweryID, final int styleID,
      final int categoryID) {
    this.id = id;
    this.name = name;
    this.breweryID = breweryID;
    this.styleID = styleID;
    this.categoryID = categoryID;
  }

  public int getID() {
    return this.id;
  }

  public void setName(final String value) {
    this.name = value;
  }

  public String getName() {
    return this.name;
  }

  public void setBreweryID(final int value) {
    this.breweryID = value;
  }

  public int getBreweryID() {
    return this.breweryID;
  }

  public void setStyleID(final int value) {
    this.styleID = value;
  }

  public int getStyleID() {
    return this.styleID;
  }

  public void setCategoryID(final int value) {
    this.categoryID = value;
  }

  public int getCategoryID() {
    return this.categoryID;
  }

  @Override
  public String toString() {
    return String.format("[%s] %s", getID(), getName());
  }
}
