package quantum.comparator;

import common.math.PolyUtil;

import java.io.IOException;
import java.util.List;

public class Test {

    public static void main(String[] args) throws IOException {
        String what = "fish_3";
        args = new String[] {
            "D:\\work\\quantum\\new\\cpp\\" + what + ".out",
            "D:\\work\\quantum\\new\\cpp\\" + what + ".rad",
            "D:\\work\\quantum\\new\\cpp\\" + what + ".txt"
        };
        TableReader out = TableReader.read(args[0]);
        TableReader rad = TableReader.read(args[1]);
        GraphReader graph = GraphReader.read(args[2]);
        List<Double> times = out.times;

        int radColumn = 0;
        for (int i = 0; i < rad.columns.size(); i++) {
            Double values = rad.columns.get(i).get(10);
            if (values.intValue() > 0) {
                radColumn = i;
                break;
            }
        }
        List<Double> radValues = rad.columns.get(radColumn);

        int to = times.size();
        Double prev = null;
        for (int i = 0; i < times.size(); i++) {
            Double radValue = radValues.get(i);
            if (prev != null && radValue.intValue() < prev.intValue()) {
                to = i - 10;
                break;
            }
            prev = radValue;
        }
        times = times.subList(0, to);
        List<Double> outValues = out.columns.get(0).subList(0, to);
        radValues = radValues.subList(0, to);

        int m = graph.edges.length;
        double[] cc = PolyUtil.leastSquares(times, outValues, m);
        double[] rc = PolyUtil.leastSquares(times, radValues, m + 1);
        double c = cc[cc.length - 1];
        double r = rc[rc.length - 1];
        double sum = 0;
        for (double edge : graph.edges) {
            sum += edge;
        }
        System.out.println("C = " + c);
        System.out.println("C = " + (2 * m * r * sum));
    }
}
