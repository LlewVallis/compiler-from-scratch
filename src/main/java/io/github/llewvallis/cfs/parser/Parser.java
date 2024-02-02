package io.github.llewvallis.cfs.parser;

import io.github.llewvallis.cfs.ast.*;
import io.github.llewvallis.cfs.reporting.CompileErrorsException;
import io.github.llewvallis.cfs.reporting.ErrorReporter;
import io.github.llewvallis.cfs.reporting.ParseError;
import io.github.llewvallis.cfs.token.*;
import io.github.llewvallis.cfs.util.Once;
import java.util.ArrayList;
import java.util.List;

/** A recursive-descent parser for the language. */
public class Parser {

  private final ErrorReporter reporter;

  // If we have two errors next to each other, we only want to report the first one
  private Once errorGuard = new Once();

  private boolean hasErrors = false;

  private TokenStream tokens;

  public Parser(ErrorReporter reporter, TokenStream tokens) {
    this.reporter = reporter;
    this.tokens = tokens;
  }

  /** Most of the functions that parse pieces of the grammar should conform to this interface. */
  private interface ParseFunction<T> {

    /**
     * Either produces a {@link T}, or throws. If the method throws, the input stream may still be
     * mutated. If the function recovers from a syntax error, this method will still return a
     * (possibly invalid) value.
     */
    T parse(Parser parser) throws ParseException;
  }

  public static ProgramAst parse(ErrorReporter reporter, String syntax) {
    var tokens = new TokenStream(reporter, new Lexer(syntax));
    return new Parser(reporter, tokens).parse();
  }

  public static ProgramAst parseOrThrow(String syntax) throws CompileErrorsException {
    var reporter = new ErrorReporter();
    var result = parse(reporter, syntax);
    reporter.assertNoErrors();
    return result;
  }

  public ProgramAst parse() {
    var ast = parseProgram();

    // Sets the parent fields appropriately on all the AST nodes. Doing this all at once at the end
    // is a bit easier than doing it progressively as we are parsing
    if (!hasErrors) ast.assignParents();

    return ast;
  }

  /** Attempts to parse a token or throws if the wrong token was found instead. */
  @SuppressWarnings("unchecked")
  private <T extends Token> T expect(Class<T> cls) throws ParseException {
    var next = tokens.peek();

    if (!cls.isInstance(next)) {
      throw new ParseException(cls.getSimpleName(), next);
    }

    // We don't want to report two adjacent errors, but if there is at least one valid token between
    // them, then we should report them both
    errorGuard.reset();

    return (T) tokens.next();
  }

  /**
   * This augments a parsing function such that if the function throws, this method instead returns
   * {@code null} and resets all state mutated by the function. This is useful when there is a
   * choice between different rules, we can try one rule and then another if the first fails.
   */
  private <T> T speculate(ParseFunction<T> f) {
    var oldTokens = new TokenStream(tokens);
    var oldErrorGuard = new Once(errorGuard);

    try {
      return f.parse(this);
    } catch (ParseException e) {
      tokens = oldTokens;
      errorGuard = oldErrorGuard;
      return null;
    }
  }

  /**
   * This augments a parsing function to return a fallback value (usually {@code null}) and report
   * an error instead of throwing. This method does not wind back state like the token stream, so
   * should not be used for implementing a choice between rules. Instead, this method allows rules
   * to gracefully continue in the presence of errors.
   *
   * <p>The general pattern is that once a rule knows it is the "correct" rule, then it wraps all
   * further operations with this method, since backtracking won't be required.
   */
  private <T> T recover(ParseFunction<T> f, T fallback) {
    try {
      return f.parse(this);
    } catch (ParseException e) {
      if (errorGuard.once()) {
        reporter.report(new ParseError(e.getExpected(), e.getActual()));
      }

      hasErrors = true;
      return fallback;
    }
  }

  private <T> T recover(ParseFunction<T> f) {
    return recover(f, null);
  }

  private <T extends Token> T recover(Class<T> cls) {
    return recover(parser -> parser.expect(cls));
  }

  /**
   * This method allows repeating a rule until another token is encountered, and handles some edge
   * cases like EOFs, forward progress, and separator characters.
   *
   * <p>Rules are required to consume at least one token when they complete successfully.
   */
  private <T> List<T> repeat(
      Class<? extends Token> until, Class<? extends Token> sep, ParseFunction<T> f) {
    var results = new ArrayList<T>();
    // This tracks whether we should request a separator token on the next iteration
    var needsSeparator = false;

    // We also need to bail out on EOF
    while (!until.isInstance(tokens.peek()) && !(tokens.peek() instanceof EofToken)) {
      if (needsSeparator) {
        recover(sep);
        needsSeparator = false;
      }

      var before = tokens.getNextPosition();
      var result = recover(f);
      var madeProgress = tokens.getNextPosition() != before;

      if (result != null) {
        needsSeparator = true;
        results.add(result);
      }

      // There are some edge-cases where an infinite loop would occur, e.g. if the repeated rule
      // fails without consuming input
      if (!madeProgress) {
        tokens.next();
      }
    }

    return results;
  }

