package org.personal.beertaster.algorithms.routers;

import static org.personal.beertaster.main.BreweryManager.ORIGIN;

import org.personal.beertaster.algorithms.Router;
import org.personal.beertaster.elements.Tour;

/**
 * @author DATA-DOG Team
 */
public class EmptyRouter implements Router {

  @Override
  public Tour planRoute() {
    return new Tour().withBrewery(ORIGIN).withBrewery(ORIGIN);
  }

  @Override
  public String toString() {
    return "Empty Router";
  }
}
