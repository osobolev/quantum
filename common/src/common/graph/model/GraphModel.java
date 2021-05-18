package common.graph.model;

import common.graph.Graph;
import common.graph.SimpleEdge;
import common.math.Arithmetic;
import common.math.JSRunner;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.*;

public final class GraphModel {

    private static final String NO_WEIGHT = "-";

    private final List<Node> nodes = new ArrayList<>();
    private final List<Edge> edges = new ArrayList<>();

    private Double coeff = null;

    public Node findNode(Point p0, int delta) {
        for (Node node : nodes) {
            Point p = node.p;
            int dx = Math.abs(p0.x - p.x);
            int dy = Math.abs(p0.y - p.y);
            if (dx <= delta && dy <= delta)
                return node;
        }
        return null;
    }

    private static boolean isInside(Point p1, Point p2, Point p) {
        float dx1 = p.x - p1.x;
        float dy1 = p.y - p1.y;
        float dx2 = p2.x - p1.x;
        float dy2 = p2.y - p1.y;
        float sign = dx1 * dx2 + dy1 * dy2;
        return sign > 0;
    }

    private static boolean near(Point p1, Point p2, Point p, int delta) {
        if (!isInside(p1, p2, p))
            return false;
        if (!isInside(p2, p1, p))
            return false;
        return Line2D.ptLineDist(p1.x, p1.y, p2.x, p2.y, p.x, p.y) <= delta;
    }

    private static boolean nearCircle(Point p1, Point p2, Point p, int delta) {
        Point2D pc = new Point2D.Double((p1.x + p2.x) / 2.0, (p1.y + p2.y) / 2.0);
        double d = pc.distance(p);
        double r = pc.distance(p1);
        return Math.abs(d - r) <= delta;
    }

    public Edge findEdge(Point p0, int delta) {
        for (Edge edge : edges) {
            if (edge.isLoop()) {
                if (nearCircle(edge.p1.p, edge.arc, p0, delta)) {
                    return edge;
                }
            }
            if (edge.arc == null) {
                if (near(edge.p1.p, edge.p2.p, p0, delta)) {
                    return edge;
                }
            } else {
                if (near(edge.p1.p, edge.arc, p0, delta) || near(edge.arc, edge.p2.p, p0, delta)) {
                    return edge;
                }
            }
        }
        return null;
    }

    public void setEdgeWeight(Edge edge, String weight) {
        edge.weight = weight;
        coeff = null;
    }

    public void setNodeWeight(Node node, Integer weight) {
        node.weight = weight;
    }

    public void add(Node node) {
        nodes.add(node);
        coeff = null;
    }

    public void setStartEdge(Edge edge, Boolean direction) {
        edge.startForward = direction;
    }

    public void connect(Node p1, Node p2) {
        if (p1 == p2)
            return;
        for (Edge e : edges) {
            if (p1 == e.p1 && p2 == e.p2) {
                if (e.arc == null)
                    return;
            }
            if (p1 == e.p2 && p2 == e.p1) {
                if (e.arc == null)
                    return;
            }
        }
        edges.add(new Edge(p1, p2));
        coeff = null;
    }

    public void connectLoop(Node p, Point arc) {
        Edge e = new Edge(p, p);
        e.arc = arc;
        edges.add(e);
        coeff = null;
    }

    public boolean removeNode(Node node, Edge selectedEdge) {
        nodes.remove(node);
        boolean removed = false;
        for (Iterator<Edge> i = edges.iterator(); i.hasNext(); ) {
            Edge e = i.next();
            if (node == e.p1 || node == e.p2) {
                if (e == selectedEdge) {
                    removed = true;
                }
                i.remove();
            }
        }
        coeff = null;
        return removed;
    }

