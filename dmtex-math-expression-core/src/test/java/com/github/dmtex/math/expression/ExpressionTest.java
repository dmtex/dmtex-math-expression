package com.github.dmtex.math.expression;

import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExpressionTest {

  private final Parser parser = new Parser();
  private final Set<String> args = Set.of("x", "y", "z");

  @Test
  void testSimplify() {
    assertAll(
        () -> assertEquals("x", simplify("0+x")),
        () -> assertEquals("x", simplify("x-0")),
        () -> assertEquals("0", simplify("0*x")),
        () -> assertEquals("x", simplify("1*x")),
        () -> assertEquals("0.0", simplify("sin(0)"))
    );
  }

  private String simplify(String source) {
    return parser.compile(source, args).simplify().toString();
  }

  @Test
  void testDerivative() {
    assertAll(
        () -> assertEquals("0", derivative("1")),
        () -> assertEquals("2.0", derivative("2*x")),
        () -> assertEquals("(((3*(x^2.0))+(2*(x^1.0)))-1)", derivative("x^3+x^2-x+1")),
        () -> assertEquals("(cos((x/2))*(2.0/4.0))", derivative("sin(x/2)")),
        () -> assertEquals("(e^x)", derivative("e^x")),
        () -> assertEquals("(0.5/sqrt(x))", derivative("sqrt(x)")),
        () -> assertEquals("exp(x)", derivative("exp(x)")),
        () -> assertEquals("(1/x)", derivative("ln(x)")),
        () -> assertEquals("(2.302585092994046/x)", derivative("log(x)")),
        () -> assertEquals("cos(x)", derivative("sin(x)")),
        () -> assertEquals("(-1*sin(x))", derivative("cos(x)")),
        () -> assertEquals("(1/(cos(x)^2))", derivative("tg(x)")),
        () -> assertEquals("(-1/(sin(x)^2))", derivative("ctg(x)")),
        () -> assertEquals("(1/sqrt((1-(x*x))))", derivative("arcsin(x)")),
        () -> assertEquals("(-1/sqrt((1-(x*x))))", derivative("arccos(x)")),
        () -> assertEquals("(1/(1+(x*x)))", derivative("arctg(x)")),
        () -> assertEquals("ch(x)", derivative("sh(x)")),
        () -> assertEquals("sh(x)", derivative("ch(x)")),
        () -> assertEquals("(1/(ch(x)^2))", derivative("th(x)")),
        () -> assertEquals("sign(x)", derivative("abs(x)")),
        () -> assertEquals("0.0", derivative("sign(x)")),
        () -> assertEquals("0", derivative("y")),
        () -> assertEquals("0", derivative("y+z")),
        () -> assertEquals("0", derivative("sin(y)")),
        () -> assertEquals("1.0", derivative("x+y"))
    );
  }

  private String derivative(String source) {
    return parser.compile(source, args).derivative("x").toString();
  }

  @Test
  void testUnsupportedDerivative() {
    assertThrows(ExpressionException.class, () -> parser.compile("sin(x) % x", args).derivative("x"));
  }
}
