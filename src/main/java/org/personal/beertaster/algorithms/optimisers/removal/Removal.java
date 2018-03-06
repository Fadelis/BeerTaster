package org.personal.beertaster.algorithms.optimisers.removal;

import org.personal.beertaster.elements.Tour;

/**
 * @author DATA-DOG Team
 */
@FunctionalInterface
public interface Removal {

  Tour remove(final Tour tour);
}
