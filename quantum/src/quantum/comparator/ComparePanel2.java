package quantum.comparator;

import common.math.PolyUtil;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

final class ComparePanel2 extends JComponent {

    private static final Color[] COLORS = {Color.green, Color.red, Color.black, Color.cyan, Color.magenta};

    private final List<Double> times;
    private final List<Double> photons;
    private final List<Integer> toCompare = new ArrayList<>();
    private final List<double[]> compareCoeffs = new ArrayList<>();

    private final NumberFormat cdf;

    private int start = 60;

    ComparePanel2(List<Double> times, List<Double> photons) {
        this.times = times;
        this.photons = photons;

        DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance(Locale.US);
        cdf = new DecimalFormat("#.########E0", dfs);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        double width = getWidth() - 1;
        double height = getHeight() - 1;
        double minTime = times.get(start).doubleValue();
        double maxTime = times.get(times.size() - 1).doubleValue();
        for (int i = 0; i < toCompare.size(); i++) {
            int degree = toCompare.get(i).intValue();
            g.setColor(COLORS[i % COLORS.length]);
            double min = 0;
            double max = 0;
            for (int j = start; j < times.size(); j++) {
                double time = times.get(j).doubleValue();
                double value = photons.get(j).doubleValue() / Math.pow(time, degree);
                //min = Math.min(min, value);
                max = Math.max(max, value);
            }
            int prevx = 0;
            int prevy = 0;
            for (int j = start; j < times.size(); j++) {
                double time = times.get(j).doubleValue();
                double value = photons.get(j).doubleValue() / Math.pow(time, degree);
                int x = (int) Math.round((time - minTime) / (maxTime - minTime) * width);
                int y = (int) Math.round(height - (value - min) / (max - min) * height);
                if (j > start) {
                    g.drawLine(prevx, prevy, x, y);
                }
                prevx = x;
                prevy = y;
            }
            double coeff = compareCoeffs.get(i)[degree];
            int base = (int) Math.round(height - (coeff - min) / (max - min) * height);
            g.drawLine(0, base, getWidth(), base);
        }
        for (int i = 0; i < toCompare.size(); i++) {
            int degree = toCompare.get(i).intValue();
            g.setColor(COLORS[i % COLORS.length]);
            g.drawString(degree + ": " + cdf.format(compareCoeffs.get(i)[degree]), 20, 20 + 20 * i);
        }
    }

    private void recalc() {
        compareCoeffs.clear();
        for (Integer degree : toCompare) {
            double[] doubles = PolyUtil.leastSquares(
                times.subList(start, times.size()),
                photons.subList(start, times.size()),
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

    void setStart(int start) {
        this.start = start;
        recalc();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(800, 600);
    }
}
