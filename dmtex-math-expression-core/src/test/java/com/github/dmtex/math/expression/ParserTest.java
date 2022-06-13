package com.github.dmtex.math.expression;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ParserTest {

  private final Parser parser = new Parser();

  @Test
  void testCompileFailures() {
    assertAll(
        () -> testException(null, "^"),
        () -> testException("", "^"),
        () -> testException("a", "^"),
        () -> testException(")", "^"),
        () -> testException("(", "^"),
        () -> testException("*1", "^"),
        () -> testException("1 2", "^"),
        () -> testException("x+", "  ^", List.of("x")),
        () -> testException("sin ", "    ^"),
        () -> testException("sin()", "    ^"),
        () -> testException("sin( )", "    ^"),
        () -> testException("x sin", "^", List.of("x"))
    );
  }

  private void testException(String source, String markup, Iterable<String> args) {
    ExpressionException e = assertThrows(ExpressionException.class, () -> new Parser().compile(source, args));
    assertAll(
        () -> assertEquals(source, e.getExpression()),
        () -> assertEquals(markup, e.getMarker(), "For source: " + source)
    );
  }

  private void testException(String source, String markup) {
    ExpressionException e = assertThrows(ExpressionException.class, () -> new Parser().compile(source));
    assertAll(
        () -> assertEquals(source, e.getExpression()),
        () -> assertEquals(markup, e.getMarker(), "For source: " + source)
    );
  }

  @Test
  void testCompile() {
    assertAll(
        () -> assertEquals(Math.E, compile("e")),
        () -> assertEquals(0, compile("1+2-3")),
        () -> assertEquals(2, compile("1-2+3")),
        () -> assertEquals(7, compile("1+2*3")),
        () -> assertEquals(9, compile("3^2")),
        () -> assertEquals(1, compile("3%2")),
        () -> assertEquals(-1, compile("-1")),
        () -> assertEquals(1, compile("ctg(pi/4)"), 1e-15)
    );
  }

  private double compile(String source) {
    return parser.compile(source).evaluate();
  }

  @Test
  void testCompileArgs() {
    double actual = parser.compile("x", List.of("x")).evaluate(Map.of("x", 1));
    assertEquals(1, actual);
  }
}
