package com.github.dmtex.math.expression;

/**
 * {@code ExpressionException} class is general exception of Math Expression Parser module.
 *
 * @author Denis Murashev
 *
 * @since Math Expression Parser 1.0
 */
public final class ExpressionException extends RuntimeException {

  private static final long serialVersionUID = 2455059089281385021L;

  private final transient Token token;

  /**
   * Initializes with given values.
   *
   * @param message message
   * @param token   token
   */
  ExpressionException(String message, Token token) {
    super(message);
    this.token = token;
  }

  /**
   * Initializes with given values.
   *
   * @param message message
   * @param token   token
   * @param cause   cause
   */
  ExpressionException(String message, Token token, Throwable cause) {
    super(message, cause);
    this.token = token;
  }

  /**
   * Provides expression.
   *
   * @return expression
   */
  public String getExpression() {
    return token.getExpression();
  }

  /**
   * Provides position.
   *
   * @return position
   */
  public int getPosition() {
    return token.getPosition();
  }

  /**
   * Provides length.
   *
   * @return length
   */
  public int getLength() {
    return token.getValue().length();
  }

  /**
   * Provides marker.
   *
   * @return marker
   */
  public String getMarker() {
    final char c = '\0';
    return new String(new char[getPosition()]).replace(c, ' ') + new String(new char[getLength()]).replace(c, '^');
  }
}
