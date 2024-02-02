package io.github.llewvallis.cfs.ast.analysis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import io.github.llewvallis.cfs.ast.IdentAst;
import io.github.llewvallis.cfs.ast.VarDeclStmtAst;
import io.github.llewvallis.cfs.parser.Parser;
import io.github.llewvallis.cfs.reporting.CompileErrorsException;
import io.github.llewvallis.cfs.reporting.DuplicateNameError;
import io.github.llewvallis.cfs.reporting.ErrorReporter;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class CollectNamesTest {

  @ParameterizedTest
  @MethodSource
  void duplicatesDetected(String source, String ident) throws CompileErrorsException {
    var reporter = new ErrorReporter();
    var ast = Parser.parseOrThrow(source);

    ast.accept(new CollectNames(reporter));

    var expected = List.of(new DuplicateNameError(new IdentAst(null, ident)));
    assertEquals(expected, reporter.getErrors());
  }

  static Stream<Arguments> duplicatesDetected() {
    return Stream.of(
        arguments("int main() {} int main() {}", "main"),
        arguments("int main(int a, int a) {}", "a"),
        arguments("int main() { int a; int a; }", "a"),
        arguments("int main(int a) { int a; }", "a"));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "int foo() { int a; } int bar() { int a; }",
        "int foo(int a) {} int bar(int a) {}",
        "int foo(int foo) {}",
        "int foo() { int foo; }",
        "int main(int foo) {} int foo() {}",
        "int main() { int foo; } int foo() {}",
      })
  void falseDuplicatesNotDetected(String source) throws CompileErrorsException {
    var reporter = new ErrorReporter();
    var ast = Parser.parseOrThrow(source);

    ast.accept(new CollectNames(reporter));

    reporter.assertNoErrors();
  }

  @Test
  void functionNamesAreCollected() throws CompileErrorsException {
    var reporter = new ErrorReporter();
    var ast = Parser.parseOrThrow("int a() {} int b() {}");
    var collect = new CollectNames(reporter);
    ast.accept(collect);

    var expectedA = ast.getFunction("a");
    var expectedB = ast.getFunction("b");

    assertEquals(expectedA, collect.getFunction(new IdentAst(null, "a")));
    assertEquals(expectedB, collect.getFunction(new IdentAst(null, "b")));
    assertNull(collect.getFunction(new IdentAst(null, "c")));
  }

  @Test
  void variableNamesAreCollected() throws CompileErrorsException {
    var reporter = new ErrorReporter();
    var ast = Parser.parseOrThrow("int main(int a) { int b; }");
    var collect = new CollectNames(reporter);
    ast.accept(collect);

    var function = ast.getFunction("main");
    var expectedA = function.getParams().get(0);
    var expectedB = ((VarDeclStmtAst) function.getBody().getStmts().get(0)).getDecl();

    assertEquals(expectedA, collect.getVariable(function.getName(), new IdentAst(null, "a")));
    assertEquals(expectedB, collect.getVariable(function.getName(), new IdentAst(null, "b")));
    assertNull(collect.getVariable(function.getName(), new IdentAst(null, "c")));
  }

  @Test
  void collectedVariablesArePerFunction() throws CompileErrorsException {
    var reporter = new ErrorReporter();
    var ast = Parser.parseOrThrow("int main() {} int other(int a) { int b; }");
    var collect = new CollectNames(reporter);
    ast.accept(collect);

    var function = ast.getFunction("main");

    assertNull(collect.getVariable(function.getName(), new IdentAst(null, "a")));
    assertNull(collect.getVariable(function.getName(), new IdentAst(null, "b")));
  }
}
