package com.github.dmtex.math.expression;

import java.util.Map;

/**
 * {@code Expression} class represents parsed and compiled mathematical expression.
 *
 * @author Denis Murashev
 *
 * @since Math Expression Parser 1.0
 */
public final class Expression {

  private final AbstractNode root;

  Expression(AbstractNode root) {
    this.root = root;
  }

  /**
   * Evaluates expression.
   *
   * @return evaluated expression
   */
  public double evaluate() {
    return root.evaluate(null);
  }

  /**
   * Evaluates expression.
   *
   * @param arg map of arguments
   * @return evaluated expression
   */
  public double evaluate(Map<String, ? extends Number> arg) {
    return root.evaluate(arg);
  }

  /**
   * Simplifies expression.
   *
   * @return simplified expression
   */
  public Expression simplify() {
    return new Expression(root.simplify());
  }

  /**
   * Calculates derivative.
   *
   * @param arg argument
   * @return derivative
   */
  public Expression derivative(String arg) {
    return new Expression(root.derivative(arg).simplify());
  }

  @Override
  public String toString() {
    return root.toString();
  }
}
