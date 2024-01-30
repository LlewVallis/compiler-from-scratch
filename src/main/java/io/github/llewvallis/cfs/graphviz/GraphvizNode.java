package io.github.llewvallis.cfs.graphviz;

import java.util.*;
import lombok.Getter;

public class GraphvizNode {

  @Getter private final String id;

  @Getter private final List<GraphvizEdge> edges = new ArrayList<>();

  @Getter private final Map<String, String> attributes = new HashMap<>();

  GraphvizNode(String id) {
    this.id = id;
  }

  public GraphvizNode label(String label) {
    return attribute("label", label);
  }

  public GraphvizEdge addEdge(GraphvizNode other, String label) {
    return addEdge(other).label(label);
  }

  public GraphvizEdge addEdge(GraphvizNode other) {
    var edge = new GraphvizEdge(this, other);
    edges.add(edge);
    return edge;
  }

  public GraphvizNode attribute(String attr, String value) {
    attributes.put(attr, value);
    return this;
  }
}
