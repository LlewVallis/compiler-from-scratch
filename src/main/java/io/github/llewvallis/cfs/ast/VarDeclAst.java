package io.github.llewvallis.cfs.ast;

import io.github.llewvallis.cfs.ast.analysis.AnalysisException;
import io.github.llewvallis.cfs.ast.analysis.AstVisitor;
import io.github.llewvallis.cfs.graphviz.GraphvizBuilder;
import io.github.llewvallis.cfs.graphviz.GraphvizNode;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public final class VarDeclAst extends Ast {

  @Getter private final IdentAst name;

  @Getter private final TyAst ty;

  public VarDeclAst(IdentAst name, TyAst ty) {
    this.name = name;
    this.ty = ty;
  }

  @Override
  public void accept(AstVisitor visitor) throws AnalysisException {
    visitor.visitVarDecl(this);
  }

  @Override
  public List<? extends Ast> getChildren() {
    return List.of(name, ty);
  }

  @Override
  public GraphvizNode graphviz(GraphvizBuilder builder) {
    var node = builder.newNode("Declaration");
    node.addEdge(name.graphviz(builder), "Name");
    node.addEdge(ty.graphviz(builder), "Type");
    return node;
  }
}
