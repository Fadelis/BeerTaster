package com.personal.beertaster.elements;

import java.util.ArrayList;

public class Brewery {
	private final int id;
	private String name;
	private String country, city;
	private Coordinates coord;
	private ArrayList<Beer> beerList;
	
	public Brewery(int id) {
		this.id = id;
	}
	
	public Brewery(int id, String name, String country, String city, double lat, double lon) {
		this.id = id;
		this.name = name;
		this.country = country;
		this.city = city;
		setCoordinates(lat, lon);
	}
	
	public double getDistance(Coordinates other) {
		if (other == null || getCoordinates() == null) return Double.NaN;
		return getCoordinates().getDistance(other);
	}
	
	public double getDistance(Brewery other) {
		if (other == null || other.getCoordinates() == null || getCoordinates() == null) return Double.NaN;
		return getCoordinates().getDistance(other.getCoordinates());
	}
	
	public int getID() { return this.id; }
	
	public void setName(String value) { this.name = value; }
	public String getName() { return this.name; }
	
	public void setCountry(String value) { this.country = value; }
	public String getCountry() { return this.country; }
	
	public void setCity(String value) { this.city = value; }
	public String getCity() { return this.city; }
	
	public void setCoordinates(double lat, double lon) { 
		if (coord == null) {
			this.coord = new Coordinates(lat, lon);
		} else {
			this.coord.setLatitude(lat);
			this.coord.setLongitude(lon);
		}
	}
	public Coordinates getCoordinates() { return this.coord; }
	
	public void addBeer(Beer beer) {
		if (beer == null) return;
		if (beerList == null) 
			beerList = new ArrayList<>();
		
		if (!beerList.contains(beer))
			beerList.add(beer);
	}
	public void removeBeer(Beer beer) {
		if (beer == null || beerList == null || beerList.isEmpty()) return;
		
		beerList.remove(beer);
	}
	public ArrayList<Beer> getBeerList() { return this.beerList; }
	public int getBeerCount() { return beerList != null ? beerList.size() : 0; }
	
	@Override
	public String toString() {
		if (getID() > 0)
			return String.format("[%s] [%s beers] %s: %s", getID(), getBeerCount(), getName(), getCoordinates());
		else
			return String.format("%s: %s", getName(), getCoordinates());
	}
}
