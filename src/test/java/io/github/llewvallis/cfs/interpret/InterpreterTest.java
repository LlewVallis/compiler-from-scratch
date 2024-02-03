package io.github.llewvallis.cfs.interpret;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import io.github.llewvallis.cfs.CompilerDriver;
import io.github.llewvallis.cfs.reporting.CompileErrorsException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class InterpreterTest {

  @ParameterizedTest
  @MethodSource
  void interpretationProducesCorrectValue(String source, RValue expected, List<RValue> args)
      throws InterpretException, CompileErrorsException {
    var output = new CompilerDriver(source).interpret("main", args);
    assertEquals(expected, output);
  }

  static Stream<Arguments> interpretationProducesCorrectValue() {
    return Stream.of(
        intProgram("int main() { return 42; }", 42),
        intProgram("int main() { return 42; }", 42),
        intProgram("int main(int a) { return a; }", 42, 42),
        intProgram("int main() { int a; a = 42; return a; }", 42),
        intProgram("int main(int a) { a = 42; return a; }", 42, 17),
        intProgram("int main() { return 1 + 2; }", 3),
        intProgram("int main() { return 1 + 2 * 3; }", 7),
        intProgram(
            "int main() { return 1 + mul(2, mul(3, 4)); } int mul(int a, int b) { return a * b; }",
            25),
        intProgram("int main() { return 1 ? 42 : (1 / 0); }", 42),
        intProgram("int main() { return 0 ? (1 / 0) : 42; }", 42),
        intProgram("int main() { return 0 && (1 / 0); }", 0),
        intProgram("int main() { return 42 || (1 / 0); }", 42));
  }

  private static Arguments intProgram(String program, int returns, int... args) {
    var wrappedArgs = Arrays.stream(args).mapToObj(IntValue::new).collect(Collectors.toList());
    return arguments(program, new IntValue(returns), wrappedArgs);
  }

  @ParameterizedTest
  @MethodSource
  void interpretingErrorThrowsCorrectException(String source, List<RValue> args)
      throws CompileErrorsException {
    var ast = new CompilerDriver(source).analyze();
    var interpreter = new Interpreter(ast);

    assertThrows(InterpretException.class, () -> interpreter.run("main", args));
  }

  static Stream<Arguments> interpretingErrorThrowsCorrectException() {
    return Stream.of(
        arguments("int main() {}", List.of()),
        arguments("int main(int a) { return a; }", List.of()),
        arguments("int main() { int a; return a; }", List.of()),
        arguments("int main() { 1 / 0; }", List.of()));
  }
}
