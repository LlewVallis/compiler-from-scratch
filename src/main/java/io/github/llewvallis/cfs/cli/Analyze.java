package io.github.llewvallis.cfs.cli;

import io.github.llewvallis.cfs.CompilerDriver;
import io.github.llewvallis.cfs.reporting.CompileErrorsException;
import io.github.llewvallis.cfs.reporting.SourceMap;
import java.io.IOException;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;

@Command(
    name = "analyze",
    description = "run analysis passes on an AST",
    mixinStandardHelpOptions = true)
public class Analyze implements Callable<Integer> {

  @Override
  public Integer call() throws IOException {
    var input = new String(System.in.readAllBytes());
    var sourceMap = new SourceMap(input);
    var compiler = new CompilerDriver(input);

    try {
      compiler.analyze();
    } catch (CompileErrorsException e) {
      System.err.println(e.prettyPrint(sourceMap));
      return 1;
    }

    return 0;
  }
}
