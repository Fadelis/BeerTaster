package org.personal.beertaster.algorithms.optimisers.removal;

/**
 * @author DATA-DOG Team
 */
public abstract class RemovalType implements Removal {

  private int successCounter;

  public RemovalType() {
  }

  public int success() {
    return successCounter++;
  }

  public void reset() {
    successCounter = 0;
  }

  public abstract String name();

  public String toString() {
    return String.format("%s: %d", name(), successCounter);
  }
}
