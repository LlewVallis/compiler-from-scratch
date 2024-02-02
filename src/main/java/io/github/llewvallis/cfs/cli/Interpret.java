package io.github.llewvallis.cfs.cli;

import io.github.llewvallis.cfs.CompilerDriver;
import io.github.llewvallis.cfs.interpret.InterpretException;
import io.github.llewvallis.cfs.reporting.CompileErrorsException;
import io.github.llewvallis.cfs.reporting.SourceMap;
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
  public Integer call() throws IOException, InterpretException {
    var input = new String(System.in.readAllBytes());
    var sourceMap = new SourceMap(input);
    var compiler = new CompilerDriver(input);

    try {
      var value = compiler.interpret("main", List.of());
      System.out.println(value);
    } catch (CompileErrorsException e) {
      System.err.println(e.prettyPrint(sourceMap));
      return 1;
    }

    return 0;
  }
}
