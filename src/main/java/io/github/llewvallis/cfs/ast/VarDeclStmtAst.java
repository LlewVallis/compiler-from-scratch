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
public final class VarDeclStmtAst extends StmtAst {

  @Getter private final VarDeclAst decl;

  public VarDeclStmtAst(VarDeclAst decl) {
    this.decl = decl;
  }

  @Override
  public void accept(AstVisitor visitor) throws AnalysisException {
    visitor.visitVarDeclStmt(this);
  }

  @Override
  public List<? extends Ast> getChildren() {
    return List.of(decl);
  }

  @Override
  public GraphvizNode graphviz(GraphvizBuilder builder) {
    return decl.graphviz(builder);
  }
}