  /** Same as {@link #repeat(Class, Class, ParseFunction)} but without the separator token. */
  private <T> List<T> repeat(Class<? extends Token> until, ParseFunction<T> f) {
    var results = new ArrayList<T>();

    while (!until.isInstance(tokens.peek()) && !(tokens.peek() instanceof EofToken)) {
      var before = tokens.getNextPosition();
      var result = recover(f);
      var madeProgress = tokens.getNextPosition() != before;

      if (result != null) {
        results.add(result);
      }

      if (!madeProgress) {
        tokens.next();
      }
    }

    return results;
  }

  public ProgramAst parseProgram() {
    var span = new SpanTracker(tokens);
    var functions = repeat(EofToken.class, Parser::parseFunction);
    return new ProgramAst(span.finish(), functions);
  }

  private FunctionAst parseFunction() {
    var span = new SpanTracker(tokens);

    var returnTy = recover(Parser::parseTy);
    var name = recover(Parser::parseIdent);
    var params = recover(Parser::parseParams, List.<VarDeclAst>of());
    var body = recover(Parser::parseBlock);

    return new FunctionAst(span.finish(), name, params, returnTy, body);
  }

  private List<VarDeclAst> parseParams() throws ParseException {
    expect(OpenParenToken.class);
    var params = repeat(CloseParenToken.class, CommaToken.class, Parser::parseVarDecl);
    recover(CloseParenToken.class);
    return params;
  }

  private VarDeclAst parseVarDecl() throws ParseException {
    var span = new SpanTracker(tokens);

    var ty = parseTy();
    var name = recover(Parser::parseIdent);

    return new VarDeclAst(span.finish(), name, ty);
  }

  private TyAst parseTy() throws ParseException {
    return parseIntTy();
  }

  private IntTyAst parseIntTy() throws ParseException {
    var span = new SpanTracker(tokens);
    expect(KwIntToken.class);
    return new IntTyAst(span.finish());
  }

  private BlockAst parseBlock() throws ParseException {
    var span = new SpanTracker(tokens);

    expect(OpenBraceToken.class);
    var stmts = repeat(CloseBraceToken.class, Parser::parseStmt);
    recover(CloseBraceToken.class);

    return new BlockAst(span.finish(), stmts);
  }

  private StmtAst parseStmt() throws ParseException {
    VarDeclStmtAst varDecl = speculate(Parser::parseVarDeclStmt);
    if (varDecl != null) return varDecl;

    ReturnStmtAst returnStmt = speculate(Parser::parseReturnStmt);
    if (returnStmt != null) return returnStmt;

    ExprStmtAst expr = speculate(Parser::parseExprStmt);
    if (expr != null) return expr;

    throw new ParseException("statement", tokens.peek());
  }

  private VarDeclStmtAst parseVarDeclStmt() throws ParseException {
    var span = new SpanTracker(tokens);

    var decl = parseVarDecl();
    recover(SemicolonToken.class);

    return new VarDeclStmtAst(span.finish(), decl);
  }

  private ReturnStmtAst parseReturnStmt() throws ParseException {
    var span = new SpanTracker(tokens);

    expect(KwReturnToken.class);
    var expr = recover(Parser::parseExpr);
    recover(SemicolonToken.class);

    return new ReturnStmtAst(span.finish(), expr);
  }

  private ExprStmtAst parseExprStmt() throws ParseException {
    var span = new SpanTracker(tokens);

    var expr = parseExpr();
    recover(SemicolonToken.class);

    return new ExprStmtAst(span.finish(), expr);
  }

  private ExprAst parseExpr() throws ParseException {
    AssignmentExprAst assignment = speculate(Parser::parseAssignmentExpr);
    if (assignment != null) return assignment;

    VarExprAst variable = speculate(Parser::parseVariableExpr);
    if (variable != null) return variable;

    IntLiteralExprAst intLiteral = speculate(Parser::parseIntLiteralExpr);
    if (intLiteral != null) return intLiteral;

    throw new ParseException("expression", tokens.peek());
  }

  private VarExprAst parseVariableExpr() throws ParseException {
    var span = new SpanTracker(tokens);
    var ident = parseIdent();
    return new VarExprAst(span.finish(), ident);
  }

  private AssignmentExprAst parseAssignmentExpr() throws ParseException {
    var span = new SpanTracker(tokens);

    var lhs = parseIdent();
    expect(EqualsToken.class);
    var rhs = recover(Parser::parseExpr);

    return new AssignmentExprAst(span.finish(), lhs, rhs);
  }

  private IntLiteralExprAst parseIntLiteralExpr() throws ParseException {
    var span = new SpanTracker(tokens);
    var token = expect(IntLiteralToken.class);
    return new IntLiteralExprAst(span.finish(), token.getValue());
  }

  private IdentAst parseIdent() throws ParseException {
    var span = new SpanTracker(tokens);
    var token = expect(IdentToken.class);
    return new IdentAst(span.finish(), token.getContent());
  }
}
