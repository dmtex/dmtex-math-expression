package com.github.dmtex.math.expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@code Parser} class is responsible for expression parsing.
 *
 * @author Denis Murashev
 *
 * @since Math Expression Parser 1.0
 */
public final class Parser {

  private static final int PRIORITY_SHIFT = 4;
  private static final String OPEN_BRACKET = "(";
  private static final String CLOSED_BRACKET = ")";
  private final Values values = new Values();
  private final Functions functions = new Functions();
  private final Operators operators = new Operators();

  /**
   * Compiles given string into {@link Expression}.
   *
   * @param expression string expression
   * @param variables  variables
   * @return compiled expression
   */
  public Expression compile(String expression, Iterable<String> variables) {
    values.setVariables(variables);
    return compile(expression);
  }

  /**
   * Compiles given string into {@link Expression}.
   *
   * @param expression string expression
   * @return compiled expression
   */
  public Expression compile(String expression) {
    if (Objects.isNull(expression) || expression.isEmpty()) {
      throw new ExpressionException("Empty Expression", Token.defaultError(expression));
    }
    List<Token> tokens = getTokens(expression);
    int brackets = 0;
    for (Token token : tokens) {
      if (OPEN_BRACKET.equals(token.getValue())) {
        brackets++;
      } else if (CLOSED_BRACKET.equals(token.getValue())) {
        brackets--;
        if (brackets < 0) {
          throw new ExpressionException("Unexpected bracket", token);
        }
      } else {
        processToken(token, brackets);
      }
    }
    if (brackets > 0) {
      throw new ExpressionException("No expected bracket", Token.defaultError(expression));
    }
    return new Expression(createNode(tokens, 0, tokens.size() - 1));
  }

  private void processToken(Token token, int brackets) {
    AbstractNode node = operators.getNode(token);
    if (Objects.nonNull(node)) {
      token.setPriority(((BinaryNode) node).getPriority() + PRIORITY_SHIFT * brackets);
      token.setNode(node);
    } else {
      node = functions.getNode(token);
      if (Objects.nonNull(node)) {
        token.setPriority(PRIORITY_SHIFT * (brackets + 1));
        token.setNode(node);
      } else {
        try {
          token.setNode(values.getNode(token));
        } catch (NumberFormatException e) {
          throw new ExpressionException("Unexpected token: " + token.getValue(), token, e);
        }
      }
    }
  }

  private static List<Token> getTokens(String expression) {
    List<Token> list = new ArrayList<>();
    int position = 0;
    while (position < expression.length()) {
      String current = expression.substring(position);
      Pattern pattern = Pattern.compile("[+\\-*/%^()\\s]");
      Matcher matcher = pattern.matcher(current);
      if (!matcher.find()) {
        list.add(new Token(current.trim(), position, expression));
        return list;
      }
      String group = matcher.group().trim();
      int index = matcher.start();
      if (index > 0) {
        list.add(new Token(current.substring(0, index).trim(), position, expression));
        position += index;
      }

      if (!group.isEmpty()) {
        list.add(new Token(group, position, expression));
      }
      position += matcher.group().length();
    }
    return list;
  }

  private AbstractNode createNode(List<Token> tokens, int start, int finish) {
    if (start > finish) {
      return null;
    }
    int begin = start;
    int end = finish;
    while (begin < tokens.size() && OPEN_BRACKET.equals(tokens.get(begin).getValue())) {
      begin++;
    }
    while (end > 0 && CLOSED_BRACKET.equals(tokens.get(end).getValue())) {
      end--;
    }
    if (begin > end) {
      return null;
    }
    int minIndex = begin;
    for (int i = begin + 1; i <= end; ++i) {
      if (tokens.get(minIndex).getPriority() >= tokens.get(i).getPriority()) {
        minIndex = i;
      }
    }
    AbstractNode node = tokens.get(minIndex).getNode();
    AbstractNode left = createNode(tokens, begin, minIndex - 1);
    AbstractNode right = createNode(tokens, minIndex + 1, end);
    node.setChildren(left, right);
    return node;
  }
}
