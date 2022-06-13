package com.github.dmtex.math.expression;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

final class VariableNode extends ValueNode {

  VariableNode(Token token) {
    super(token);
  }

  @Override
  double evaluate(Map<String, ? extends Number> arg) {
    return arg.get(getName()).doubleValue();
  }

  @Override
  AbstractNode derivative(String arg) {
    return getName().equals(arg) ? ConstantNode.ONE : ConstantNode.ZERO;
  }

  @Override
  Set<String> getVariables() {
    return new HashSet<>(Collections.singletonList(getName()));
  }
}
