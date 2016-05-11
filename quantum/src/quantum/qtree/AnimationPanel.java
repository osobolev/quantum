package quantum.qtree;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;

final class AnimationPanel extends JComponent {

    private final DecimalFormat df6 = new DecimalFormat("#.######");

    private int count = 0;
    private int center = -1;
    private double[] energy = null;
    private double speed = 0;
    private double scale;

    void setCount(int count, int center) {
        this.count = count;
        this.center = center;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        double[] data = energy;
        if (data == null)
            return;

        g.setColor(Color.black);
        int fh = g.getFontMetrics().getHeight();
        g.drawString("Step: " + data.length, 10, 20);
        g.drawString("Speed: " + df6.format(speed), 10, 20 + fh);

        g.setColor(Color.green.darker());
        int xmargin = 10;
        int ymargin = 10;
        int x = xmargin;
        int y = getHeight() - ymargin;
        int width = (getWidth() - 2 * xmargin) / count;
        if (width < 1)
            width = 1;
        int right = xmargin + data.length * width;
        if (right > getWidth() - xmargin) {
            g.translate(getWidth() - right - xmargin, 0);
        }
        int height = getHeight() - 2 * ymargin;
        for (double e : data) {
            double scaled = e * scale;
            if (scaled > 1)
                scaled = 1;
            int h = (int) Math.round(height * scaled);
            g.fillRect(x, y - h + 1, width, h);
            x += width;
        }
        g.setColor(Color.black);
        g.drawLine(x, ymargin, x, ymargin + height);
        if (center >= 0) {
            int centerx = xmargin + width * center;
            g.drawLine(centerx, y + 10, centerx, y - 10);
            g.drawLine(centerx - 1, y + 10, centerx - 1, y - 10);
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension(800, 600);
    }

    void setData(double[] energy, double speed) {
        this.energy = energy;
        this.speed = speed;
        repaint();
    }

    void setScale(double scale) {
        this.scale = scale;
        repaint();
    }

    void savePicture(File file) throws IOException {
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        paint(image.getGraphics());
        ImageIO.write(image, "gif", file);
    }

    void saveText(File file) throws IOException {
        double[] data = energy;
        if (data == null)
            return;
        PrintWriter pw = new PrintWriter(file);
        for (double x : data) {
            pw.printf("%10.6f%n", x);
        }
        pw.close();
    }
}
