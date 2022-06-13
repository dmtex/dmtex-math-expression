package com.github.dmtex.math.expression;

import java.util.HashMap;
import java.util.Map;

final class Simplifiers {

  private static final Map<MathOperator, SimplifyRule> SIMPLIFIER_RULES = new HashMap<>();

  static {
    SIMPLIFIER_RULES.put(Operators.PLUS, new PlusSimplifier());
    SIMPLIFIER_RULES.put(Operators.MINUS, new MinusSimplifier());
    SIMPLIFIER_RULES.put(Operators.MULTIPLY, new MultiplySimplifier());
  }

  private Simplifiers() {
  }

  static SimplifyRule getSimplifier(MathOperator operator) {
    return SIMPLIFIER_RULES.get(operator);
  }

  private static class PlusSimplifier implements SimplifyRule {

    @Override
    public AbstractNode simplify(BinaryNode node) {
      AbstractNode left = node.getLeftNode();
      if (left.getVariables().isEmpty() && left.evaluate(null) == 0) {
        return node.getRightNode().simplify();
      }
      AbstractNode right = node.getRightNode();
      return right.getVariables().isEmpty() && right.evaluate(null) == 0 ? node.getLeftNode().simplify() : node;
    }
  }

  private static class MinusSimplifier implements SimplifyRule {

    @Override
    public AbstractNode simplify(BinaryNode node) {
      AbstractNode right = node.getRightNode();
      return right.getVariables().isEmpty() && right.evaluate(null) == 0 ? node.getLeftNode().simplify() : node;
    }
  }

  private static class MultiplySimplifier implements SimplifyRule {

    @Override
    public AbstractNode simplify(BinaryNode node) {
      AbstractNode left = node.getLeftNode();
      if (left.getVariables().isEmpty()) {
        double value = left.evaluate(null);
        if (value == 0) {
          return new ConstantNode(0);
        }
        if (value == 1) {
          return node.getRightNode().simplify();
        }
      }
      AbstractNode right = node.getRightNode();
      if (right.getVariables().isEmpty()) {
        double value = right.evaluate(null);
        if (value == 0) {
          return new ConstantNode(0);
        }
        if (value == 1) {
          return node.getLeftNode().simplify();
        }
      }
      return node;
    }
  }
}
