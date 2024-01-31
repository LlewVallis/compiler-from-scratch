package io.github.llewvallis.cfs.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;

/** Command line interface for the compiler */
@Command(
    name = "compiler",
    description = "tools for working with the compiler",
    mixinStandardHelpOptions = true,
    subcommands = {Interpret.class, DumpAst.class, Analyze.class})
public class Main {

  public static void main(String[] args) {
    var exitCode = new CommandLine(new Main()).execute(args);
    System.exit(exitCode);
  }
}
