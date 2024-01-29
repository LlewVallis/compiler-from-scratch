package io.github.llewvallis.cfs.parser;

import io.github.llewvallis.cfs.ast.*;
import io.github.llewvallis.cfs.token.*;
import java.util.ArrayList;

public class Parser {

  private final Lexer lexer;

  public Parser(Lexer lexer) {
    this.lexer = lexer;
  }

  public ProgramAst parseProgram() throws ParseException {
    var functions = new ArrayList<FunctionAst>();

    while (!(lexer.peek() instanceof EofToken)) {
      functions.add(parseFunction());
    }

    return new ProgramAst(functions);
  }

  @SuppressWarnings("unchecked")
  private <T extends Token> T expect(Class<T> cls) throws ParseException {
    var next = lexer.next();

    if (!cls.isInstance(next)) {
      throw new ParseException("expected " + cls + " but found " + next);
    }

    return (T) next;
  }

  private FunctionAst parseFunction() throws ParseException {
    var returnTy = parseTy();
    var name = parseIdent();
    var params = new ArrayList<ParamAst>();

    expect(OpenParenToken.class);

    while (!(lexer.peek() instanceof CloseParenToken)) {
      if (!params.isEmpty()) {
        expect(CommaToken.class);
      }

      params.add(parseParam());
    }

    expect(CloseParenToken.class);

    var body = parseBlock();

    return new FunctionAst(name, params, returnTy, body);
  }

  private ParamAst parseParam() throws ParseException {
    var ty = parseTy();
    var name = parseIdent();

    return new ParamAst(name, ty);
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
    return parseReturnStmt();
  }

  private ReturnStmtAst parseReturnStmt() throws ParseException {
    expect(KwReturnToken.class);
    var expr = parseExpr();
    expect(SemicolonToken.class);

    return new ReturnStmtAst(expr);
  }

  private ExprAst parseExpr() throws ParseException {
    return parseIntLiteralExpr();
  }

  private IntLiteralExpr parseIntLiteralExpr() throws ParseException {
    var token = expect(IntLiteralToken.class);
    return new IntLiteralExpr(token.getValue());
  }

  private IdentAst parseIdent() throws ParseException {
    var token = expect(IdentToken.class);
    return new IdentAst(token.getContent());
  }
}
