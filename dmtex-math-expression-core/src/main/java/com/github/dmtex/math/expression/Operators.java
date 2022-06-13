package com.github.dmtex.math.expression;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

final class Operators {

  static final MathOperator PLUS = new MathOperator("+", MathOperator.LOW_PRIORITY, true, Double::sum);

  static final MathOperator MINUS = new MathOperator("-", MathOperator.LOW_PRIORITY, true, (x, y) -> x - y);

  static final MathOperator MULTIPLY = new MathOperator("*", MathOperator.MEDIUM_PRIORITY, (x, y) -> x * y);

  static final MathOperator DIVIDE = new MathOperator("/", MathOperator.MEDIUM_PRIORITY, (x, y) -> x / y);

  static final MathOperator MODULO = new MathOperator("%", MathOperator.MEDIUM_PRIORITY, (x, y) -> x % y);

  static final MathOperator POWER = new MathOperator("^", MathOperator.HIGH_PRIORITY, Math::pow);

  private static final Map<MathOperator, DerivativeRule<BinaryNode>> RULES = new HashMap<>();

  static {
    RULES.put(PLUS, new PlusRule());
    RULES.put(MINUS, new MinusRule());
    RULES.put(MULTIPLY, new MultiplyRule());
    RULES.put(DIVIDE, new DivideRule());
    RULES.put(POWER, new PowerRule());
  }

  private final Map<String, MathOperator> operatorMap = new HashMap<>();

  Operators() {
    addOperator(PLUS);
    addOperator(MINUS);
    addOperator(MULTIPLY);
    addOperator(DIVIDE);
    addOperator(MODULO);
    addOperator(POWER);
  }

  BinaryNode getNode(Token token) {
    MathOperator operator = operatorMap.get(token.getValue());
    return Objects.nonNull(operator) ? new BinaryNode(token, operator) : null;
  }

  static DerivativeRule<BinaryNode> getRule(MathOperator operator) {
    return RULES.get(operator);
  }

  private void addOperator(MathOperator operator) {
    operatorMap.put(operator.getSymbol(), operator);
  }

  private static class PlusRule implements DerivativeRule<BinaryNode> {

    @Override
    public AbstractNode derivative(BinaryNode node, String arg) {
      BinaryNode result = new BinaryNode(Operators.PLUS);
      result.setChildren(node.getLeftNode().derivative(arg), node.getRightNode().derivative(arg)
      );
      return result;
    }
  }

  private static class MinusRule implements DerivativeRule<BinaryNode> {

    @Override
    public AbstractNode derivative(BinaryNode node, String arg) {
      BinaryNode result = new BinaryNode(Operators.MINUS);
      result.setChildren(node.getLeftNode().derivative(arg), node.getRightNode().derivative(arg));
      return result;
    }
  }

  private static class MultiplyRule implements DerivativeRule<BinaryNode> {

    @Override
    public AbstractNode derivative(BinaryNode node, String arg) {
      BinaryNode first = new BinaryNode(Operators.MULTIPLY);
      first.setChildren(node.getLeftNode().derivative(arg), node.getRightNode());
      BinaryNode second = new BinaryNode(Operators.MULTIPLY);
      second.setChildren(node.getLeftNode(), node.getRightNode().derivative(arg));
      BinaryNode result = new BinaryNode(Operators.PLUS);
      result.setChildren(first, second);
      return result;
    }
  }

  private static class DivideRule implements DerivativeRule<BinaryNode> {

    @Override
    public AbstractNode derivative(BinaryNode node, String arg) {
      BinaryNode first = new BinaryNode(Operators.MULTIPLY);
      first.setChildren(node.getLeftNode().derivative(arg), node.getRightNode());
      BinaryNode second = new BinaryNode(Operators.MULTIPLY);
      second.setChildren(node.getLeftNode(), node.getRightNode().derivative(arg));
      BinaryNode top = new BinaryNode(Operators.MINUS);
      top.setChildren(first, second);
      BinaryNode bottom = new BinaryNode(Operators.POWER);
      bottom.setChildren(node.getRightNode(), ConstantNode.TWO);
      BinaryNode result = new BinaryNode(Operators.DIVIDE);
      result.setChildren(top, bottom);
      return result;
    }
  }

  private static class PowerRule implements DerivativeRule<BinaryNode> {

    @Override
    public AbstractNode derivative(BinaryNode node, String arg) {
      return node.getRightNode().getVariables().contains(arg) ? complex(node, arg) : simple(node, arg);
    }

    private AbstractNode simple(BinaryNode node, String arg) {
      BinaryNode power = new BinaryNode(Operators.MINUS);
      power.setChildren(node.getRightNode(), ConstantNode.ONE);
      BinaryNode first = new BinaryNode(Operators.POWER);
      first.setChildren(node.getLeftNode(), power);
      BinaryNode second = new BinaryNode(Operators.MULTIPLY);
      second.setChildren(node.getRightNode(), first);
      BinaryNode result = new BinaryNode(Operators.MULTIPLY);
      result.setChildren(second, node.getLeftNode().derivative(arg));
      return result;
    }

    private AbstractNode complex(BinaryNode node, String arg) {
      FunctionNode function = new FunctionNode(Functions.LN);
      function.setChildren(null, node.getLeftNode());
      BinaryNode first = new BinaryNode(Operators.MULTIPLY);
      first.setChildren(node.getRightNode().derivative(arg), function);
      BinaryNode second = new BinaryNode(Operators.DIVIDE);
      second.setChildren(node.getRightNode(), node.getLeftNode());
      BinaryNode third = new BinaryNode(Operators.MULTIPLY);
      third.setChildren(second, node.getLeftNode().derivative(arg));
      BinaryNode fourth = new BinaryNode(Operators.PLUS);
      fourth.setChildren(first, third);
      BinaryNode fifth = new BinaryNode(Operators.POWER);
      fifth.setChildren(node.getLeftNode(), node.getRightNode());
      BinaryNode result = new BinaryNode(Operators.MULTIPLY);
      result.setChildren(fifth, fourth);
      return result;
    }
  }
}
