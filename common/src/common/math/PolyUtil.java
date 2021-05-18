package common.math;

import common.matrix.Matrix;

import java.util.List;

public final class PolyUtil {

    /**
     * @param numCoeff число коэффициентов многочлена (т.е. степень будет на 1 меньше)
     */
    public static double[] leastSquares(List<Double> times, List<Double> photons, int numCoeff) {
        if (times.size() < numCoeff)
            return null;
        double[][] x = new double[times.size()][numCoeff];
        for (int i = 0; i < times.size(); i++) {
            double t = times.get(i).doubleValue();
            double s = 1;
            for (int j = 0; j < numCoeff; j++) {
                x[i][j] = s;
                s *= t;
            }
        }
        double[][] y = new double[photons.size()][1];
        for (int i = 0; i < photons.size(); i++) {
            y[i][0] = photons.get(i).doubleValue();
        }

        Matrix mx = new Matrix(x);
        Matrix my = new Matrix(y);

        Matrix ma = mx.transpose().times(mx).inverse().times(mx.transpose()).times(my);
        double[][] a = ma.getArray();
        double[] ret = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            ret[i] = a[i][0];
        }
        return ret;
    }

    public static double symmpoly(double[] w) {
        double sum = 0;
        for (int i = 0; i < w.length; i++) {
            double prod = 1;
            for (int j = 0; j < w.length; j++) {
                if (j != i) {
                    prod *= w[j];
                }
            }
            sum += prod;
        }
        return sum;
    }
}
