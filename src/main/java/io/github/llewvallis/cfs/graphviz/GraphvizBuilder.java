package io.github.llewvallis.cfs.graphviz;

import java.util.*;

/**
 * A utility for building diagrams in DOT (Graphviz) syntax, for example creating an AST diagram.
 */
public class GraphvizBuilder {

  /** All nodes created in this builder. */
  private final List<GraphvizNode> nodes = new ArrayList<>();

  /** The next ID to be allocated to a node. */
  private int nextId = 0;

  public GraphvizNode newNode(String label) {
    var node = new GraphvizNode("node_" + nextId++).label(label);
    nodes.add(node);
    return node;
  }

  public String build() {
    var builder = new StringBuilder();
    builder.append("digraph {\n");

    for (var node : nodes) {
      buildNode(builder, node);
    }

    builder.append("}");
    return builder.toString();
  }

  private void buildNode(StringBuilder builder, GraphvizNode node) {
    builder.append(node.getId());
    buildAttributes(builder, node.getAttributes());
    builder.append(";\n");

    for (var edge : node.getEdges()) {
      buildEdge(builder, edge);
    }
  }

  private void buildEdge(StringBuilder builder, GraphvizEdge edge) {
    builder.append(edge.getFrom().getId());
    builder.append(" -> ");
    builder.append(edge.getTo().getId());
    buildAttributes(builder, edge.getAttributes());
    builder.append(";\n");
  }

  private void buildAttributes(StringBuilder builder, Map<String, String> attributes) {
    builder.append("[");

    for (var entry : attributes.entrySet()) {
      builder.append("\"");
      builder.append(entry.getKey().replace("\"", "\\\""));
      builder.append("\"=\"");
      builder.append(entry.getValue().replace("\"", "\\\""));
      builder.append("\";");
    }

    builder.append("]");
  }
}
