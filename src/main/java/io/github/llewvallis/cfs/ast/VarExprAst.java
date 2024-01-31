package io.github.llewvallis.cfs.ast;

import io.github.llewvallis.cfs.ast.analysis.AnalysisException;
import io.github.llewvallis.cfs.ast.analysis.AstVisitor;
import io.github.llewvallis.cfs.graphviz.GraphvizBuilder;
import io.github.llewvallis.cfs.graphviz.GraphvizNode;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public final class VarExprAst extends ExprAst {

  @Getter private final IdentAst name;

  @Getter @Setter private VarDeclAst decl = null;

  public VarExprAst(IdentAst name) {
    this.name = name;
  }

  @Override
  public void accept(AstVisitor visitor) throws AnalysisException {
    visitor.visitVariableExpr(this);
  }

  @Override
  public List<? extends Ast> getChildren() {
    return List.of(name);
  }

  @Override
  public GraphvizNode graphviz(GraphvizBuilder builder) {
    return name.graphviz(builder);
  }
}
