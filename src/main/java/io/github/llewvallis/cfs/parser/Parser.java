package io.github.llewvallis.cfs.parser;

import io.github.llewvallis.cfs.ast.*;
import io.github.llewvallis.cfs.token.*;
import java.util.ArrayList;

/** A recursive-descent parser for the language. */
public class Parser {

  private Lexer lexer;

  public Parser(Lexer lexer) {
    this.lexer = lexer;
  }

  /** Most of the functions that parse pieces of the grammar should conform to this interface. */
  private interface ParseFunction<T extends Ast> {

    /**
     * Either produces a valid {@link T}, or throws. If the method throws, the input stream may
     * still be mutated.
     */
    T parse(Parser parser) throws ParseException;
  }

  public static ProgramAst parse(String syntax) throws ParseException {
    return new Parser(new Lexer(syntax)).parse();
  }

  public ProgramAst parse() throws ParseException {
    var ast = parseProgram();

    // Sets the parent fields appropriately on all the AST nodes. Doing this all at once at the end
    // is a bit easier than doing it progressively as we are parsing.
    ast.assignParents();

    return ast;
  }

  /** Attempts to parse a token or throws if the wrong token was found instead. */
  @SuppressWarnings("unchecked")
  private <T extends Token> T expect(Class<T> cls) throws ParseException {
    var next = lexer.peek();
    if (!cls.isInstance(next)) {
      throw new ParseException("expected " + cls + " but found " + next);
    }

    return (T) lexer.next();
  }

  /**
   * Runs a parsing function, but returns null instead of propagating any parsing exceptions. If the
   * function throws, the input stream will not be advanced.
   */
  private <T extends Ast> T attempt(ParseFunction<T> f) {
    var copy = new Parser(new Lexer(lexer));

    try {
      var result = f.parse(copy);
      lexer = copy.lexer;
      return result;
    } catch (ParseException e) {
      return null;
    }
  }

  public ProgramAst parseProgram() throws ParseException {
    var functions = new ArrayList<FunctionAst>();

    while (!(lexer.peek() instanceof EofToken)) {
      functions.add(parseFunction());
    }

    return new ProgramAst(functions);
  }

  private FunctionAst parseFunction() throws ParseException {
    var returnTy = parseTy();
    var name = parseIdent();
    var params = new ArrayList<VarDeclAst>();

    expect(OpenParenToken.class);

    while (!(lexer.peek() instanceof CloseParenToken)) {
      if (!params.isEmpty()) {
        expect(CommaToken.class);
      }

      params.add(parseVarDecl());
    }

    expect(CloseParenToken.class);

    var body = parseBlock();

    return new FunctionAst(name, params, returnTy, body);
  }

  private VarDeclAst parseVarDecl() throws ParseException {
    var ty = parseTy();
    var name = parseIdent();

    return new VarDeclAst(name, ty);
  }

  private TyAst parseTy() throws ParseException {
    return parseKwTy();
  }

  private TyAst parseKwTy() throws ParseException {
    var result =
        switch (lexer.peek()) {
          case KwIntToken ignored -> new IntTyAst();
          default -> throw new ParseException("not a keyword type");
        };

    lexer.next();

    return result;
  }

  private BlockAst parseBlock() throws ParseException {
    var statements = new ArrayList<StmtAst>();

    expect(OpenBraceToken.class);

    while (!(lexer.peek() instanceof CloseBraceToken)) {
      statements.add(parseStmt());
    }

    expect(CloseBraceToken.class);

    return new BlockAst(statements);
  }

  private StmtAst parseStmt() throws ParseException {
    VarDeclStmtAst varDecl = attempt(Parser::parseVarDeclStmt);
    if (varDecl != null) return varDecl;

    ReturnStmtAst returnStmt = attempt(Parser::parseReturnStmt);
    if (returnStmt != null) return returnStmt;

    ExprStmtAst expr = attempt(Parser::parseExprStmt);
    if (expr != null) return expr;

    throw new ParseException("no valid statement");
  }

  private VarDeclStmtAst parseVarDeclStmt() throws ParseException {
    var decl = parseVarDecl();
    expect(SemicolonToken.class);

    return new VarDeclStmtAst(decl);
  }

  private ReturnStmtAst parseReturnStmt() throws ParseException {
    expect(KwReturnToken.class);
    var expr = parseExpr();
    expect(SemicolonToken.class);

    return new ReturnStmtAst(expr);
  }

  private ExprStmtAst parseExprStmt() throws ParseException {
    var expr = parseExpr();
    expect(SemicolonToken.class);

    return new ExprStmtAst(expr);
  }

  private ExprAst parseExpr() throws ParseException {
    AssignmentExprAst assignment = attempt(Parser::parseAssignmentExpr);
    if (assignment != null) return assignment;

    VarExprAst variable = attempt(Parser::parseVariableExpr);
    if (variable != null) return variable;

    IntLiteralExprAst intLiteral = attempt(Parser::parseIntLiteralExpr);
    if (intLiteral != null) return intLiteral;

    throw new ParseException("no valid expression");
  }

  private VarExprAst parseVariableExpr() throws ParseException {
    return new VarExprAst(parseIdent());
  }

  private AssignmentExprAst parseAssignmentExpr() throws ParseException {
    var lhs = parseIdent();
    expect(EqualsToken.class);
    var rhs = parseExpr();

    return new AssignmentExprAst(lhs, rhs);
  }

  private IntLiteralExprAst parseIntLiteralExpr() throws ParseException {
    var token = expect(IntLiteralToken.class);
    return new IntLiteralExprAst(token.getValue());
  }

  private IdentAst parseIdent() throws ParseException {
    var token = expect(IdentToken.class);
    return new IdentAst(token.getContent());
  }
}
