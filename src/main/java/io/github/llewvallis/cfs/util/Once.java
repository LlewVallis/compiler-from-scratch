package io.github.llewvallis.cfs.util;

/**
 * A utility for executing a piece of code only once. This is useful for managing errors - often we
 * want to ignore errors after the first one until we manage to recover in some way.
 */
public class Once {

  private boolean triggered;

  public Once() {
    triggered = false;
  }

  public Once(Once other) {
    triggered = other.triggered;
  }

  public boolean once() {
    var result = !triggered;
    triggered = true;
    return result;
  }

  public void reset() {
    triggered = false;
  }
}
