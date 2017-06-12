package com.personal.beertaster.algorithms;

import com.personal.beertaster.elements.Brewery;
import com.personal.beertaster.elements.Tour;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * @author DATA-DOG Team
 */
public class SimulatedAnnealing {

    public static Tour optimiseTour(final Tour initialSolution) {
        // Set initial temp
        double temp = 100000;

        // degradation percentage
        final double degradation = 0.1;

        // Cooling rate
        final double coolingRate = 0.003;

        // number of swaps in each iteration
        final int swaps = 1;

        // Possible breweries to visit
        final List<Brewery> possibleBreweries = new ArrayList<>(BreweryManager.getPossibleBreweries());

        // initialise solution
        Tour currentSolution = new Tour(initialSolution);

        System.out.println(String.format(
                "Initial solution distance: %.1f; beers: %d",
                currentSolution.distance(),
                currentSolution.beerCount()
        ));

        // Set as current best
        Tour best = new Tour(currentSolution);

        // Loop until system has cooled
        while (temp > 1) {
            // Create new neighbour tour
            final Tour newSolution = new Tour(currentSolution);

            IntStream.range(0, swaps).forEach(i -> swap(newSolution));

            // Decide if we should accept the neighbour
            if (acceptanceProbability(currentSolution, newSolution, best, degradation)) {
                currentSolution = new Tour(newSolution);
            }

            // Keep track of the best solution found
            if (currentSolution.distance() < best.distance()) {
                best = new Tour(currentSolution);
            }

            // Cool system
            temp *= 1 - coolingRate;
        }

        System.out.println(String.format(
                "Final solution distance: %.1f; beers: %d",
                best.distance(),
                best.beerCount()
        ));

        return best;
    }

    private static void swap(final Tour tour) {
        // Get a random positions in the tour
        final int tourPos1 = (int) (tour.tourSize() * Math.random());
        final int tourPos2 = (int) (tour.tourSize() * Math.random());

        // Get the cities at selected positions in the tour
        final Brewery citySwap1 = tour.getBrewery(tourPos1);
        final Brewery citySwap2 = tour.getBrewery(tourPos2);

        // Swap them
        tour.setBrewery(tourPos2, citySwap1);
        tour.setBrewery(tourPos1, citySwap2);
    }

    // Calculate the acceptance probability
    private static boolean acceptanceProbability(
            final Tour currentTour,
            final Tour newTour,
            final Tour bestTour,
            final double degradation
    ) {
        // Get energy of solutions
        final double bestEnergy = bestTour.distance();
        final double energy = currentTour.distance();
        final double newEnergy = newTour.distance();

        // If the new solution is better, accept it
        if (newEnergy < energy) {
            return true;
        }
        // If the new solution is worse, calculate an acceptance probability
        return newEnergy < bestEnergy * (1 + degradation);
    }
}