    public void removeEdge(Edge edge) {
        edges.remove(edge);
        coeff = null;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public Boolean getStartDirection(Edge edge) {
        Edge e0 = null;
        boolean hasAnyStart = false;
        for (int i = 0; i < edges.size(); i++) {
            Edge e = edges.get(i);
            if (i == 0) {
                e0 = e;
            }
            if (e.startForward != null) {
                hasAnyStart = true;
                break;
            }
        }
        if (hasAnyStart) {
            return edge.startForward;
        } else {
            if (edge == e0) {
                return true;
            } else {
                return null;
            }
        }
    }

    public void save(File file) throws FileNotFoundException {
        PrintWriter w = new PrintWriter(file);
        w.println(nodes.size());
        for (Node node : nodes) {
            Point p = node.p;
            String weightStr = node.weight == null ? NO_WEIGHT : node.weight.toString();
            w.println(p.x + " " + p.y + " " + weightStr);
        }
        w.println(edges.size());
        for (Edge e : edges) {
            int n1 = nodes.indexOf(e.p1);
            int n2 = nodes.indexOf(e.p2);
            String dir;
            if (e.startForward != null) {
                dir = e.startForward.booleanValue() ? ">" : "<";
            } else {
                dir = "-";
            }
            w.println(n1 + " " + n2 + " #" + e.weight + "# " + (e.arc == null ? "l" : "b " + e.arc.x + " " + e.arc.y) + " " + dir);
        }
        w.close();
    }

    private void reset() {
        nodes.clear();
        edges.clear();
        coeff = null;
    }

    public void load(Readable file) {
        reset();

        try (Scanner r = new Scanner(file)) {
            r.useLocale(Locale.US);

            int nnodes = r.nextInt();
            for (int i = 0; i < nnodes; i++) {
                int x = r.nextInt();
                int y = r.nextInt();
                String weightStr = r.next();
                Integer weight = null;
                if (weightStr != null && !NO_WEIGHT.equals(weightStr)) {
                    try {
                        weight = Integer.valueOf(weightStr);
                    } catch (NumberFormatException nfex) {
                        // ignore
                    }
                }
                Node node = new Node(new Point(x, y), weight);
                nodes.add(node);
            }
            int nedges = r.nextInt();
            for (int i = 0; i < nedges; i++) {
                int n1 = r.nextInt();
                int n2 = r.nextInt();
                String weight = r.next("#.*#");
                weight = weight.substring(1, weight.length() - 1);
                String s = r.next();
                Point arc = null;
                if ("b".equals(s)) {
                    int arcX = r.nextInt();
                    int arcY = r.nextInt();
                    arc = new Point(arcX, arcY);
                }
                Edge edge = new Edge(nodes.get(n1), nodes.get(n2), weight);
                if (r.hasNext("[-><]")) {
                    String dir = r.next();
                    if (">".equals(dir)) {
                        edge.startForward = true;
                    } else if ("<".equals(dir)) {
                        edge.startForward = false;
                    }
                }
                edge.arc = arc;
                edges.add(edge);
            }
        }
    }

    public void createNew() {
        reset();
    }

    public int getNumVertex() {
        return nodes.size();
    }

    public Integer getNodeWeight(int vertex) {
        return nodes.get(vertex).weight;
    }

    public SimpleEdge[] toSimple(Arithmetic a) {
        JSRunner runner = new JSRunner(a);
        SimpleEdge[] se = new SimpleEdge[edges.size()];
        for (int i = 0; i < edges.size(); i++) {
            Edge edge = edges.get(i);
            int n1 = nodes.indexOf(edge.p1);
            int n2 = nodes.indexOf(edge.p2);
            Number evalResult = runner.evaluate(edge.weight);
            Number length = a.evaluate(evalResult);
            se[i] = new SimpleEdge(n1, n2, length, getStartDirection(edge));
        }
        return se;
    }

    public double getCoeff() {
        if (coeff == null) {
            double sumLen = 0;
            double prodLen = 1;
            double fact = 1;
            JSRunner runner = new JSRunner(Arithmetic.createArithmetic(0, 0));
            for (int i = 0; i < edges.size(); i++) {
                Edge edge = edges.get(i);
                double ti = runner.evaluate(edge.weight).doubleValue();
                prodLen *= ti;
                sumLen += ti;
                if (i > 0) {
                    fact *= i;
                }
            }
            coeff = sumLen / (fact * prodLen * Math.pow(2, nodes.size() - 2));
        }
        return coeff.doubleValue();
    }

    public int getIncoming(int vertex) {
        Node node = nodes.get(vertex);
        int count = 0;
        for (Edge edge : edges) {
            if (edge.p2 == node) {
                count++;
            }
        }
        return count;
    }

    public int getOutgoing(int vertex) {
        Node node = nodes.get(vertex);
        int count = 0;
        for (Edge edge : edges) {
            if (edge.p1 == node) {
                count++;
            }
        }
        return count;
    }

    public Graph toGraph(Arithmetic a) {
        SimpleEdge[] edges = toSimple(a);
        Integer[] weights = new Integer[getNumVertex()];
        for (int i = 0; i < weights.length; i++) {
            weights[i] = getNodeWeight(i);
        }
        return new Graph(weights, edges);
    }
}
