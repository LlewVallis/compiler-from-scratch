package io.github.llewvallis.cfs.interpret;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public final class LValue extends Value {

  public RValue rValue;

  public LValue() {
    this.rValue = null;
  }

  public LValue(RValue rValue) {
    this.rValue = rValue;
  }

  public RValue get() throws InterpretException {
    if (rValue == null) throw new InterpretException("uninitialized variable");
    return rValue;
  }

  public void set(RValue value) {
    rValue = value;
  }
}
