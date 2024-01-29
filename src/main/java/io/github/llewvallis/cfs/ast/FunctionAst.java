package io.github.llewvallis.cfs.ast;

import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public final class FunctionAst extends AstNode {

  private final IdentAst name;
  private final List<ParamAst> parameters;
  private final TyAst returnTy;
  private final BlockAst body;

  public FunctionAst(IdentAst name, List<ParamAst> parameters, TyAst returnTy, BlockAst body) {
    this.name = name;
    this.parameters = new ArrayList<>(parameters);
    this.returnTy = returnTy;
    this.body = body;
  }

  @Override
  public List<AstNode> getChildren() {
    var results = new ArrayList<AstNode>();
    results.add(name);
    results.addAll(parameters);
    results.add(returnTy);
    results.add(body);
    return results;
  }
}
