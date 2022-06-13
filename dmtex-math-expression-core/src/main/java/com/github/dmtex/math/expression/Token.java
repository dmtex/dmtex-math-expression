package com.github.dmtex.math.expression;

class Token {

  private static final String BLANK = " ";

  private final String value;
  private final int position;
  private final String expression;
  private int priority = Integer.MAX_VALUE;
  private AbstractNode node;

  Token(String value, int position, String expression) {
    this.value = value;
    this.position = position;
    this.expression = expression;
  }

  String getValue() {
    return value;
  }

  int getPosition() {
    return position;
  }

  String getExpression() {
    return expression;
  }

  int getPriority() {
    return priority;
  }

  void setPriority(Integer priority) {
    this.priority = priority;
  }

  AbstractNode getNode() {
    return node;
  }

  void setNode(AbstractNode node) {
    this.node = node;
  }

  Token nextError(int shift) {
    return new Token(BLANK, position + value.length() + shift, expression);
  }

  static Token defaultError(String expression) {
    return new Token(BLANK, 0, expression);
  }
}
