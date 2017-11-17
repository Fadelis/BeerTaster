package org.personal.beertaster.algorithms.optimisers;

import java.util.Random;
import java.util.stream.IntStream;
import org.personal.beertaster.algorithms.Optimiser;
import org.personal.beertaster.elements.Brewery;
import org.personal.beertaster.elements.Tour;

/**
 * @author DATA-DOG Team
 */
public class SimulatedAnnealing implements Optimiser {

  private final Random rnd;

  public SimulatedAnnealing() {
    this.rnd = new Random();
  }

  @Override
  public Tour optimiseTour(final Tour initialSolution) {
    // Set initial temp
    double temp = 100000;

    // degradation percentage
    final double degradation = 0.1;

    // Cooling rate
    final double coolingRate = 0.003;

    // number of swaps in each iteration
    final int swaps = 1;

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

  private void swap(final Tour tour) {
    // Get a random positions in the tour
    final int tourPos1 = rnd.nextInt(tour.tourSize() - 1) + 1;
    final int tourPos2 = rnd.nextInt(tour.tourSize() - 1) + 1;

    // Get the cities at selected positions in the tour
    final Brewery citySwap1 = tour.getBrewery(tourPos1);
    final Brewery citySwap2 = tour.getBrewery(tourPos2);

    // Swap them
    tour.setBrewery(tourPos2, citySwap1);
    tour.setBrewery(tourPos1, citySwap2);
  }

  // Calculate the acceptance probability
  private boolean acceptanceProbability(
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

  @Override
  public String toString() {
    return "Simulated Annealing";
  }
}
