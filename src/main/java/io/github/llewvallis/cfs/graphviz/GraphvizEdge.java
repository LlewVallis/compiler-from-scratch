package io.github.llewvallis.cfs.graphviz;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

public class GraphvizEdge {

  @Getter private final GraphvizNode from;
  @Getter private final GraphvizNode to;

  @Getter
  private final Map<String, String> attributes = new HashMap<>();

  GraphvizEdge(GraphvizNode from, GraphvizNode to) {
    this.from = from;
    this.to = to;
  }

  public GraphvizEdge label(String label) {
    return attribute("label", label);
  }

  public GraphvizEdge attribute(String attr, String value) {
    attributes.put(attr, value);
    return this;
  }
}
