package org.personal.beertaster.elements;

import java.util.ArrayList;

public class Brewery {

  private final int id;
  private String name;
  private String country, city;
  private Coordinates coordinates;
  private ArrayList<Beer> beerList;

  public Brewery(final int id) {
    this.id = id;
  }

  public Brewery(
      final int id,
      final String name,
      final String country,
      final String city,
      final double lat,
      final double lon
  ) {
    this.id = id;
    this.name = name;
    this.country = country;
    this.city = city;
    setCoordinates(lat, lon);
  }

  public double getDistance(final Coordinates other) {
    if (other == null || getCoordinates() == null) {
      return Double.NaN;
    }
    return getCoordinates().getDistance(other);
  }

  public double getDistance(final Brewery other) {
    if (other == null || other.getCoordinates() == null || getCoordinates() == null) {
      return Double.NaN;
    }
    return getCoordinates().getDistance(other.getCoordinates());
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

  public void setCountry(final String value) {
    this.country = value;
  }

  public String getCountry() {
    return this.country;
  }

  public void setCity(final String value) {
    this.city = value;
  }

  public String getCity() {
    return this.city;
  }

  public void setCoordinates(final double lat, final double lon) {
    this.coordinates = new Coordinates(lat, lon);
  }

  public void setCoordinates(final Coordinates coordinates) {
    this.coordinates = coordinates;
  }

  public Coordinates getCoordinates() {
    return this.coordinates;
  }

  public void addBeer(final Beer beer) {
    if (beer == null) {
      return;
    }
    if (beerList == null) {
      beerList = new ArrayList<>();
    }

    if (!beerList.contains(beer)) {
      beerList.add(beer);
    }
  }

  public ArrayList<Beer> getBeerList() {
    return this.beerList;
  }

  public int getBeerCount() {
    return beerList != null ? beerList.size() : 0;
  }

  public boolean containsBeer() {
    return getBeerCount() > 0;
  }

  @Override
  public String toString() {
    if (getID() > 0) {
      return String
          .format("[%s] [%s beers] %s: %s", getID(), getBeerCount(), getName(),
              getCoordinates());
    } else {
      return String.format("%s: %s", getName(), getCoordinates());
    }
  }
}
