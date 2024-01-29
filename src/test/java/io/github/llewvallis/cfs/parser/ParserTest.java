package io.github.llewvallis.cfs.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.llewvallis.cfs.ast.*;
import io.github.llewvallis.cfs.graphviz.GraphvizBuilder;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ParserTest {

  @ParameterizedTest
  @MethodSource
  void invalidSyntaxIsRejected(String input) {
    var parser = new Parser(new Lexer(input));
    assertThrows(ParseException.class, parser::parseProgram);
  }

  static Stream<Arguments> invalidSyntaxIsRejected() {
    return Stream.of(
        Arguments.of("main() {}"),
        Arguments.of("int int() {}"),
        Arguments.of("int main {}"),
        Arguments.of("int main()"));
  }

  @ParameterizedTest
  @MethodSource
  void validSyntaxIsParsedCorrectly(String input, AstNode expected) throws ParseException {
    var parser = new Parser(new Lexer(input));
    var actual = parser.parseProgram();
    assertEquals(expected, actual);
  }

  static Stream<Arguments> validSyntaxIsParsedCorrectly() {
    return Stream.of(
        Arguments.of("", new ProgramAst(List.of())),
        Arguments.of(
            "int main() {}",
            new ProgramAst(
                List.of(
                    new FunctionAst(
                        new IdentAst("main"),
                        List.of(),
                        new IntTyAst(),
                        new BlockAst(List.of()))))),
        Arguments.of(
            "int main(int a) {}",
            new ProgramAst(
                List.of(
                    new FunctionAst(
                        new IdentAst("main"),
                        List.of(new ParamAst(new IdentAst("a"), new IntTyAst())),
                        new IntTyAst(),
                        new BlockAst(List.of()))))),
        Arguments.of(
            "int main() { return 42; }",
            new ProgramAst(
                List.of(
                    new FunctionAst(
                        new IdentAst("main"),
                        List.of(),
                        new IntTyAst(),
                        new BlockAst(List.of(new ReturnStmtAst(new IntLiteralExpr(42)))))))));
  }
}
