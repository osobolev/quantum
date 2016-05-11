package quantum.comparator;

import common.math.PolyUtil;

import java.io.IOException;

public final class Hypo2 {

    private static double get(double[] a, double[] b, double[] x, double[] y, int i) {
        return 2 * (a[i] + b[i] - x[i] - y[i]);
    }

    public static void main(String[] args) throws IOException {
        TableReader a = TableReader.read("C:\\work\\projects\\quantum\\cpp\\diff\\4\\A1.out");
        TableReader b = TableReader.read("C:\\work\\projects\\quantum\\cpp\\diff\\4\\B1.out");
        TableReader x = TableReader.read("C:\\work\\projects\\quantum\\cpp\\diff\\4\\X1.out");
        TableReader y = TableReader.read("C:\\work\\projects\\quantum\\cpp\\diff\\4\\Y1.out");
        int deg = 5;
        for (int n = 50; n <= 99; n += 5) {
            double[] ac = PolyUtil.leastSquares(a.times.subList(0, n), a.columns.get(0).subList(0, n), deg);
            double[] bc = PolyUtil.leastSquares(b.times.subList(0, n), b.columns.get(0).subList(0, n), deg);
            double[] xc = PolyUtil.leastSquares(x.times.subList(0, n), x.columns.get(0).subList(0, n), deg);
            double[] yc = PolyUtil.leastSquares(y.times.subList(0, n), y.columns.get(0).subList(0, n), deg);
            double v2 = get(ac, bc, xc, yc, 2);
            //double v2 = yc[4];
            System.out.println(v2);
        }
        double t1 = Math.sqrt(11);
        double t2 = Math.sqrt(3);
        double t3 = Math.sqrt(2);
        double t4 = Math.sqrt(5);
        double t5 = Math.sqrt(7);
        double p = -0.25 * (1 / (t1 * t2) + 1 / (t4 * t5) - 1 / (t2 * t4) - 1 / (t1 * t5)) / 3;
        //System.out.println(p);
    }
}
