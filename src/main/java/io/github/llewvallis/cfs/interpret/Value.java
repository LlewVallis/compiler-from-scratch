package io.github.llewvallis.cfs.interpret;

public abstract sealed class Value permits IntValue {

  @Override
  public abstract boolean equals(Object obj);

  @Override
  public abstract int hashCode();

  @Override
  public abstract String toString();
}
