package com.github.dmtex.math.expression;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

final class Values {

  private final Map<String, Number> constants = new HashMap<>();
  private final Set<String> variables = new HashSet<>();

  Values() {
    this.constants.put("e", Math.E);
    this.constants.put("pi", Math.PI);
  }

  ValueNode getNode(Token token) {
    if (variables.contains(token.getValue())) {
      return new VariableNode(token);
    }
    Number value = constants.get(token.getValue());
    return new ConstantNode(token, value == null ? Double.parseDouble(token.getValue()) : value);
  }

  void setVariables(Iterable<String> names) {
    for (String n : names) {
      variables.add(n.toLowerCase(Locale.getDefault()));
    }
  }
}
