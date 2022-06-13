package com.github.dmtex.math.expression;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

final class ConstantNode extends ValueNode {

  static final ConstantNode MINUS_ONE = new ConstantNode(-1);

  static final ConstantNode ZERO = new ConstantNode(0);

  static final ConstantNode ONE = new ConstantNode(1);

  static final ConstantNode TWO = new ConstantNode(2);

  private final Number value;

  ConstantNode(Number value) {
    super(String.valueOf(value));
    this.value = value;
  }

  ConstantNode(Token token, Number value) {
    super(token);
    this.value = value;
  }

  @Override
  double evaluate(Map<String, ? extends Number> arg) {
    return value.doubleValue();
  }

  @Override
  AbstractNode derivative(String arg) {
    return ZERO;
  }

  @Override
  Set<String> getVariables() {
    return Collections.emptySet();
  }
}
