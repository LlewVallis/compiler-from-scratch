package io.github.llewvallis.cfs.interpret;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public final class IntValue extends Value {

  private final int value;

  public IntValue(int value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return Integer.toString(value);
  }
}
