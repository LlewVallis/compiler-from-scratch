package io.github.llewvallis.cfs.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import io.github.llewvallis.cfs.ast.*;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ParserTest {

  @ParameterizedTest
  @MethodSource
  void invalidSyntaxIsRejected(String input) {
    assertThrows(ParseException.class, () -> Parser.parse(input));
  }

  static Stream<Arguments> invalidSyntaxIsRejected() {
    return Stream.of(
        arguments("main() {}"),
        arguments("int int() {}"),
        arguments("int main {}"),
        arguments("int main()"),
        arguments("int main() { 42 = 42; }"));
  }

  @ParameterizedTest
  @MethodSource
  void validSyntaxIsParsedCorrectly(String input, Ast expected) throws ParseException {
    var actual = Parser.parse(input);
    assertEquals(expected, actual);
  }

  static Stream<Arguments> validSyntaxIsParsedCorrectly() {
    return Stream.of(
        arguments("", new ProgramAst(List.of())),
        arguments(
            "int main() {}",
            new ProgramAst(
                List.of(
                    new FunctionAst(
                        new IdentAst("main"),
                        List.of(),
                        new IntTyAst(),
                        new BlockAst(List.of()))))),
        arguments(
            "int main(int a) {}",
            new ProgramAst(
                List.of(
                    new FunctionAst(
                        new IdentAst("main"),
                        List.of(new VarDeclAst(new IdentAst("a"), new IntTyAst())),
                        new IntTyAst(),
                        new BlockAst(List.of()))))),
        arguments(
            "int main() { return 42; }",
            new ProgramAst(
                List.of(
                    new FunctionAst(
                        new IdentAst("main"),
                        List.of(),
                        new IntTyAst(),
                        new BlockAst(List.of(new ReturnStmtAst(new IntLiteralExprAst(42)))))))),
        arguments(
            "int main() { int a; }",
            new ProgramAst(
                List.of(
                    new FunctionAst(
                        new IdentAst("main"),
                        List.of(),
                        new IntTyAst(),
                        new BlockAst(
                            List.of(
                                new VarDeclStmtAst(
                                    new VarDeclAst(new IdentAst("a"), new IntTyAst())))))))),
        arguments(
            "int main() { 42; }",
            new ProgramAst(
                List.of(
                    new FunctionAst(
                        new IdentAst("main"),
                        List.of(),
                        new IntTyAst(),
                        new BlockAst(List.of(new ExprStmtAst(new IntLiteralExprAst(42)))))))),
        arguments(
            "int main() { b = a = 42; }",
            new ProgramAst(
                List.of(
                    new FunctionAst(
                        new IdentAst("main"),
                        List.of(),
                        new IntTyAst(),
                        new BlockAst(
                            List.of(
                                new ExprStmtAst(
                                    new AssignmentExprAst(
                                        new IdentAst("b"),
                                        new AssignmentExprAst(
                                            new IdentAst("a"), new IntLiteralExprAst(42)))))))))));
  }
}
