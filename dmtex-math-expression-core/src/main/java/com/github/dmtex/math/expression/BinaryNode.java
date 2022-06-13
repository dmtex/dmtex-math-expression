package com.github.dmtex.math.expression;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

final class BinaryNode extends AbstractNode {

  private final MathOperator operator;
  private AbstractNode leftNode;
  private AbstractNode rightNode;

  BinaryNode(MathOperator operator) {
    super(operator.getSymbol());
    this.operator = operator;
  }

  BinaryNode(Token token, MathOperator operator) {
    super(token);
    this.operator = operator;
  }

  @Override
  public String toString() {
    return String.format("(%s%s%s)", leftNode.toString(), getName(), rightNode.toString());
  }

  @Override
  double evaluate(Map<String, ? extends Number> arg) {
    return operator.evaluate(leftNode.evaluate(arg), rightNode.evaluate(arg));
  }

  @Override
  AbstractNode simplify() {
    if (leftNode.getVariables().isEmpty() && rightNode.getVariables().isEmpty()) {
      return new ConstantNode(evaluate(null));
    }
    leftNode = leftNode.simplify();
    rightNode = rightNode.simplify();
    SimplifyRule simplifier = Simplifiers.getSimplifier(operator);
    if (simplifier != null) {
      return simplifier.simplify(this);
    }
    return this;
  }

  @Override
  AbstractNode derivative(String arg) {
    if (!this.getVariables().contains(arg)) {
      return ConstantNode.ZERO;
    }
    DerivativeRule<BinaryNode> rule = Operators.getRule(this.operator);
    if (rule == null) {
      throw new ExpressionException("Unsupported Derivative", getToken());
    }
    return rule.derivative(this, arg);
  }

  @Override
  void setChildren(AbstractNode left, AbstractNode right) {
    if (right == null) {
      throw new ExpressionException("Missing right operand", getToken().nextError(0));
    }
    leftNode = left;
    rightNode = right;
    if (left == null) {
      if (!operator.isEmptyLeftOperandAllowed()) {
        throw new ExpressionException("Missing left operand", getToken());
      }
      leftNode = ConstantNode.ZERO;
    }
  }

  @Override
  Set<String> getVariables() {
    Set<String> dependencies = new HashSet<>();
    dependencies.addAll(leftNode.getVariables());
    dependencies.addAll(rightNode.getVariables());
    return dependencies;
  }

  int getPriority() {
    return operator.getPriority();
  }

  AbstractNode getLeftNode() {
    return leftNode;
  }

  AbstractNode getRightNode() {
    return rightNode;
  }
}
