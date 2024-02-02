package io.github.llewvallis.cfs.parser;

import static org.junit.jupiter.api.Assertions.*;

import io.github.llewvallis.cfs.token.*;
import org.junit.jupiter.api.Test;

class LexerTest {

  @Test
  void emptyInputGivesEof() throws LexException {
    var lexer = new Lexer("");
    assertInstanceOf(EofToken.class, lexer.next());
  }

  @Test
  void justWhitespaceInputGivesEof() throws LexException {
    var lexer = new Lexer(" ");
    assertInstanceOf(EofToken.class, lexer.next());
  }

  @Test
  void whitespaceIsSkippedBetweenTokens() throws LexException {
    var lexer = new Lexer("int foo return");

    assertInstanceOf(KwIntToken.class, lexer.next());
    assertInstanceOf(IdentToken.class, lexer.next());
    assertInstanceOf(KwReturnToken.class, lexer.next());
    assertInstanceOf(EofToken.class, lexer.next());
  }

  @Test
  void eofOccursRepeatedlyAfterNormalTokens() throws LexException {
    var lexer = new Lexer("int");

    lexer.next();
    assertInstanceOf(EofToken.class, lexer.next());
    assertInstanceOf(EofToken.class, lexer.next());
  }

  @Test
  void identWithKeywordPrefixIsNotKeyword() throws LexException {
    var lexer = new Lexer("intl");
    assertInstanceOf(IdentToken.class, lexer.next());
  }

  @Test
  void keywordsAreLexedCorrectly() throws LexException {
    var lexer = new Lexer("int");
    assertInstanceOf(KwIntToken.class, lexer.next());
  }

  @Test
  void identsHaveCorrectContent() throws LexException {
    var lexer = new Lexer("foobar");
    assertEquals("foobar", ((IdentToken) lexer.next()).getContent());
  }

  @Test
  void intLiteralsHaveCorrectValue() throws LexException {
    var lexer = new Lexer("12345");
    assertEquals(12345, ((IntLiteralToken) lexer.next()).getValue());
  }

  @Test
  void unknownTokenThrows() {
    var lexer = new Lexer("@");
    assertThrows(LexException.class, lexer::next);
  }
}
