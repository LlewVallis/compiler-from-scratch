package io.github.llewvallis.cfs.interpret;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import io.github.llewvallis.cfs.ast.analysis.AnalysisException;
import io.github.llewvallis.cfs.ast.analysis.Analyzer;
import io.github.llewvallis.cfs.parser.ParseException;
import io.github.llewvallis.cfs.parser.Parser;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class InterpreterTest {

  @ParameterizedTest
  @MethodSource
  void interpretationProducesCorrectValue(String source, Value expected, List<Value> args)
      throws ParseException, AnalysisException, InterpretException {
    var ast = Parser.parse(source);
    new Analyzer().analyze(ast);
    var interpreter = new Interpreter(ast);
    var output = interpreter.run("main", args);

    assertEquals(expected, output);
  }

  static Stream<Arguments> interpretationProducesCorrectValue() {
    return Stream.of(
        arguments("int main() { return 42; }", new IntValue(42), List.of()),
        arguments("int main(int a) { return a; }", new IntValue(42), List.of(new IntValue(42))),
        arguments("int main() { int a; a = 42; return a; }", new IntValue(42), List.of()),
        arguments(
            "int main(int a) { a = 42; return a; }", new IntValue(42), List.of(new IntValue(17))));
  }

  @ParameterizedTest
  @MethodSource
  void interpretingErrorThrowsCorrectException(String source, List<Value> args)
      throws ParseException, AnalysisException {
    var ast = Parser.parse(source);
    new Analyzer().analyze(ast);
    var interpreter = new Interpreter(ast);

    assertThrows(InterpretException.class, () -> interpreter.run("main", args));
  }

  static Stream<Arguments> interpretingErrorThrowsCorrectException() {
    return Stream.of(
        arguments("int main() {}", List.of()),
        arguments("int main(int a) { return a; }", List.of()),
        arguments("int main() { int a; return a; }", List.of()));
  }
}