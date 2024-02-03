package io.github.llewvallis.cfs.parser;

import static io.github.llewvallis.cfs.parser.AstUtil.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import io.github.llewvallis.cfs.ast.*;
import io.github.llewvallis.cfs.reporting.*;
import io.github.llewvallis.cfs.token.*;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class ParserTest {

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
        arguments("int main() { a; }", exprProgram(varExpr("a"))),
        arguments(
            "int main(int a) {}",
            program(function("main", params("a", intTy()), intTy(), block()))),
        arguments(
            "int main() { return 42; }",
            program(function("main", params(), intTy(), block(returnStmt(literal(42)))))),
        arguments(
            "int main() { int a; }",
            program(function("main", params(), intTy(), block(varDeclStmt("a", intTy()))))),
        arguments("int main() { 42; }", exprProgram(literal(42))),
        arguments(
            "int main() { b = a = 42; }",
            exprProgram(assignment("b", assignment("a", literal(42))))),
        arguments("int main() { 1 + 2; }", exprProgram(add(literal(1), literal(2)))),
        arguments(
            "int main() { 1 + 2 * 3; }", exprProgram(add(literal(1), mul(literal(2), literal(3))))),
        arguments(
            "int main() { 1 * 2 + 3; }", exprProgram(add(mul(literal(1), literal(2)), literal(3)))),
        arguments(
            "int main() { 1 + 2 + 3; }", exprProgram(add(add(literal(1), literal(2)), literal(3)))),
        arguments(
            "int main() { 1 / 2 - 3 / 4; }",
            exprProgram(sub(div(literal(1), literal(2)), div(literal(3), literal(4))))),
        arguments("int main() { -a; }", exprProgram(neg(varExpr("a")))),
        arguments("int main() { -1 + 2; }", exprProgram(add(neg(literal(1)), literal(2)))),
        arguments("int main() { 1 + -2; }", exprProgram(add(literal(1), neg(literal(2))))),
        arguments("int main() { -(1 + 2); }", exprProgram(neg(add(literal(1), literal(2))))),
        arguments("int main() { -(1 * 2); }", exprProgram(neg(mul(literal(1), literal(2))))),
        arguments("int main() { ((((a)))); }", exprProgram(varExpr("a"))),
        arguments("int main() { foo(); }", exprProgram(call("foo"))),
        arguments(
            "int main() { foo(1 + 2, 3 * 4); }",
            exprProgram(call("foo", add(literal(1), literal(2)), mul(literal(3), literal(4))))),
        arguments(
            "int main() { foo() + bar() * bazz(); }",
            exprProgram(add(call("foo"), mul(call("bar"), call("bazz"))))),
        arguments("int main() { foo(bar()); }", exprProgram(call("foo", call("bar")))),
        arguments(
            "int main() { a ? b ? c : d : e ? f : g; }",
            exprProgram(
                ternary(
                    varExpr("a"),
                    ternary(varExpr("b"), varExpr("c"), varExpr("d")),
                    ternary(varExpr("e"), varExpr("f"), varExpr("g"))))));
  }

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
        "int main() {};",
        "int main() { -; }",
        "int main() { 1 + ; }",
        "int main() { (); }",
        "int main() { 1 * / 2; }",
        "int main(,) {}",
        "int main(int foo,) {}",
        "int main() { foo(int bar); }",
        "int main() { foo(,); }",
        "int main() { foo(bar,); }",
      })
  void invalidSyntaxIsRejected(String input) {
    assertThrows(CompileErrorsException.class, () -> Parser.parseOrThrow(input));
  }

  @ParameterizedTest
  @MethodSource
  void correctErrorsAreReported(String source, List<CompileError> errors) {
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
                new ParseError("SemicolonToken", new CloseBraceToken(null)))),
        arguments("int main() { 42 = 42; }", List.of(new NotAnLValueError(null))),
        arguments(
            "int main() { 1+; }", List.of(new ParseError("expression", new SemicolonToken(null)))),
        arguments(
            "int main() { (); }",
            List.of(new ParseError("expression", new CloseParenToken(null)))));
  }
}
