package io.github.llewvallis.cfs.parser;

import io.github.llewvallis.cfs.reporting.Span;
import io.github.llewvallis.cfs.token.*;
import java.util.Map;
import java.util.Set;

/**
 * Given an input string, a lexer can produce tokens one by one. A lexer keeps tracks and mutates
 * its position in the input. Once the input is exhausted, the lexer produces an infinite sequence
 * of {@link EofToken}.
 *
 * <p>Generally you want to use a {@link TokenStream} instead, since a token stream is able to
 * rewind and gracefully handle errors.
 */
public class Lexer {

  /** Characters completely ignored by the lexer */
  private static final Set<Character> WHITESPACE = Set.of(' ', '\t', '\n', '\r');

  private static final Map<String, TokenFactory> SYMBOLS =
      Map.of(
          "(", OpenParenToken::new,
          ")", CloseParenToken::new,
          "{", OpenBraceToken::new,
          "}", CloseBraceToken::new,
          ";", SemicolonToken::new,
          ",", CommaToken::new,
          "=", EqualsToken::new);

  /** If an identifier matches one of these, we replace it using this table. */
  private static final Map<String, TokenFactory> KEYWORDS =
      Map.of(
          "int", KwIntToken::new,
          "return", KwReturnToken::new);

  private final String input;

  private int position = 0;

  public Lexer(String input) {
    this.input = input;
  }

  private interface TokenFactory {

    Token createToken(Span span);
  }

  public Token next() throws LexException {
    skipWhitespace();

    if (position >= input.length()) {
      return new EofToken(Span.point(input.length()));
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

    throw new LexException(position++, "unknown token");
  }

  /** Lex anything in the symbols table. */
  private Token lexSymbol() {
    for (var entry : SYMBOLS.entrySet()) {
      if (stringMatches(entry.getKey())) {
        var end = position + entry.getKey().length();
        var span = new Span(position, end);

        position = end;

        return entry.getValue().createToken(span);
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
    var currentPosition = this.position;
    while (currentPosition < input.length()
        && Character.isAlphabetic(input.charAt(currentPosition))) {
      currentPosition++;
    }

    if (currentPosition == this.position) {
      return null;
    }

    var content = input.substring(this.position, currentPosition);
    var span = new Span(position, currentPosition);

    this.position = currentPosition;

    var keyword = KEYWORDS.get(content);
    if (keyword == null) {
      return new IdentToken(span, content);
    } else {
      return keyword.createToken(span);
    }
  }

  private Token lexIntLiteral() {
    var currentPosition = this.position;
    var value = 0;

    while (currentPosition < input.length() && Character.isDigit(input.charAt(currentPosition))) {
      int digit = Character.digit(input.charAt(currentPosition), 10);
      value = value * 10 + digit;

      currentPosition++;
    }

    if (currentPosition == this.position) {
      return null;
    }

    var span = new Span(position, currentPosition);
    this.position = currentPosition;

    return new IntLiteralToken(span, value);
  }

  private void skipWhitespace() {
    while (position < input.length() && WHITESPACE.contains(input.charAt(position))) {
      position++;
    }
  }
}
