package com.github.dmtex.math.expression;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

final class Functions {

  static final MathFunction SQRT = new MathFunction("sqrt", Math::sqrt);

  static final MathFunction EXP = new MathFunction("exp", Math::exp);

  static final MathFunction LN = new MathFunction("ln", Math::log);

  static final MathFunction LOG = new MathFunction("log", Math::log10);

  static final MathFunction SIGN = new MathFunction("sign", Math::signum);

  static final MathFunction ABS = new MathFunction("abs", Math::abs);

  static final MathFunction SIN = new MathFunction("sin", Math::sin);

  static final MathFunction COS = new MathFunction("cos", Math::cos);

  static final MathFunction TG = new MathFunction("tg", Math::tan);

  static final MathFunction CTG = new MathFunction("ctg", x -> 1 / Math.tan(x));

  static final MathFunction ASIN = new MathFunction("arcsin", Math::asin);

  static final MathFunction ACOS = new MathFunction("arccos", Math::acos);

  static final MathFunction ATG = new MathFunction("arctg", Math::atan);

  static final MathFunction SH = new MathFunction("sh", Math::sinh);

  static final MathFunction CH = new MathFunction("ch", Math::cosh);

  static final MathFunction TH = new MathFunction("th", Math::tanh);

  private static final double HALF = 0.5;

  private static final Map<MathFunction, FunctionRule> RULES = new HashMap<>();

  private static final ConstantNode LOG10 = new ConstantNode(Math.log(10));

  private final Map<String, MathFunction> functionMap = new HashMap<>();

  static {
    RULES.put(SQRT, node -> binaryNode(new ConstantNode(HALF), Operators.DIVIDE, functionNode(SQRT, node.getNode())));
    RULES.put(EXP, node -> functionNode(EXP, node.getNode()));
    RULES.put(LN, node -> binaryNode(ConstantNode.ONE, Operators.DIVIDE, node.getNode()));
    RULES.put(LOG, node -> binaryNode(LOG10, Operators.DIVIDE, node.getNode()));
    RULES.put(SIGN, node -> ConstantNode.ZERO);
    RULES.put(ABS, node -> functionNode(SIGN, node.getNode()));
    RULES.put(SIN, node -> functionNode(COS, node.getNode()));
    RULES.put(COS, node -> binaryNode(ConstantNode.MINUS_ONE, Operators.MULTIPLY, functionNode(SIN, node.getNode())));
    RULES.put(TG, node -> {
      FunctionNode left = new FunctionNode(COS);
      left.setChildren(null, node.getNode());
      BinaryNode right = new BinaryNode(Operators.POWER);
      right.setChildren(left, ConstantNode.TWO);
      BinaryNode result = new BinaryNode(Operators.DIVIDE);
      result.setChildren(ConstantNode.ONE, right);
      return result;
    });
    RULES.put(CTG, node -> {
      FunctionNode left = new FunctionNode(SIN);
      left.setChildren(null, node.getNode());
      BinaryNode right = new BinaryNode(Operators.POWER);
      right.setChildren(left, ConstantNode.TWO);
      BinaryNode result = new BinaryNode(Operators.DIVIDE);
      result.setChildren(ConstantNode.MINUS_ONE, right);
      return result;
    });
    RULES.put(ASIN, new ArcRule(ConstantNode.ONE));
    RULES.put(ACOS, new ArcRule(ConstantNode.MINUS_ONE));
    RULES.put(ATG, node -> {
      BinaryNode right = new BinaryNode(Operators.MULTIPLY);
      right.setChildren(node.getNode(), node.getNode());
      AbstractNode bottom = new BinaryNode(Operators.PLUS);
      bottom.setChildren(ConstantNode.ONE, right);
      BinaryNode result = new BinaryNode(Operators.DIVIDE);
      result.setChildren(ConstantNode.ONE, bottom);
      return result;
    });
    RULES.put(SH, node -> functionNode(CH, node.getNode()));
    RULES.put(CH, node -> functionNode(SH, node.getNode()));
    RULES.put(TH, node -> {
      FunctionNode left = new FunctionNode(CH);
      left.setChildren(null, node.getNode());
      BinaryNode right = new BinaryNode(Operators.POWER);
      right.setChildren(left, ConstantNode.TWO);
      BinaryNode result = new BinaryNode(Operators.DIVIDE);
      result.setChildren(ConstantNode.ONE, right);
      return result;
    });
  }

  Functions() {
    addFunction(SQRT);
    addFunction(EXP);
    addFunction(LN);
    addFunction(LOG);
    addFunction(SIGN);
    addFunction(ABS);
    addFunction(SIN);
    addFunction(COS);
    addFunction(TG);
    addFunction(CTG);
    addFunction(ASIN);
    addFunction(ACOS);
    addFunction(ATG);
    addFunction(SH);
    addFunction(CH);
    addFunction(TH);
  }

  FunctionNode getNode(Token token) {
    MathFunction function = functionMap.get(token.getValue());
    return Objects.nonNull(function) ? new FunctionNode(token, function) : null;
  }

  static DerivativeRule<FunctionNode> getRule(MathFunction function) {
    return RULES.get(function);
  }

  private static FunctionNode functionNode(MathFunction function, AbstractNode argument) {
    FunctionNode result = new FunctionNode(function);
    result.setChildren(null, argument);
    return result;
  }

  private static BinaryNode binaryNode(AbstractNode left, MathOperator operator, AbstractNode right) {
    BinaryNode result = new BinaryNode(operator);
    result.setChildren(left, right);
    return result;
  }

  private void addFunction(MathFunction function) {
    functionMap.put(function.getName(), function);
  }

  private interface FunctionRule extends DerivativeRule<FunctionNode> {

    @Override
    default AbstractNode derivative(FunctionNode node, String arg) {
      BinaryNode result = new BinaryNode(Operators.MULTIPLY);
      result.setChildren(simpleDerivative(node), node.getNode().derivative(arg));
      return result;
    }

    AbstractNode simpleDerivative(FunctionNode node);
  }

  private static class ArcRule implements FunctionRule {

    private final ConstantNode constantNode;

    ArcRule(ConstantNode constantNode) {
      this.constantNode = constantNode;
    }

    @Override
    public AbstractNode simpleDerivative(FunctionNode node) {
      BinaryNode right = new BinaryNode(Operators.MULTIPLY);
      right.setChildren(node.getNode(), node.getNode());
      AbstractNode difference = new BinaryNode(Operators.MINUS);
      difference.setChildren(ConstantNode.ONE, right);
      AbstractNode bottom = new FunctionNode(SQRT);
      bottom.setChildren(null, difference);
      BinaryNode result = new BinaryNode(Operators.DIVIDE);
      result.setChildren(constantNode, bottom);
      return result;
    }
  }
}
