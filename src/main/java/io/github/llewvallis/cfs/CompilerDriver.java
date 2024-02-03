package io.github.llewvallis.cfs;

import io.github.llewvallis.cfs.ast.ProgramAst;
import io.github.llewvallis.cfs.ast.analysis.Analyzer;
import io.github.llewvallis.cfs.interpret.InterpretException;
import io.github.llewvallis.cfs.interpret.Interpreter;
import io.github.llewvallis.cfs.interpret.RValue;
import io.github.llewvallis.cfs.parser.Parser;
import io.github.llewvallis.cfs.reporting.CompileErrorsException;
import io.github.llewvallis.cfs.reporting.ErrorReporter;
import java.util.List;

/**
 * A utility for running different stages of the compile in sequence. For example calling {@link
 * #analyze()} will lex, parse, and validate the program.
 *
 * <p>Only one method should be called on the driver to run all the required stages.
 */
public class CompilerDriver {

  private final ErrorReporter reporter = new ErrorReporter();
  private final String source;

  private ProgramAst ast;

  public CompilerDriver(String source) {
    this.source = source;
  }

  public ProgramAst parse() throws CompileErrorsException {
    ast = Parser.parse(reporter, source);
    reporter.assertNoErrors();
    return ast;
  }

  public ProgramAst analyze() throws CompileErrorsException {
    parse();
    new Analyzer(reporter).analyze(ast);
    reporter.assertNoErrors();
    return ast;
  }

  public RValue interpret(String function, List<RValue> params)
      throws CompileErrorsException, InterpretException {
    analyze();
    return new Interpreter(ast).run(function, params);
  }
}
