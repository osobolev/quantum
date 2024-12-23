package common.graph;

import common.math.PolyUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public final class Graph {

    private final int numVertex;
    private final Number[] lengths; // [numEdges]
    private final int[][] outEdges; // [numVertex][]
    private final int[][] outEdgesFwd; // [numVertex][]
    private final int[][] inEdgesFwd; // [numVertex][]
    private final int[][] destination; // [numEdges][2]
    private final Integer[] weights; // [numVertex]
    private final Boolean[] startForward; // [numEdges]
    private final int degree;
    private final double sigma;

    public Graph(Integer[] weights, SimpleEdge[] edges) {
        this.numVertex = weights.length;
        this.weights = weights;
        outEdges = new int[numVertex][];
        outEdgesFwd = new int[numVertex][];
        inEdgesFwd = new int[numVertex][];
        lengths = new Number[edges.length];
        destination = new int[edges.length][2];
        startForward = new Boolean[edges.length];
        Map<Integer, List<Integer>> outMap = new HashMap<>(numVertex);
        Map<Integer, List<Integer>> outMapFwd = new HashMap<>(numVertex);
        Map<Integer, List<Integer>> inMapFwd = new HashMap<>(numVertex);
        for (int i = 0; i < edges.length; i++) {
            SimpleEdge e = edges[i];
            lengths[i] = e.length;
            startForward[i] = e.startForward;
            destination[i][0] = e.node1;
            destination[i][1] = e.node2;
            put(outMap, e.node1, i);
            put(outMap, e.node2, i);
            put(outMapFwd, e.node1, i);
            put(inMapFwd, e.node2, i);
        }
        int maxEdges = 0;
        int maxVertex = -1;
        for (int i = 0; i < numVertex; i++) {
            List<Integer> vedges = outMap.get(i);
            outEdges[i] = toArray(vedges);
            if (vedges != null) {
                int numEdges = vedges.size();
                if (numEdges > maxEdges) {
                    maxEdges = numEdges;
                    maxVertex = i;
                }
            }
            outEdgesFwd[i] = toArray(outMapFwd.get(i));
            inEdgesFwd[i] = toArray(inMapFwd.get(i));
        }
        degree = maxEdges;
        if (maxVertex >= 0) {
            int[] maxOutEdges = goingOut(maxVertex);
            double[] w = new double[maxOutEdges.length];
            for (int i = 0; i < maxOutEdges.length; i++) {
                w[i] = 1 / getEdgeLength(maxOutEdges[i]).doubleValue();
            }
            sigma = PolyUtil.symmpoly(w);
        } else {
            sigma = 0;
        }
    }

    private Graph(int numVertex, Number[] lengths, int[][] outEdges, int[][] outEdgesFwd, int[][] inEdgesFwd, int[][] destination, Integer[] weights, Boolean[] startForward, int degree, double sigma) {
        this.numVertex = numVertex;
        this.lengths = lengths;
        this.outEdges = outEdges;
        this.outEdgesFwd = outEdgesFwd;
        this.inEdgesFwd = inEdgesFwd;
        this.destination = destination;
        this.weights = weights;
        this.startForward = startForward;
        this.degree = degree;
        this.sigma = sigma;
    }

    private static int[] toArray(List<Integer> c) {
        if (c == null)
            return new int[0];
        int num = c.size();
        int[] array = new int[num];
        int j = 0;
        for (Integer e : c) {
            array[j++] = e.intValue();
        }
        return array;
    }

    private static void put(Map<Integer, List<Integer>> outMap, int vertex, int edge) {
        outMap.computeIfAbsent(vertex, k -> new ArrayList<>()).add(edge);
    }

    public int getVertexNum() {
        return numVertex;
    }

    public int getEdgeNum() {
        return lengths.length;
    }

    public int[] goingOut(int vertex) {
        return outEdges[vertex];
    }

    public int[] goingOutFwd(int vertex) {
        return outEdgesFwd[vertex];
    }

    public int[] goingInFwd(int vertex) {
        return inEdgesFwd[vertex];
    }

    public Number getEdgeLength(int edge) {
        return lengths[edge];
    }

    public int destination(int source, int edge) {
        int[] vert = destination[edge];
        if (source == vert[0]) {
            return vert[1];
        } else if (source == vert[1]) {
            return vert[0];
        } else {
            return -1;
        }
    }

    public boolean isEdgeStart(int edge, int vertex) {
        int[] vert = destination[edge];
        return vertex == vert[0];
    }

    public int getEdgeSide(int edge, boolean forward) {
        int[] vert = destination[edge];
        return forward ? vert[1] : vert[0];
    }

    public boolean direction(int source, int edge) {
        int[] vert = destination[edge];
        return source == vert[0];
    }

    public boolean isLoop(int edge) {
        int[] vert = destination[edge];
        return vert[0] == vert[1];
    }

    public int getDegree() {
        return degree;
    }

    public double getSigma() {
        return sigma;
    }

    public void toC(File file) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(file);
        pw.printf("%d %d %d%n", numVertex, lengths.length, degree);
        for (Number length : lengths) {
            pw.printf(Locale.US, "%.20f%n", length.doubleValue());
        }
        for (int[] dest : destination) {
            pw.printf("%d %d%n", dest[0], dest[1]);
        }
        for (int[] outEdge : outEdges) {
            pw.printf("%d", outEdge.length);
            for (int out : outEdge) {
                pw.printf(" %d", out);
            }
            pw.println();
        }
        for (int i = 0; i < startForward.length; i++) {
            Boolean fwd = startForward[i];
            if (fwd != null) {
                pw.printf("%d %d%n", i, fwd.booleanValue() ? 0 : 1);
            }
        }
        pw.close();
    }

    public Integer getVertexWeight(int vertex) {
        return weights[vertex];
    }

    public Boolean isStartForward(int edge) {
        return startForward[edge];
    }

    public Graph withEdge(int edge, Number length) {
        Number[] newLengths = new Number[lengths.length];
        System.arraycopy(lengths, 0, newLengths, 0, lengths.length);
        newLengths[edge] = length;
        return new Graph(
            numVertex, newLengths, outEdges, outEdgesFwd, inEdgesFwd, destination, weights, startForward, degree, sigma
        );
    }

    public static void main(String[] args) throws FileNotFoundException {
        Graph graph = new Graph(new Integer[3], new SimpleEdge[] {
            new SimpleEdge(0, 1, Math.sqrt(2), true),
            new SimpleEdge(0, 2, Math.sqrt(5), null),
            new SimpleEdge(0, 0, 2 * Math.sqrt(3), null)
        });
        graph.toC(new File("D:\\star31.txt"));
    }
}
