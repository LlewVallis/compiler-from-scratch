package io.github.llewvallis.cfs.ast.analysis;

import static org.junit.jupiter.api.Assertions.*;

import io.github.llewvallis.cfs.ast.AssignmentExprAst;
import io.github.llewvallis.cfs.ast.VarDeclAst;
import io.github.llewvallis.cfs.ast.VarExprAst;
import io.github.llewvallis.cfs.parser.ParseException;
import io.github.llewvallis.cfs.parser.Parser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ResolveNamesTest {

  @ParameterizedTest
  @ValueSource(
      strings = {
        "int main(int a) { a; }",
        "int main() { int a; a; }",
        "int main() { a; int a; }",
        "int main() { int a; int b; a; }"
      })
  void resolvesVariableToDecl(String source) throws ParseException, AnalysisException {
    var ast = Parser.parse(source);
    var collect = new CollectNames();
    ast.accept(collect);
    ast.accept(new ResolveNames(collect));

    var decl = ast.findDescendant(VarDeclAst.class);
    var expr = ast.findDescendant(VarExprAst.class);

    assertEquals(decl, expr.getDecl());
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "int main(int a) { a = 42; }",
        "int main() { int a; a = 42; }",
        "int main() { a = 42; int a; }",
        "int main() { int a; int b; a = 42; }"
      })
  void resolvesAssignmentToDecl(String source) throws ParseException, AnalysisException {
    var ast = Parser.parse(source);
    var collect = new CollectNames();
    ast.accept(collect);
    ast.accept(new ResolveNames(collect));

    var decl = ast.findDescendant(VarDeclAst.class);
    var expr = ast.findDescendant(AssignmentExprAst.class);

    assertEquals(decl, expr.getDecl());
  }

  @ParameterizedTest
  @ValueSource(strings = {"int main() { a; }", "int main() { a = 42; }"})
  void reportsUndeclaredVariables(String source) throws ParseException, AnalysisException {
    var ast = Parser.parse(source);
    var collect = new CollectNames();
    ast.accept(collect);

    assertThrows(AnalysisException.class, () -> ast.accept(new ResolveNames(collect)));
  }
}
