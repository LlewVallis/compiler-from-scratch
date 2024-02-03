package io.github.llewvallis.cfs.parser;

import io.github.llewvallis.cfs.ast.*;
import io.github.llewvallis.cfs.reporting.CompileErrorsException;
import io.github.llewvallis.cfs.reporting.ErrorReporter;
import io.github.llewvallis.cfs.reporting.NotAnLValueError;
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

  /**
   * We will use this for the Pratt parser. The binding power of an operator means how tightly it
   * binds in an expression. So addition has a lower binding power than multiplication. Each
   * operator has a left or right power. A smaller left power means left associative and a smaller
   * right power means right associative.
   */
  private record BindingPower(int power, boolean rightAssociative) {

    public static BindingPower prefix(Token token) {
      return switch (token) {
        case MinusToken ignored -> new BindingPower(7, true);
        default -> null;
      };
    }

    public static BindingPower continuing(Token token) {
      return switch (token) {
        case EqualsToken ignored -> new BindingPower(1, true);
        case QuestionToken ignored -> new BindingPower(2, true);
        case AndAndToken ignored -> new BindingPower(3, false);
        case OrOrToken ignored -> new BindingPower(4, false);
        case PlusToken ignored -> new BindingPower(5, false);
        case MinusToken ignored -> new BindingPower(5, false);
        case StarToken ignored -> new BindingPower(6, false);
        case SlashToken ignored -> new BindingPower(6, false);
        default -> null;
      };
    }

    public int left() {
      return power * 2 + (rightAssociative ? 1 : 0);
    }

    public int right() {
      return power * 2 + (rightAssociative ? 0 : 1);
    }
  }

  private record SpeculateResult<T>(T value, boolean success) {}

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
  private <T> SpeculateResult<T> speculate(ParseFunction<T> f) {
    var oldTokens = new TokenStream(tokens);
    var oldErrorGuard = new Once(errorGuard);

    try {
      var result = f.parse(this);
      return new SpeculateResult<>(result, true);
    } catch (ParseException e) {
      tokens = oldTokens;
      errorGuard = oldErrorGuard;
      return new SpeculateResult<>(null, false);
    }
  }

  @SafeVarargs
  private <T> T alternative(String expected, ParseFunction<T>... alts) throws ParseException {
    for (var alt : alts) {
      var result = speculate(alt);
      if (result.success) return result.value;
    }

    throw new ParseException(expected, tokens.peek());
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

  private LValueExprAst requireLValue(ExprAst expr) {
    if (expr instanceof LValueExprAst lValue) {
      return lValue;
    } else {
      reporter.report(new NotAnLValueError(expr.getSpan()));
      hasErrors = true;
      return null;
    }
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
    return alternative(
        "statement", Parser::parseVarDeclStmt, Parser::parseReturnStmt, Parser::parseExprStmt);
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
    return parseExpr(0);
  }

  /** Pratt parser for expressions. */
  private ExprAst parseExpr(int minBindingPower) throws ParseException {
    var span = new SpanTracker(tokens);
    var expr = parsePrefixedExpr();

    while (true) {
      var power = BindingPower.continuing(tokens.peek());

      if (power == null || power.left() < minBindingPower) {
        return expr;
      }

      expr = continueExpr(span, expr, power.right());
    }
  }

  private ExprAst parsePrefixedExpr() throws ParseException {
    var span = new SpanTracker(tokens);

    var power = BindingPower.prefix(tokens.peek());
    if (power == null) return parseAtomicExpr();

    var op = tokens.next();
    var rhs = recover(parser -> parser.parseExpr(power.right()));

    return switch (op) {
      case MinusToken ignored -> new NegExprAst(span.finish(), rhs);
      default -> throw new AssertionError();
    };
  }

  private ExprAst continueExpr(SpanTracker span, ExprAst lhs, int rhsPower) {
    var op = tokens.next();

    if (op instanceof QuestionToken) {
      var ifTrue = recover(parser -> parseExpr(0));
      recover(ColonToken.class);
      var ifFalse = recover(parser -> parseExpr(rhsPower));
      return new TernaryExprAst(span.finish(), lhs, ifTrue, ifFalse);
    }

    var rhs = recover(parser -> parser.parseExpr(rhsPower));

    return switch (op) {
      case PlusToken ignored -> new AddExprAst(span.finish(), lhs, rhs);
      case MinusToken ignored -> new SubExprAst(span.finish(), lhs, rhs);
      case StarToken ignored -> new MulExprAst(span.finish(), lhs, rhs);
      case SlashToken ignored -> new DivExprAst(span.finish(), lhs, rhs);
      case AndAndToken ignored -> new LogicalAndExprAst(span.finish(), lhs, rhs);
      case OrOrToken ignored -> new LogicalOrExprAst(span.finish(), lhs, rhs);
      case EqualsToken ignored -> new AssignmentExprAst(span.finish(), requireLValue(lhs), rhs);
      default -> throw new AssertionError();
    };
  }

  private ExprAst parseAtomicExpr() throws ParseException {
    return alternative(
        "expression",
        Parser::parseCallExpr,
        Parser::parseVariableExpr,
        Parser::parseIntLiteralExpr,
        Parser::parseParenExpr);
  }

  private ExprAst parseParenExpr() throws ParseException {
    expect(OpenParenToken.class);
    var expr = recover(Parser::parseExpr);
    recover(CloseParenToken.class);
    return expr;
  }

  private CallExprAst parseCallExpr() throws ParseException {
    var span = new SpanTracker(tokens);
    var ident = parseIdent();
    expect(OpenParenToken.class);
    var args = repeat(CloseParenToken.class, CommaToken.class, Parser::parseExpr);
    recover(CloseParenToken.class);

    return new CallExprAst(span.finish(), ident, args);
  }

  private VarExprAst parseVariableExpr() throws ParseException {
    var span = new SpanTracker(tokens);
    var ident = parseIdent();
    return new VarExprAst(span.finish(), ident);
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
