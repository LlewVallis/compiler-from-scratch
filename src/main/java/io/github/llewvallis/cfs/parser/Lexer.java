package io.github.llewvallis.cfs.parser;

import io.github.llewvallis.cfs.token.*;
import java.util.Map;
import java.util.Set;

public class Lexer {

  private static final Set<Character> WHITESPACE = Set.of(' ', '\t', '\n', '\r');

  private static final Map<String, Token> SYMBOLS =
      Map.of(
          "(", new OpenParenToken(),
          ")", new CloseParenToken(),
          "{", new OpenBraceToken(),
          "}", new CloseBraceToken(),
          ";", new SemicolonToken(),
          ",", new CommaToken());

  private static final Map<String, Token> KEYWORDS =
      Map.of(
          "int", new KwIntToken(),
          "return", new KwReturnToken());

  private final String input;
  private int index = 0;

  public Lexer(String input) {
    this.input = input;
  }

  public Lexer(Lexer other) {
    this.input = other.input;
    this.index = other.index;
  }

  public Token peek() throws LexException {
    return new Lexer(this).next();
  }

  public Token next() throws LexException {
    skipWhitespace();

    if (index >= input.length()) {
      return new EofToken();
    }

    Token symbol = lexSymbol();
    if (symbol != null) {
      return symbol;
    }

    Token word = lexWord();
    if (word != null) {
      return word;
    }

    Token literal = lexLiteral();
    if (literal != null) {
      return literal;
    }

    throw new LexException();
  }

  private Token lexSymbol() {
    for (var entry : SYMBOLS.entrySet()) {
      if (stringMatches(entry.getKey())) {
        index += entry.getKey().length();
        return entry.getValue();
      }
    }

    return null;
  }

  private boolean stringMatches(String symbol) {
    for (var i = 0; i < symbol.length(); i++) {
      if (index + i >= input.length()) return false;
      if (symbol.charAt(i) != input.charAt(index + i)) return false;
    }

    return true;
  }

  private Token lexWord() {
    var index = this.index;
    while (index < input.length() && Character.isAlphabetic(input.charAt(index))) {
      index++;
    }

    if (index == this.index) {
      return null;
    }

    String content = input.substring(this.index, index);
    this.index = index;
    return KEYWORDS.getOrDefault(content, new IdentToken(content));
  }

  private Token lexLiteral() {
    var index = this.index;
    var value = 0;

    while (index < input.length() && Character.isDigit(input.charAt(index))) {
      int digit = Character.digit(input.charAt(index), 10);
      value = value * 10 + digit;

      index++;
    }

    if (index == this.index) {
      return null;
    }

    this.index = index;
    return new IntLiteralToken(value);
  }

  private void skipWhitespace() {
    while (index < input.length() && WHITESPACE.contains(input.charAt(index))) {
      index++;
    }
  }
}
