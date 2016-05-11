package common.events;

import common.draw.NodeState;

import java.util.List;
import java.util.Map;

public final class ShowState {

    public final Map<Integer, List<Double>> edges;
    public final Map<Integer, ? extends NodeState> nodes;

    public ShowState(Map<Integer, List<Double>> edges, Map<Integer, ? extends NodeState> nodes) {
        this.edges = edges;
        this.nodes = nodes;
    }
}
