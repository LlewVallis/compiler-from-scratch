package io.github.llewvallis.cfs.ast;

import io.github.llewvallis.cfs.ast.analysis.Analyzer;
import io.github.llewvallis.cfs.ast.analysis.AstVisitor;
import io.github.llewvallis.cfs.graphviz.GraphvizBuilder;
import io.github.llewvallis.cfs.graphviz.GraphvizNode;
import io.github.llewvallis.cfs.reporting.Span;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * The superclass of all AST nodes. An AST node has any number of children and an optional parent.
 * ASTs will usually need to be validated and lightly transformed by an {@link Analyzer} before use.
 *
 * <p>In general, ASTs are assumed to be well-formed, but if parsing is not successful, then
 * arbitrary fields on the AST may be null.
 *
 * <p>Equality amongst AST nodes is mostly a convenience for tests, and is intended to be structural
 * equality. This equality ignores spans and parents.
 */
public abstract sealed class Ast
    permits BlockAst, ExprAst, FunctionAst, IdentAst, ProgramAst, StmtAst, TyAst, VarDeclAst {

  @Getter private final Span span;

  /**
   * The parent of the AST node. This should be whatever other node this node is a child of. By
   * default, this is `null`, but calling {@link #assignParents()} will fix that for all nodes in
   * the tree.
   */
  @Getter @Setter private Ast parent = null;

  public Ast(Span span) {
    this.span = span;
  }

  /**
   * Walks the AST and assigns {@link #parent} to its appropriate value for all descendants. The
   * parent of this node will remain null.
   *
   * <p>Ideally, we would set the parent in the constructor, but this would present a chicken and
   * egg problem since a node's children also tend to be a constructor parameter.
   */
  public void assignParents() {
    for (var child : getChildren()) {
      child.setParent(this);
      child.assignParents();
    }
  }

  /**
   * Find's the first descendant in pre-order/depth-first order that is an instance of the given
   * class.
   */
  @SuppressWarnings("unchecked")
  public <T extends Ast> T findDescendant(Class<T> cls) {
    for (var child : getChildren()) {
      if (cls.isInstance(child)) {
        return (T) child;
      } else {
        var result = child.findDescendant(cls);
        if (result != null) return result;
      }
    }

    return null;
  }

  /** Finds the first ancestor that is an instance of the given class. */
  @SuppressWarnings("unchecked")
  public <T extends Ast> T findAncestor(Class<T> cls) {
    if (parent == null) return null;
    if (cls.isInstance(parent)) return (T) parent;
    return parent.findAncestor(cls);
  }

  /**
   * Calls the appropriate visitor method on the visitor. For example, {@link
   * IdentAst#accept(AstVisitor)} should just call {@link AstVisitor#visitIdent(IdentAst)}.
   */
  public abstract void accept(AstVisitor visitor);

  /**
   * Lists all other nodes that are children of this one. The order of children does not need to be
   * exactly the same as appears in the source code.
   */
  public abstract List<? extends Ast> getChildren();

  /** Recursively builds a Graphviz representation of the node. */
  public abstract GraphvizNode graphviz(GraphvizBuilder builder);

  @Override
  public abstract boolean equals(Object obj);

  @Override
  public abstract int hashCode();

  @Override
  public abstract String toString();
}
