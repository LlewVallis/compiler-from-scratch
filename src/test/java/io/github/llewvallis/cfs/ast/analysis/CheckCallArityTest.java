package io.github.llewvallis.cfs.ast.analysis;

import io.github.llewvallis.cfs.parser.Parser;
import io.github.llewvallis.cfs.reporting.CallArityError;
import io.github.llewvallis.cfs.reporting.CompileErrorsException;
import io.github.llewvallis.cfs.reporting.ErrorReporter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class CheckCallArityTest {

  @ParameterizedTest
  @MethodSource
  void incorrectArityIsReported(String source, int expected, int actual)
      throws CompileErrorsException {
    var reporter = new ErrorReporter();
    var ast = Parser.parseOrThrow(source);

    var collectNames = new CollectNames(reporter);
    ast.accept(collectNames);
    ast.accept(new ResolveNames(reporter, collectNames));
    reporter.assertNoErrors();

    ast.accept(new CheckCallArity(reporter));

    assertEquals(List.of(new CallArityError(null, expected, actual)), reporter.getErrors());
  }

  static Stream<Arguments> incorrectArityIsReported() {
    return Stream.of(
        arguments("int main() { foo(1); } int foo() {}", 0, 1),
        arguments("int main() { foo(); } int foo(int a) {}", 1, 0),
        arguments("int main() { foo(1, 2); } int foo(int a) {}", 1, 2));
  }
}
