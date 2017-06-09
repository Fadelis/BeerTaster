package com.personal.beertaster.algorithms;

import com.personal.beertaster.elements.Brewery;

import java.util.ArrayList;
import java.util.List;

/**
 * @author DATA-DOG Team
 */
public class SimulatedAnnealing {

    public static Tour optimiseTour(final Tour initialSolution) {
        // Set initial temp
        double temp = 10000;

        // Cooling rate
        final double coolingRate = 0.003;

        // Possible breweries to visit
        final List<Brewery> possibleBreweries = new ArrayList<>(BreweryManager.getPossibleBreweries());

        // initialise solution
        Tour currentSolution = new Tour(initialSolution);

        System.out.println(String.format(
                "Initial solution distance: %.1f; beers: %d",
                currentSolution.getDistance(),
                currentSolution.getBeerCount()
        ));

        // Set as current best
        Tour best = new Tour(currentSolution);

        // Loop until system has cooled
        while (temp > 1) {
            // Create new neighbour tour
            final Tour newSolution = new Tour(currentSolution);

            // Get a random positions in the tour
            final int tourPos1 = (int) (newSolution.tourSize() * Math.random());
            final int tourPos2 = (int) (newSolution.tourSize() * Math.random());

            // Get the cities at selected positions in the tour
            final Brewery citySwap1 = newSolution.getBrewery(tourPos1);
            final Brewery citySwap2 = newSolution.getBrewery(tourPos2);

            // Swap them
            newSolution.setBrewery(tourPos2, citySwap1);
            newSolution.setBrewery(tourPos1, citySwap2);

            // Get energy of solutions
            final double currentEnergy = currentSolution.getDistance();
            final double neighbourEnergy = newSolution.getDistance();

            // Decide if we should accept the neighbour
            if (acceptanceProbability(currentEnergy, neighbourEnergy, temp) > Math.random()) {
                currentSolution = new Tour(newSolution);
            }

            // Keep track of the best solution found
            if (currentSolution.getDistance() < best.getDistance()) {
                best = new Tour(currentSolution);
            }

            // Cool system
            temp *= 1 - coolingRate;
        }

        System.out.println(String.format(
                "Final solution distance: %.1f; beers: %d",
                best.getDistance(),
                best.getBeerCount()
        ));

        return best;
    }

    // Calculate the acceptance probability
    private static double acceptanceProbability(final double energy, final double newEnergy, final double temperature) {
        // If the new solution is better, accept it
        if (newEnergy < energy) {
            return 1.0;
        }
        // If the new solution is worse, calculate an acceptance probability
        return Math.exp((energy - newEnergy) / temperature);
    }
}
