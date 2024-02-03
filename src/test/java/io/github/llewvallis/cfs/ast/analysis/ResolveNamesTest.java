package io.github.llewvallis.cfs.ast.analysis;

import static org.junit.jupiter.api.Assertions.*;

import io.github.llewvallis.cfs.ast.*;
import io.github.llewvallis.cfs.parser.Parser;
import io.github.llewvallis.cfs.reporting.CompileErrorsException;
import io.github.llewvallis.cfs.reporting.ErrorReporter;
import io.github.llewvallis.cfs.reporting.UndeclaredNameError;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ResolveNamesTest {

  private record Setup(ErrorReporter reporter, ProgramAst ast) {

    public static Setup setup(String source) throws CompileErrorsException {
      var reporter = new ErrorReporter();
      var ast = Parser.parseOrThrow(source);
      var collect = new CollectNames(reporter);
      ast.accept(collect);
      reporter.assertNoErrors();

      ast.accept(new ResolveNames(reporter, collect));

      return new Setup(reporter, ast);
    }
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "int main(int a) { a; }",
        "int main() { int a; a; }",
        "int main() { a; int a; }",
        "int main() { int a; int b; a; }",
        "int main(int a) { a = 42; }",
        "int main() { int a; a = 42; }",
        "int main() { a = 42; int a; }",
        "int main() { int a; int b; a = 42; }"
      })
  void resolvesVariableToDecl(String source) throws CompileErrorsException {
    var setup = Setup.setup(source);
    setup.reporter.assertNoErrors();

    var decl = setup.ast.findDescendant(VarDeclAst.class);
    var expr = setup.ast.findDescendant(VarExprAst.class);

    assertEquals(decl, expr.getDecl());
  }

  @ParameterizedTest
  @ValueSource(strings = {"int main() { a; }", "int main() { a = 42; }"})
  void reportsUndeclaredVariables(String source) throws CompileErrorsException {
    var setup = Setup.setup(source);
    var error = new UndeclaredNameError(new IdentAst(null, "a"));
    assertEquals(List.of(error), setup.reporter.getErrors());
  }
}
