package quantum.comparator;

import common.math.PolyUtil;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

final class ComparePanel1 extends JComponent {

    private static final Color[] COLORS = {Color.green.darker(), Color.red, Color.black, Color.cyan.darker(), Color.magenta.darker()};

    private final List<Double> times;
    private final List<Double> photons;
    private final double maxPhotons;
    private final double minPhotons;
    private final List<Integer> toCompare = new ArrayList<Integer>();
    private final List<double[]> compareCoeffs = new ArrayList<double[]>();

    private final NumberFormat cdf;

    private int start;
    private int end;

    ComparePanel1(List<Double> times, List<Double> photons) {
        this.times = times;
        this.photons = photons;
        maxPhotons = Collections.max(photons).doubleValue();
        minPhotons = Collections.min(photons).doubleValue();
        start = times.size() / 8;
        end = times.size();

        DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance(Locale.US);
        cdf = new DecimalFormat("#.########E0", dfs);
    }

    private static double poly(double[] coeffs, double x) {
        double sum = 0;
        for (int i = coeffs.length - 1; i >= 0; i--) {
            double coeff = coeffs[i];
            sum *= x;
            sum += coeff;
        }
        return sum;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.blue);
        double width = getWidth() - 1;
        double height = getHeight() - 1;
        double minTime = times.get(0).doubleValue();
        double maxTime = times.get(times.size() - 1).doubleValue();
        for (int i = 0; i < times.size(); i++) {
            double time = times.get(i).doubleValue();
            double ph = photons.get(i).doubleValue();
            int x = (int) Math.round((time - minTime) / (maxTime - minTime) * width);
            int y = (int) Math.round(height - (ph - minPhotons) / (maxPhotons - minPhotons) * height);
            g.drawOval(x - 3, y - 3, 6, 6);
        }
        for (int i = 0; i < compareCoeffs.size(); i++) {
            double[] coeffs = compareCoeffs.get(i);
            g.setColor(COLORS[i % COLORS.length]);
            int prevy = 0;
            for (int x = 0; x < getWidth(); x++) {
                double time = x / width * (maxTime - minTime) + minTime;
                double value = poly(coeffs, time);
                int y = (int) Math.round(height - (value - minPhotons) / (maxPhotons - minPhotons) * height);
                if (x > 0) {
                    g.drawLine(x - 1, prevy, x, y);
                }
                prevy = y;
            }
        }
        for (int i = 0; i < toCompare.size(); i++) {
            int degree = toCompare.get(i).intValue();
            g.setColor(COLORS[i % COLORS.length]);
            StringBuilder buf = new StringBuilder();
            for (int j = degree; j >= 0; j--) {
                if (buf.length() > 0) {
                    buf.append(" + ");
                }
                buf.append(cdf.format(compareCoeffs.get(i)[j])).append(" t^").append(j);
            }
            g.drawString(degree + ": " + buf, 20, 20 + 20 * i);
        }
    }

    private void recalc() {
        compareCoeffs.clear();
        for (Integer degree : toCompare) {
            double[] doubles = PolyUtil.leastSquares(
                times.subList(start, end),
                photons.subList(start, end),
                degree.intValue() + 1
            );
            compareCoeffs.add(doubles);
        }
        repaint();
    }

    void setCompare(List<Integer> toCompare) {
        this.toCompare.clear();
        this.toCompare.addAll(toCompare);
        recalc();
    }

    void setEnd(int end) {
        this.end = end;
        recalc();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(800, 600);
    }
}
