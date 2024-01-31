package io.github.llewvallis.cfs.cli;

import io.github.llewvallis.cfs.graphviz.GraphvizBuilder;
import io.github.llewvallis.cfs.parser.ParseException;
import io.github.llewvallis.cfs.parser.Parser;
import java.io.IOException;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "dump-ast", description = "output a parsed AST", mixinStandardHelpOptions = true)
public class DumpAst implements Callable<Integer> {

  @Option(
      names = {"-g", "--graphviz"},
      description = "output a representation using Graphviz")
  private boolean graphviz;

  @Override
  public Integer call() throws IOException, ParseException {
    var input = new String(System.in.readAllBytes());
    var ast = Parser.parse(input);

    if (graphviz) {
      var builder = new GraphvizBuilder();
      ast.graphviz(builder);
      System.out.println(builder.build());
    } else {
      System.out.println(ast);
    }

    return 0;
  }
}
