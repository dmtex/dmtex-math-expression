package com.github.dmtex.math.expression;

interface SimplifyRule {

  AbstractNode simplify(BinaryNode node);
}
