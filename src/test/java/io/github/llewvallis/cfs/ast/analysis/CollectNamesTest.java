package io.github.llewvallis.cfs.ast.analysis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import io.github.llewvallis.cfs.ast.IdentAst;
import io.github.llewvallis.cfs.ast.VarDeclStmtAst;
import io.github.llewvallis.cfs.parser.ParseException;
import io.github.llewvallis.cfs.parser.Parser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CollectNamesTest {

  @Test
  void duplicateFunctionsReported() throws ParseException {
    var ast = Parser.parse("int main() {} int main() {}");
    Assertions.assertThrows(AnalysisException.class, () -> ast.accept(new CollectNames()));
  }

  @Test
  void duplicateParamsReported() throws ParseException {
    var ast = Parser.parse("int main(int a, int a) {}");
    Assertions.assertThrows(AnalysisException.class, () -> ast.accept(new CollectNames()));
  }

  @Test
  void duplicateVariablesReports() throws ParseException {
    var ast = Parser.parse("int main() { int a; int a; }");
    Assertions.assertThrows(AnalysisException.class, () -> ast.accept(new CollectNames()));
  }

  @Test
  void variableCannotShadowParam() throws ParseException {
    var ast = Parser.parse("int main(int a) { int a; }");
    Assertions.assertThrows(AnalysisException.class, () -> ast.accept(new CollectNames()));
  }

  @Test
  void functionsMayShadowVariables() throws ParseException, AnalysisException {
    var ast = Parser.parse("int main(int main) {}");
    ast.accept(new CollectNames());
  }

  @Test
  void differentFunctionsMayHaveSimilarVariables() throws ParseException, AnalysisException {
    var ast = Parser.parse("int a() { int foo; } int b() { int foo; }");
    ast.accept(new CollectNames());
  }

  @Test
  void functionNamesAreCollected() throws ParseException, AnalysisException {
    var ast = Parser.parse("int a() {} int b() {}");
    var collect = new CollectNames();
    ast.accept(collect);

    var expectedA = ast.getFunction("a");
    var expectedB = ast.getFunction("b");

    assertEquals(expectedA, collect.getFunction(new IdentAst("a")));
    assertEquals(expectedB, collect.getFunction(new IdentAst("b")));
    assertNull(collect.getFunction(new IdentAst("c")));
  }

  @Test
  void variableNamesAreCollected() throws ParseException, AnalysisException {
    var ast = Parser.parse("int main(int a) { int b; }");
    var collect = new CollectNames();
    ast.accept(collect);

    var function = ast.getFunction("main");
    var expectedA = function.getParams().get(0);
    var expectedB = ((VarDeclStmtAst) function.getBody().getStmts().get(0)).getDecl();

    assertEquals(expectedA, collect.getVariable(function.getName(), new IdentAst("a")));
    assertEquals(expectedB, collect.getVariable(function.getName(), new IdentAst("b")));
    assertNull(collect.getVariable(function.getName(), new IdentAst("c")));
  }

  @Test
  void collectedVariablesArePerFunction() throws ParseException, AnalysisException {
    var ast = Parser.parse("int main() {} int other(int a) { int b; }");
    var collect = new CollectNames();
    ast.accept(collect);

    var function = ast.getFunction("main");

    assertNull(collect.getVariable(function.getName(), new IdentAst("a")));
    assertNull(collect.getVariable(function.getName(), new IdentAst("b")));
  }
}
