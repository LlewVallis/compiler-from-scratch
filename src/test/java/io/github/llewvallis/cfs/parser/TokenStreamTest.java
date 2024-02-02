package io.github.llewvallis.cfs.parser;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import io.github.llewvallis.cfs.reporting.ErrorReporter;
import io.github.llewvallis.cfs.reporting.TokenError;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class TokenStreamTest {

  @ParameterizedTest
  @MethodSource
  void errorsAreEmittedCorrectly(String source, List<TokenError> errors) {
    var reporter = new ErrorReporter();
    new TokenStream(reporter, new Lexer(source));
    assertEquals(reporter.getErrors(), errors);
  }

  static Stream<Arguments> errorsAreEmittedCorrectly() {
    return Stream.of(
        arguments("", List.of()),
        arguments("@", List.of(new TokenError(0))),
        arguments("@@", List.of(new TokenError(0))),
        arguments("@ @", List.of(new TokenError(0))),
        arguments("@ int @", List.of(new TokenError(0), new TokenError(6))));
  }

  @Test
  void copiesDoNotAffectOriginal() {
    var reporter = new ErrorReporter();
    var original = new TokenStream(reporter, new Lexer("int foo"));
    var copy = new TokenStream(original);
    assertEquals(copy.next(), original.next());
  }

  @Test
  void peekDoesNotAffectNext() {
    var reporter = new ErrorReporter();
    var stream = new TokenStream(reporter, new Lexer("int foo"));
    assertEquals(stream.peek(), stream.next());
  }
}
