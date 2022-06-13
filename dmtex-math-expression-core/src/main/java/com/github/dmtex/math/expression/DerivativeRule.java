package com.github.dmtex.math.expression;

interface DerivativeRule<T extends AbstractNode> {

  AbstractNode derivative(T node, String arg);
}
