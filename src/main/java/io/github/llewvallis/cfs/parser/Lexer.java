package io.github.llewvallis.cfs.parser;

import io.github.llewvallis.cfs.token.*;
import java.util.Map;
import java.util.Set;

/**
 * Given an input string, a lexer can produce tokens one by one. A lexer keeps tracks and mutates
 * its position in the input. Once the input is exhausted, the lexer produces an infinite sequence
 * of {@link EofToken}.
 */
public class Lexer {

  /** Characters completely ignored by the lexer */
  private static final Set<Character> WHITESPACE = Set.of(' ', '\t', '\n', '\r');

  private static final Map<String, Token> SYMBOLS =
      Map.of(
          "(", new OpenParenToken(),
          ")", new CloseParenToken(),
          "{", new OpenBraceToken(),
          "}", new CloseBraceToken(),
          ";", new SemicolonToken(),
          ",", new CommaToken(),
          "=", new EqualsToken());

  /** If an identifier matches one of these, we replace it using this table. */
  private static final Map<String, Token> KEYWORDS =
      Map.of(
          "int", new KwIntToken(),
          "return", new KwReturnToken());

  private final String input;
  private int position = 0;

  public Lexer(String input) {
    this.input = input;
  }

  /**
   * Clones the lexer so further advancing the new one will not affect the old one and vice versa.
   */
  public Lexer(Lexer other) {
    this.input = other.input;
    this.position = other.position;
  }

  /** Like {@link #next()} but doesn't change the input position. */
  public Token peek() throws LexException {
    return new Lexer(this).next();
  }

  public Token next() throws LexException {
    skipWhitespace();

    if (position >= input.length()) {
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

    Token literal = lexIntLiteral();
    if (literal != null) {
      return literal;
    }

    throw new LexException("unknown token");
  }

  /** Lex anything in the symbols table. */
  private Token lexSymbol() {
    for (var entry : SYMBOLS.entrySet()) {
      if (stringMatches(entry.getKey())) {
        position += entry.getKey().length();
        return entry.getValue();
      }
    }

    return null;
  }

  private boolean stringMatches(String symbol) {
    for (var i = 0; i < symbol.length(); i++) {
      if (position + i >= input.length()) return false;
      if (symbol.charAt(i) != input.charAt(position + i)) return false;
    }

    return true;
  }

  /** Lex either an identifier or keyword. */
  private Token lexWord() {
    var index = this.position;
    while (index < input.length() && Character.isAlphabetic(input.charAt(index))) {
      index++;
    }

    if (index == this.position) {
      return null;
    }

    String content = input.substring(this.position, index);
    this.position = index;
    return KEYWORDS.getOrDefault(content, new IdentToken(content));
  }

  private Token lexIntLiteral() {
    var index = this.position;
    var value = 0;

    while (index < input.length() && Character.isDigit(input.charAt(index))) {
      int digit = Character.digit(input.charAt(index), 10);
      value = value * 10 + digit;

      index++;
    }

    if (index == this.position) {
      return null;
    }

    this.position = index;
    return new IntLiteralToken(value);
  }

  private void skipWhitespace() {
    while (position < input.length() && WHITESPACE.contains(input.charAt(position))) {
      position++;
    }
  }
}
