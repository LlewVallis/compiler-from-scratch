package io.github.llewvallis.cfs.interpret;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(callSuper = false)
public final class IntValue extends RValue {

  @Getter private final int value;

  public IntValue(int value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return Integer.toString(value);
  }
}
