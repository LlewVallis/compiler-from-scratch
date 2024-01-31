package io.github.llewvallis.cfs.ast.analysis;

import io.github.llewvallis.cfs.ast.FunctionAst;
import io.github.llewvallis.cfs.ast.IdentAst;
import io.github.llewvallis.cfs.ast.VarDeclAst;
import java.util.HashMap;
import java.util.Map;

/** Maintains an index of all identifiers and reports duplicate identifiers. */
public class CollectNames implements AstVisitor {

  private final Map<IdentAst, FunctionAst> functions = new HashMap<>();

  private final Map<NameInFunction, VarDeclAst> variables = new HashMap<>();

  private record NameInFunction(IdentAst function, IdentAst variable) {}

  public FunctionAst getFunction(IdentAst ident) {
    return functions.get(ident);
  }

  public VarDeclAst getVariable(IdentAst function, IdentAst ident) {
    return variables.get(new NameInFunction(function, ident));
  }

  @Override
  public void visitFunction(FunctionAst ast) throws AnalysisException {
    var functionName = ast.getName();

    if (functions.put(functionName, ast) != null) {
      throw new AnalysisException("function " + functionName + " already exists");
    }

    AstVisitor.super.visitFunction(ast);
  }

  @Override
  public void visitVarDecl(VarDeclAst ast) throws AnalysisException {
    var function = ast.findAncestor(FunctionAst.class);
    var nameInFunction = new NameInFunction(function.getName(), ast.getName());

    if (variables.put(nameInFunction, ast) != null) {
      throw new AnalysisException("variable " + ast.getName() + " already exists");
    }

    AstVisitor.super.visitVarDecl(ast);
  }
}
