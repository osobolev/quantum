package common.graph.model;

import java.awt.*;

public final class Node {

    public final Point p;
    public Integer weight;

    public Node(Point p, Integer weight) {
        this.p = p;
        this.weight = weight;
    }
}
