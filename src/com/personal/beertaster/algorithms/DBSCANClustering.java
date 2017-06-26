package com.personal.beertaster.algorithms;

import com.personal.beertaster.elements.Brewery;
import com.personal.beertaster.elements.Coordinates;
import com.personal.beertaster.main.BreweryManager;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

/**
 * @author DATA-DOG Team
 */
public class DBSCANClustering {

    private static final int MIN_SIZE = 4;
    private static final double MAX_DISTANCE = 500;

    public Map<Brewery, Set<Brewery>> clusterBreweries(final List<Brewery> breweries) {
        final Map<Brewery, Boolean> possibleBreweries = breweries.stream()
                .filter(brewery -> brewery.getID() > 0)
                .filter(brewery -> Objects.nonNull(brewery.getCoordinates()))
                .collect(toMap(Function.identity(), brewery -> false));

        return possibleBreweries.keySet().stream()
                .filter(brewery -> !possibleBreweries.get(brewery))
                .peek(neighbour -> possibleBreweries.put(neighbour, true))
                .map(brewery -> neighbours(brewery, possibleBreweries.keySet()))
                .filter(possibleNeighbours -> possibleNeighbours.size() >= MIN_SIZE)
                .map(neighbours -> createCluster(neighbours, possibleBreweries))
                .map(this::createCentroid)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map.Entry<Brewery, Set<Brewery>> createCentroid(final Set<Brewery> cluster) {
        final OptionalDouble avgX = cluster.stream()
                .map(Brewery::getCoordinates)
                .mapToDouble(Coordinates::getX)
                .average();
        final OptionalDouble avgY = cluster.stream()
                .map(Brewery::getCoordinates)
                .mapToDouble(Coordinates::getY)
                .average();

        if (avgX.isPresent() && avgY.isPresent()) {
            final Brewery centroid = new Brewery(-1);
            final Coordinates newCoordinates = Coordinates
                    .fromCartesianCoordinates(avgX.getAsDouble(), avgY.getAsDouble());
            centroid.setCoordinates(newCoordinates);

            return new AbstractMap.SimpleEntry<>(centroid, cluster);
        }
        return null;
    }

    private Set<Brewery> createCluster(
            final Set<Brewery> neighbours,
            final Map<Brewery, Boolean> possibleBreweries
    ) {
        final Set<Brewery> cluster = new HashSet<>();

        Set<Brewery> nextNeighbours = new HashSet<>(neighbours);
        while (!nextNeighbours.isEmpty()) {
            nextNeighbours = nextNeighbours.stream()
                    .filter(brewery -> !possibleBreweries.get(brewery))
                    .peek(neighbour -> possibleBreweries.put(neighbour, true))
                    .map(neighbour -> neighbours(neighbour, possibleBreweries.keySet()))
                    .filter(possibleNeighbours -> possibleNeighbours.size() >= MIN_SIZE)
                    .flatMap(Set::stream)
                    .collect(Collectors.toSet());
            cluster.addAll(nextNeighbours);
        }

        return cluster;
    }

    private Set<Brewery> neighbours(final Brewery brewery, final Set<Brewery> breweries) {
        return breweries.stream()
                .filter(neighbour -> BreweryManager.distanceBetween(brewery, neighbour) <= MAX_DISTANCE)
                .collect(Collectors.toSet());
    }
}
