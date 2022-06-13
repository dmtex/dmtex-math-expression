package com.github.dmtex.math.expression;

import java.util.function.DoubleBinaryOperator;

/**
 * {@code MathOperator} class represents mathematical operator.
 *
 * @author Denis Murashev
 *
 * @since Math Expression Parser 1.0
 */
public final class MathOperator {

  /**
   * To be moved.
   */
  static final int LOW_PRIORITY = 1;

  /**
   * To be moved.
   */
  static final int MEDIUM_PRIORITY = 2;

  /**
   * To be moved.
   */
  static final int HIGH_PRIORITY = 3;

  private final String symbol;
  private final int priority;
  private final boolean emptyLeftOperandAllowed;
  private final DoubleBinaryOperator operator;

  /**
   * Initializes with given values.
   *
   * @param symbol                  symbol
   * @param priority                priority
   * @param emptyLeftOperandAllowed flag
   * @param operator                operator
   */
  public MathOperator(String symbol, int priority, boolean emptyLeftOperandAllowed, DoubleBinaryOperator operator) {
    this.symbol = symbol;
    this.priority = priority;
    this.emptyLeftOperandAllowed = emptyLeftOperandAllowed;
    this.operator = operator;
  }

  /**
   * Initializes with given values.
   *
   * @param symbol   symbol
   * @param priority priority
   * @param operator operator
   */
  public MathOperator(String symbol, int priority, DoubleBinaryOperator operator) {
    this(symbol, priority, false, operator);
  }

  /**
   * Provides symbol.
   *
   * @return operator symbol
   */
  String getSymbol() {
    return symbol;
  }

  /**
   * Provides priority.
   *
   * @return operator priority
   */
  int getPriority() {
    return priority;
  }

  /**
   * Checks if left operand is allowed.
   *
   * @return {@code true} if left operand is allowed
   */
  boolean isEmptyLeftOperandAllowed() {
    return emptyLeftOperandAllowed;
  }

  /**
   * Evaluates operation.
   *
   * @param left  left operand
   * @param right right operand
   * @return result
   */
  double evaluate(double left, double right) {
    return operator.applyAsDouble(left, right);
  }
}
