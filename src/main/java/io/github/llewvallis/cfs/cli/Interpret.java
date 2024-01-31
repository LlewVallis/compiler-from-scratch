package io.github.llewvallis.cfs.cli;

import io.github.llewvallis.cfs.ast.analysis.AnalysisException;
import io.github.llewvallis.cfs.ast.analysis.Analyzer;
import io.github.llewvallis.cfs.interpret.InterpretException;
import io.github.llewvallis.cfs.interpret.Interpreter;
import io.github.llewvallis.cfs.parser.ParseException;
import io.github.llewvallis.cfs.parser.Parser;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;

@Command(
    name = "interpret",
    description = "execute the main method of a program",
    mixinStandardHelpOptions = true)
public class Interpret implements Callable<Integer> {

  @Override
  public Integer call() throws IOException, ParseException, AnalysisException, InterpretException {
    var input = new String(System.in.readAllBytes());
    var ast = Parser.parse(input);
    new Analyzer().analyze(ast);
    var interpreter = new Interpreter(ast);

    System.out.println(interpreter.run("main", List.of()));

    return 0;
  }
}
