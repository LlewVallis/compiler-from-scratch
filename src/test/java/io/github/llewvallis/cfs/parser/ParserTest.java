package io.github.llewvallis.cfs.parser;

import static io.github.llewvallis.cfs.parser.AstUtil.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import io.github.llewvallis.cfs.ast.*;
import io.github.llewvallis.cfs.reporting.CompileErrorsException;
import io.github.llewvallis.cfs.reporting.ErrorReporter;
import io.github.llewvallis.cfs.reporting.ParseError;
import io.github.llewvallis.cfs.token.CloseBraceToken;
import io.github.llewvallis.cfs.token.OpenBraceToken;
import io.github.llewvallis.cfs.token.OpenParenToken;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class ParserTest {

  @ParameterizedTest
  @ValueSource(
      strings = {
        "main() {}",
        "int int() {}",
        "int main {}",
        "int main(int) {}",
        "int main() { int a }",
        "int main()",
        "int main() { 42 = 42; }",
        "int main() {};"
      })
  void invalidSyntaxIsRejected(String input) {
    assertThrows(CompileErrorsException.class, () -> Parser.parseOrThrow(input));
  }

  @ParameterizedTest
  @MethodSource
  void validSyntaxIsParsedCorrectly(String input, Ast expected) throws CompileErrorsException {
    var actual = Parser.parseOrThrow(input);
    assertEquals(expected, actual);
  }

  static Stream<Arguments> validSyntaxIsParsedCorrectly() {
    return Stream.of(
        arguments("", program()),
        arguments("int main() {}", program(function("main", params(), intTy(), block()))),
        arguments(
            "int main() { a; }",
            program(function("main", params(), intTy(), block(exprStmt(varExpr("a")))))),
        arguments(
            "int main(int a) {}",
            program(function("main", params("a", intTy()), intTy(), block()))),
        arguments(
            "int main() { return 42; }",
            program(function("main", params(), intTy(), block(returnStmt(literal(42)))))),
        arguments(
            "int main() { int a; }",
            program(function("main", params(), intTy(), block(varDeclStmt("a", intTy()))))),
        arguments(
            "int main() { 42; }",
            program(function("main", params(), intTy(), block(exprStmt(literal(42)))))),
        arguments(
            "int main() { b = a = 42; }",
            program(
                function(
                    "main",
                    params(),
                    intTy(),
                    block(exprStmt(assignment("b", assignment("a", literal(42)))))))));
  }

  @ParameterizedTest
  @MethodSource
  void correctErrorsAreReported(String source, List<ParseError> errors) {
    var reporter = new ErrorReporter();
    Parser.parse(reporter, source);
    assertEquals(errors, reporter.getErrors());
  }

  static Stream<Arguments> correctErrorsAreReported() {
    return Stream.of(
        arguments("int () {}", List.of(new ParseError("IdentToken", new OpenParenToken(null)))),
        arguments("int {}", List.of(new ParseError("IdentToken", new OpenBraceToken(null)))),
        arguments(
            "int main { a = 42 }",
            List.of(
                new ParseError("OpenParenToken", new OpenBraceToken(null)),
                new ParseError("SemicolonToken", new CloseBraceToken(null)))));
  }
}
