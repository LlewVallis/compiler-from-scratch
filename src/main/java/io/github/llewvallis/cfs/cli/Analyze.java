package io.github.llewvallis.cfs.cli;

import io.github.llewvallis.cfs.ast.analysis.AnalysisException;
import io.github.llewvallis.cfs.ast.analysis.Analyzer;
import io.github.llewvallis.cfs.parser.ParseException;
import io.github.llewvallis.cfs.parser.Parser;
import java.io.IOException;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;

@Command(
    name = "analyze",
    description = "run analysis passes on an AST",
    mixinStandardHelpOptions = true)
public class Analyze implements Callable<Integer> {

  @Override
  public Integer call() throws IOException, ParseException, AnalysisException {
    var input = new String(System.in.readAllBytes());
    var ast = Parser.parse(input);
    new Analyzer().analyze(ast);

    return 0;
  }
}
