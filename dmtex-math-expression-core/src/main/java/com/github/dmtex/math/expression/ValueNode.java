package com.github.dmtex.math.expression;

abstract class ValueNode extends AbstractNode {

  ValueNode(Token token) {
    super(token);
  }

  ValueNode(String name) {
    super(name);
  }

  @Override
  AbstractNode simplify() {
    return this;
  }

  @Override
  void setChildren(AbstractNode left, AbstractNode right) {
    if (left != null) {
      throw exception(left);
    }
    if (right != null) {
      throw exception(right);
    }
  }

  private ExpressionException exception(AbstractNode node) {
    return new ExpressionException("Unexpected part: ", node.getToken());
  }
}
