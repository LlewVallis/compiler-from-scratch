package io.github.llewvallis.cfs.interpret;

import io.github.llewvallis.cfs.ast.*;
import java.util.*;

public class Interpreter {

  private final ProgramAst ast;

  private final Deque<Map<IdentAst, Value>> stack = new ArrayDeque<>();

  public Interpreter(ProgramAst ast) {
    this.ast = ast;
  }

  public Value run(String name, List<Value> params) throws InterpretException {
    var function = ast.getFunction(name);
    if (function == null) {
      throw new InterpretException("no function called " + name);
    }

    return runFunction(function, params);
  }

  private Value runFunction(FunctionAst ast, List<Value> args) throws InterpretException {
    var variables = new HashMap<IdentAst, Value>();
    stack.push(variables);

    if (ast.getParams().size() != args.size()) {
      throw new InterpretException("wrong number of arguments for " + ast.getName());
    }

    for (var i = 0; i < args.size(); i++) {
      var param = ast.getParams().get(i);
      var value = args.get(i);
      variables.put(param.getName(), value);
    }

    try {
      return runBlock(ast.getBody());
    } finally {
      stack.pop();
    }
  }

  private Value runBlock(BlockAst ast) throws InterpretException {
    for (var stmt : ast.getStmts()) {
      switch (stmt) {
        case VarDeclStmtAst ignored -> {}
        case ReturnStmtAst returnStmt -> {
          return evalExpr(returnStmt.getValue());
        }
        case ExprStmtAst exprStmt -> evalExpr(exprStmt.getExpr());
      }
    }

    throw new InterpretException("function did not return");
  }

  private Value evalExpr(ExprAst ast) throws InterpretException {
    return switch (ast) {
      case VarExprAst variable -> {
        var value = stack.peek().get(variable.getName());
        if (value == null)
          throw new InterpretException("uninitialized variable " + variable.getName());
        yield value;
      }
      case IntLiteralExprAst intLiteral -> new IntValue(intLiteral.getValue());
      case AssignmentExprAst assignment -> {
        var value = evalExpr(assignment.getValue());
        stack.peek().put(assignment.getVariable(), value);
        yield value;
      }
    };
  }
}
