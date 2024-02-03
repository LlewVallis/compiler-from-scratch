package io.github.llewvallis.cfs.interpret;

public abstract sealed class Value permits LValue, RValue {

  public IntValue castToInt() throws InterpretException {
    return switch (this) {
      case IntValue value -> value;
      default -> throw new InterpretException("wrong type, expected int");
    };
  }

  @Override
  public abstract boolean equals(Object obj);

  @Override
  public abstract int hashCode();

  @Override
  public abstract String toString();
}
