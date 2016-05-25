package quantum.comparator;

import common.graph.Graph;
import common.graph.SimpleEdge;

import java.io.File;
import java.io.FileNotFoundException;

public final class Cylinder {

//    private static final double T0 = 1.0;
    private static final double T0 = Math.sqrt(Math.PI / 3);
    private static final double TSTAR = Math.sqrt(2);
    //private static final double TSTAR = 2.0;
    private static final double R = 1.0;
    //private static final double D = 2.0 * Math.PI * R;
    private static final double D = 6.0;

    private static double sqr(double x) {
        return x * x;
    }

    private static Graph create(int n) {
        SimpleEdge[] edges = new SimpleEdge[n + 4];
        edges[0] = new SimpleEdge(0, 1, TSTAR, true);
        for (int i = 0; i <= n; i++) {
            double length = Math.sqrt(sqr(T0) + sqr(i * D));
            edges[1 + i] = new SimpleEdge(0, 1, length, true);
        }
        edges[n + 2] = new SimpleEdge(0, 0, D, true);
        edges[n + 3] = new SimpleEdge(1, 1, D, null);
        return new Graph(new Integer[2], edges);
    }

    public static void main(String[] args) throws FileNotFoundException {
        String str = args.length <= 0 ? "100" : args[0];
        double t = Double.parseDouble(str);
        int n = (int) Math.ceil(Math.sqrt(sqr(t) - sqr(T0)) / D);
        System.out.println(n);
        create(n).toC(new File("cylinder" + str + ".txt"));
    }
}
