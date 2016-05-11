package quantum.qtree;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

final class TreePainter {

    private static int calcY(int[] ys, int mindist, int divs) {
        for (int i = 0; i < ys.length; i++) {
            ys[i] = i * mindist;
        }
        int n = ys.length;
        for (int k = 0; k < divs; k++) {
            for (int i = 0, j = 0; i < n; i += 2, j++) {
                ys[j] = (ys[i] + ys[i + 1]) / 2;
            }
            n /= 2;
        }
        return n;
    }

    private static Color toRGB(float h, float s, float v) {
        if (s == 0) {
            // achromatic (grey)
            return new Color(v, v, v);
        }
        h /= 60;            // sector 0 to 5
        int i = (int) Math.floor(h);
        float f = h - i;
        float p = v * (1 - s);
        float q = v * (1 - s * f);
        float t = v * (1 - s * (1 - f));
        switch (i) {
        case 0: return new Color(v, t, p);
        case 1: return new Color(q, v, p);
        case 2: return new Color(p, v, t);
        case 3: return new Color(p, q, v);
        case 4: return new Color(t, p, v);
        default: return new Color(v, p, q);
        }
    }

    private static Color getColor(double energy, double maxEnergy) {
        float weighted = (float) (energy / maxEnergy);
        return toRGB(240 * (1 - weighted), 1, 1);
        //return new Color(weighted, 0, 1 - weighted);
    }

    public static void draw(double[] energy, double[][] trees, int len, File file) throws IOException {
        double maxEnergy = 0;
        for (double e : energy) {
            if (e > maxEnergy) {
                maxEnergy = e;
            }
        }
        int treeLen = 0;
        for (double[] tree : trees) {
            treeLen = tree.length;
            for (double e : tree) {
                if (e > maxEnergy) {
                    maxEnergy = e;
                }
            }
        }

        int maxBranch = 1 << len;
        int mindist = 10;
        int width1 = 40;
        int ymargin = 10;
        int xmargin = 10;
        int height = (maxBranch - 1) * mindist + ymargin * 2;
        int width = (len + treeLen) * width1 + xmargin * 2;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();

        g.setColor(Color.white);
        g.fillRect(0, 0, width, height);

        int[] ys0 = new int[maxBranch];
        int[] ys1 = new int[maxBranch];
        for (int i = 0; i < len; i++) {
            int x0 = xmargin + i * width1;
            int x1 = x0 + width1;
            int n = calcY(ys0, mindist, len - 1 - i + 1);
            if (n == 1) {
                g.setColor(getColor(energy[0], maxEnergy));
                g.drawLine(x0, ymargin + ys0[0], x1, ymargin + ys0[0]);
            } else {
                calcY(ys1, mindist, len - i + 1);
                int start = (1 << i) - 1;
                for (int j = 0; j < n; j++) {
                    g.setColor(getColor(energy[start + j], maxEnergy));
                    g.drawLine(x0, ymargin + ys1[j / 2], x1, ymargin + ys0[j]);
                }
            }
        }
        for (int i = 0; i < trees.length; i++) {
            double[] tree = trees[i];
            if (tree.length <= 0)
                continue;
            int x0 = xmargin + len * width1;
            int x1 = x0 + width1;
            int y0 = ymargin + ys0[i / 2];
            g.setColor(getColor(tree[0], maxEnergy));
            int y1 = ymargin + i * mindist;
            g.drawLine(x0, y0, x1, y1);
            for (int j = 1; j < tree.length; j++) {
                x0 = x1;
                x1 += width1;
                g.setColor(getColor(tree[j], maxEnergy));
                g.drawLine(x0, y1, x1, y1);
            }
        }

        String name = file.getName();
        int p = name.lastIndexOf('.');
        if (p >= 0) {
            name = name.substring(0, p);
        }
        File gifFile = new File(file.getParentFile(), name + ".gif");
        ImageIO.write(image, "gif", gifFile);
    }

    public static void main(String[] args) {
        for (int i = 0; i <= 240; i += 10) {
            System.out.println(toRGB(i, 1, 1));
        }
    }
}
