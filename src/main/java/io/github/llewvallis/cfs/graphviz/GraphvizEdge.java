package io.github.llewvallis.cfs.graphviz;

import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GraphvizEdge {

  @Getter private final GraphvizNode from;
  @Getter private final GraphvizNode to;

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

  public Map<String, String> getAttributes() {
    return Collections.unmodifiableMap(attributes);
  }
}
