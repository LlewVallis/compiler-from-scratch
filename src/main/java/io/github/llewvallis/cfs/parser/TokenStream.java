package io.github.llewvallis.cfs.parser;

import io.github.llewvallis.cfs.reporting.ErrorReporter;
import io.github.llewvallis.cfs.reporting.TokenError;
import io.github.llewvallis.cfs.token.EofToken;
import io.github.llewvallis.cfs.token.Token;
import io.github.llewvallis.cfs.util.Once;
import java.util.ArrayList;
import java.util.List;

public class TokenStream {

  private final List<Token> tokens;
  private int index = 0;

  public TokenStream(ErrorReporter reporter, Lexer lexer) {
    tokens = new ArrayList<>();

    var errorGuard = new Once();

    while (true) {
      try {
        var token = lexer.next();
        tokens.add(token);
        errorGuard.reset();
        if (token instanceof EofToken) break;
      } catch (LexException e) {
        if (errorGuard.once()) {
          var error = new TokenError(e.getPosition());
          reporter.report(error);
        }
      }
    }
  }

  public TokenStream(TokenStream other) {
    this.tokens = other.tokens;
    this.index = other.index;
  }

  public Token next() {
    var result = peek();
    if (index + 1 < tokens.size()) index++;
    return result;
  }

  public Token peek() {
    return tokens.get(index);
  }

  public int getNextPosition() {
    return peek().getSpan().start();
  }

  public int getLastPosition() {
    if (index != 0) {
      return tokens.get(index - 1).getSpan().end();
    } else {
      return 0;
    }
  }
}
