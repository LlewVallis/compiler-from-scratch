package io.github.llewvallis.cfs.interpret;

import io.github.llewvallis.cfs.ast.*;
import java.util.*;

public class Interpreter {

  private final ProgramAst ast;

  private final Deque<Map<IdentAst, LValue>> stack = new ArrayDeque<>();

  public Interpreter(ProgramAst ast) {
    this.ast = ast;
  }

  public RValue run(String name, List<RValue> args) throws InterpretException {
    var function = ast.getFunction(name);
    if (function == null) {
      throw new InterpretException("no function called " + name);
    }

    return runFunction(function, args);
  }

  private RValue runFunction(FunctionAst ast, List<RValue> args) throws InterpretException {
    var variables = new HashMap<IdentAst, LValue>();
    stack.push(variables);

    if (ast.getParams().size() != args.size()) {
      throw new InterpretException("wrong number of arguments for " + ast.getName());
    }

    for (var i = 0; i < args.size(); i++) {
      var param = ast.getParams().get(i);
      var value = new LValue(args.get(i));
      variables.put(param.getName(), value);
    }

    try {
      return runBody(ast.getBody());
    } finally {
      stack.pop();
    }
  }

  private RValue runBody(BlockAst ast) throws InterpretException {
    for (var stmt : ast.getStmts()) {
      switch (stmt) {
        case VarDeclStmtAst varDecl -> stack.peek().put(varDecl.getDecl().getName(), new LValue());
        case ReturnStmtAst returnStmt -> {
          return evalRValueExpr(returnStmt.getValue());
        }
        case ExprStmtAst exprStmt -> evalRValueExpr(exprStmt.getExpr());
      }
    }

    throw new InterpretException("function did not return");
  }

  private LValue evalLValueExpr(LValueExprAst ast) throws InterpretException {
    return switch (ast) {
      case VarExprAst var -> evalVarExpr(var);
    };
  }

  private LValue evalVarExpr(VarExprAst variable) throws InterpretException {
    var value = stack.peek().get(variable.getName());

    if (value == null) {
      throw new InterpretException("undefined variable " + variable.getName());
    }

    return value;
  }

  private RValue evalRValueExpr(RValueExprAst ast) throws InterpretException {
    return switch (ast) {
      case IntoRValueExprAst intoRValue -> evalIntoRValueExpr(intoRValue);
      case IntLiteralExprAst intLiteral -> evalIntLiteral(intLiteral);
      case AssignmentExprAst assignment -> evalAssignmentExpr(assignment);
      case CallExprAst call -> evalCallExpr(call);
      case AddExprAst add -> evalAddExpr(add);
      case SubExprAst sub -> evalSubExpr(sub);
      case MulExprAst mul -> evalMulExpr(mul);
      case DivExprAst div -> evalDivExpr(div);
      case NegExprAst neg -> evalNegExpr(neg);
      case LogicalAndExprAst logicalAnd -> evalLogicalAndExpr(logicalAnd);
      case LogicalOrExprAst logicalOr -> evalLogicalOrExpr(logicalOr);
      case TernaryExprAst ternary -> evalTernaryExpr(ternary);
    };
  }

  private RValue evalIntoRValueExpr(IntoRValueExprAst intoRValue) throws InterpretException {
    var lValue = evalLValueExpr(intoRValue.getLValue());
    return lValue.get();
  }

  private IntValue evalIntLiteral(IntLiteralExprAst intLiteral) {
    return new IntValue(intLiteral.getValue());
  }

  private RValue evalAssignmentExpr(AssignmentExprAst assignment) throws InterpretException {
    var lhs = evalLValueExpr(assignment.getLhs());
    var rhs = evalRValueExpr(assignment.getRhs());

    lhs.set(rhs);

    return rhs;
  }

  private RValue evalCallExpr(CallExprAst call) throws InterpretException {
    var args = new ArrayList<RValue>();
    for (var arg : call.getArgs()) args.add(evalRValueExpr(arg));
    return run(call.getFunction().getContent(), args);
  }

  private RValue evalAddExpr(AddExprAst add) throws InterpretException {
    var lhs = evalRValueExpr(add.getLhs());
    var rhs = evalRValueExpr(add.getRhs());
    var value = lhs.castToInt().getValue() + rhs.castToInt().getValue();
    return new IntValue(value);
  }

  private RValue evalSubExpr(SubExprAst sub) throws InterpretException {
    var lhs = evalRValueExpr(sub.getLhs());
    var rhs = evalRValueExpr(sub.getRhs());
    var value = lhs.castToInt().getValue() - rhs.castToInt().getValue();
    return new IntValue(value);
  }

  private RValue evalMulExpr(MulExprAst mul) throws InterpretException {
    var lhs = evalRValueExpr(mul.getLhs());
    var rhs = evalRValueExpr(mul.getRhs());
    var value = lhs.castToInt().getValue() * rhs.castToInt().getValue();
    return new IntValue(value);
  }

  private RValue evalDivExpr(DivExprAst div) throws InterpretException {
    var lhs = evalRValueExpr(div.getLhs());
    var rhs = evalRValueExpr(div.getRhs());

    try {
      var value = lhs.castToInt().getValue() + rhs.castToInt().getValue();
      return new IntValue(value);
    } catch (ArithmeticException e) {
      throw new InterpretException("division by zero");
    }
  }

  private RValue evalNegExpr(NegExprAst negation) throws InterpretException {
    var value = evalRValueExpr(negation.getExpr());
    return new IntValue(-value.castToInt().getValue());
  }

  private RValue evalLogicalAndExpr(LogicalAndExprAst logicalAnd) throws InterpretException {
    var lhs = evalRValueExpr(logicalAnd.getLhs());

    if (lhs.castToInt().getValue() == 0) {
      return lhs;
    } else {
      return evalRValueExpr(logicalAnd.getRhs());
    }
  }

  private RValue evalLogicalOrExpr(LogicalOrExprAst logicalOr) throws InterpretException {
    var lhs = evalRValueExpr(logicalOr.getLhs());

    if (lhs.castToInt().getValue() != 0) {
      return lhs;
    } else {
      return evalRValueExpr(logicalOr.getRhs());
    }
  }

  private RValue evalTernaryExpr(TernaryExprAst ternary) throws InterpretException {
    var condition = evalRValueExpr(ternary.getCondition());

    if (condition.castToInt().getValue() != 0) {
      return evalRValueExpr(ternary.getTrueCase());
    } else {
      return evalRValueExpr(ternary.getFalseCase());
    }
  }
}
