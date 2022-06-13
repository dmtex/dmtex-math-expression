package com.github.dmtex.math.expression;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

final class FunctionNode extends AbstractNode {

  private final MathFunction function;
  // TODO Remove this initialization!!!
  private AbstractNode argument = ConstantNode.ZERO;

  FunctionNode(MathFunction function) {
    super(function.getName());
    this.function = function;
  }

  FunctionNode(Token token, MathFunction function) {
    super(token);
    this.function = function;
  }

  @Override
  public String toString() {
    return getName() + '(' + argument + ')';
  }

  AbstractNode getNode() {
    return argument;
  }

  @Override
  double evaluate(Map<String, ? extends Number> arg) {
    return function.value(argument.evaluate(arg));
  }

  @Override
  AbstractNode simplify() {
    if (argument.getVariables().isEmpty()) {
      return new ConstantNode(evaluate(null));
    }
    argument = argument.simplify();
    return this;
  }

  @Override
  AbstractNode derivative(String arg) {
    if (!getVariables().contains(arg)) {
      return ConstantNode.ZERO;
    }
    DerivativeRule<FunctionNode> rule = Functions.getRule(function);
    if (rule == null) {
      throw new ExpressionException("Unsupported Derivative", getToken());
    }
    return rule.derivative(this, arg);
  }

  @Override
  void setChildren(AbstractNode left, AbstractNode right) {
    if (Objects.nonNull(left)) {
      throw new ExpressionException("Unexpected token before function", left.getToken());
    }
    if (right == null) {
      throw new ExpressionException("Missing argument", getToken().nextError(1));
    }
    argument = right;
  }

  @Override
  Set<String> getVariables() {
    return argument.getVariables();
  }
}
