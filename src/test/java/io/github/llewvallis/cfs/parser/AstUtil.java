package io.github.llewvallis.cfs.parser;

import io.github.llewvallis.cfs.ast.*;
import java.util.Arrays;
import java.util.List;

public class AstUtil {

  public static ProgramAst exprProgram(ExprAst expr) {
    return program(function("main", params(), intTy(), block(exprStmt(expr))));
  }

  public static ProgramAst program(FunctionAst... functions) {
    return new ProgramAst(null, Arrays.asList(functions));
  }

  public static FunctionAst function(
      String name, List<VarDeclAst> params, TyAst returnTy, BlockAst body) {
    return new FunctionAst(null, new IdentAst(null, name), params, returnTy, body);
  }

  public static List<VarDeclAst> params(VarDeclAst... params) {
    return Arrays.asList(params);
  }

  public static List<VarDeclAst> params(String name, TyAst ty) {
    return params(param(name, ty));
  }

  public static VarDeclAst param(String name, TyAst ty) {
    return new VarDeclAst(null, new IdentAst(null, name), ty);
  }

  public static IntTyAst intTy() {
    return new IntTyAst(null);
  }

  public static BlockAst block(StmtAst... stmts) {
    return new BlockAst(null, Arrays.asList(stmts));
  }

  public static ExprStmtAst exprStmt(ExprAst expr) {
    return new ExprStmtAst(null, expr);
  }

  public static ReturnStmtAst returnStmt(ExprAst expr) {
    return new ReturnStmtAst(null, expr);
  }

  public static VarDeclStmtAst varDeclStmt(String name, TyAst ty) {
    return new VarDeclStmtAst(null, new VarDeclAst(null, new IdentAst(null, name), ty));
  }

  public static VarExprAst varExpr(String name) {
    return new VarExprAst(null, new IdentAst(null, name));
  }

  public static IntLiteralExprAst literal(int value) {
    return new IntLiteralExprAst(null, value);
  }

  public static AssignmentExprAst assignment(LValueExprAst lhs, ExprAst rhs) {
    return new AssignmentExprAst(null, lhs, rhs);
  }

  public static AssignmentExprAst assignment(String lhs, ExprAst rhs) {
    return assignment(varExpr(lhs), rhs);
  }

  public static AddExprAst add(ExprAst lhs, ExprAst rhs) {
    return new AddExprAst(null, lhs, rhs);
  }

  public static SubExprAst sub(ExprAst lhs, ExprAst rhs) {
    return new SubExprAst(null, lhs, rhs);
  }

  public static MulExprAst mul(ExprAst lhs, ExprAst rhs) {
    return new MulExprAst(null, lhs, rhs);
  }

  public static DivExprAst div(ExprAst lhs, ExprAst rhs) {
    return new DivExprAst(null, lhs, rhs);
  }

  public static NegExprAst neg(ExprAst expr) {
    return new NegExprAst(null, expr);
  }

  public static TernaryExprAst ternary(ExprAst condition, ExprAst ifTrue, ExprAst ifFalse) {
    return new TernaryExprAst(null, condition, ifTrue, ifFalse);
  }

  public static CallExprAst call(String name, ExprAst... exprs) {
    return new CallExprAst(null, new IdentAst(null, name), Arrays.asList(exprs));
  }
}
