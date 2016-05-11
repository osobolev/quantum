package quantum.qtree.pascal;

import java.util.Arrays;

public class Tree {

    private static final double k3 = 2.0 / 3.0;
    private static final double k3_1 = k3 - 1;
    private static final double k4 = 2.0 / 4.0;
    private static final double k4_1 = k4 - 1;

    private final boolean tail;

    private double ampFwd0;
    private double ampBack0;
    private double[][] ampFwd;
    private double[][] ampBack;

    private double[][] newFwd;
    private double[][] newBack;

    private int max = 1;

    public Tree(int len, boolean tail) {
        this.tail = tail;
        ampFwd = new double[len][];
        ampBack = new double[len][];
        newFwd = new double[len][];
        newBack = new double[len][];
        for (int i = 0; i < len; i++) {
            int n = 2 * (i + 1);
            ampFwd[i] = new double[n];
            ampBack[i] = new double[n];
            newFwd[i] = new double[n];
            newBack[i] = new double[n];
        }
        if (tail) {
            ampFwd0 = 1.0;
        } else {
            ampFwd[0][0] = 1.0;
            ampFwd[0][1] = 1.0;
        }
    }

    boolean isEnd() {
        return max >= ampFwd.length;
    }

    void step() {
        for (int i = 0; i < max; i++) {
            Arrays.fill(newFwd[i], 0);
            Arrays.fill(newBack[i], 0);
        }
        double newFwd0 = 0;
        double newBack0 = 0;

        newFwd[0][0] += k3 * ampFwd0;
        newFwd[0][1] += k3 * ampFwd0;
        newBack0 += k3_1 * ampFwd0;

        newFwd0 += -ampBack0;

        for (int i = 0; i < max; i++) {
            int height = ampFwd[i].length;
            for (int j = 0; j < height; j++) {
                double fwd = ampFwd[i][j];
                double back = ampBack[i][j];
                if (j == 0) {
                    newFwd[i + 1][j] += k3 * fwd;
                    newFwd[i + 1][j + 1] += k3 * fwd;
                    newBack[i][j] += k3_1 * fwd;

                    if (i == 0) {
                        if (tail) {
                            newFwd[i][j] += k3_1 * back;
                            newFwd[i][j + 1] += k3 * back;
                            newBack0 += k3 * back;
                        } else {
                            newFwd[i][j + 1] += back;
                        }
                    } else {
                        newFwd[i][j] += k3_1 * back;
                        newFwd[i][j + 1] += k3 * back;
                        newBack[i - 1][j] += k3 * back;
                    }
                } else if (j == height - 1) {
                    newFwd[i + 1][j + 1] += k3 * fwd;
                    newFwd[i + 1][j + 2] += k3 * fwd;
                    newBack[i][j] += k3_1 * fwd;

                    if (i == 0) {
                        if (tail) {
                            newFwd[i][j] += k3_1 * back;
                            newFwd[i][j - 1] += k3 * back;
                            newBack0 += k3 * back;
                        } else {
                            newFwd[i][j - 1] += back;
                        }
                    } else {
                        newFwd[i][j] += k3_1 * back;
                        newFwd[i][j - 1] += k3 * back;
                        newBack[i - 1][j - 2] += k3 * back;
                    }
                } else {
                    if (j % 2 == 0) {
                        newBack[i][j - 1] += k4 * fwd; // goes to [i][j-1]
                        newFwd[i + 1][j] += k4 * fwd; // goes to [i+1][j]
                        newFwd[i + 1][j + 1] += k4 * fwd; // goes to [i+1][j+1]
                        newBack[i][j] += k4_1 * fwd; // goes back to [i][j]

                        if (j == height - 2) {
                            newFwd[i][j] += k3_1 * back;
                            newFwd[i][j + 1] += k3 * back;
                            newBack[i - 1][j - 1] += k3 * back;
                        } else {
                            newFwd[i][j] += k4_1 * back;
                            newFwd[i][j + 1] += k4 * back;
                            newBack[i - 1][j - 1] += k4 * back;
                            newBack[i - 1][j] += k4 * back;
                        }
                    } else {
                        newBack[i][j + 1] += k4 * fwd; // goes to [i][j+1]
                        newFwd[i + 1][j + 1] += k4 * fwd; // goes to [i+1][j+1]
                        newFwd[i + 1][j + 2] += k4 * fwd; // goes to [i+1][j+2]
                        newBack[i][j] += k4_1 * fwd; // goes back to [i][j]

                        if (j == 1) {
                            newFwd[i][j] += k3_1 * back;
                            newFwd[i][j - 1] += k3 * back;
                            newBack[i - 1][j - 1] += k3 * back;
                        } else {
                            newFwd[i][j] += k4_1 * back;
                            newFwd[i][j - 1] += k4 * back;
                            newBack[i - 1][j - 2] += k4 * back;
                            newBack[i - 1][j - 1] += k4 * back;
                        }
                    }
                }
            }
        }
        max++;
        {
            double[][] tempFwd = newFwd;
            newFwd = ampFwd;
            ampFwd = tempFwd;
            double[][] tempBack = newBack;
            newBack = ampBack;
            ampBack = tempBack;

            ampFwd0 = newFwd0;
            ampBack0 = newBack0;
        }
    }

    private double speed = 0;

    double[] getEnergy() {
        int num = this.max;
        int delta = tail ? 1 : 0;
        double[] result = new double[delta + max];
        if (tail) {
            result[0] = getEnergy0();
        }
        double max = 0;
        int maxi = -1;
        for (int i = 0; i < num; i++) {
            int height = ampFwd[i].length;
            double sum = 0;
            for (int j = 0; j < height; j++) {
                double fwd = ampFwd[i][j];
                double back = ampBack[i][j];
                sum += fwd * fwd;
                sum += back * back;
            }
            result[delta + i] = sum;
            if (i > 10 && sum > max) {
                max = sum;
                maxi = i;
            }
        }
        if (maxi >= 0) {
            speed = (double) maxi / num;
        }
        return result;
    }

    double getSpeed() {
        return speed;
    }

    double getTotalEnergy() {
        double sum = getEnergy0();
        for (int i = 0; i < max; i++) {
            int height = ampFwd[i].length;
            for (int j = 0; j < height; j++) {
                double fwd = ampFwd[i][j];
                double back = ampBack[i][j];
                sum += fwd * fwd;
                sum += back * back;
            }
        }
        return sum;
    }

    private double getEnergy0() {
        return ampFwd0 * ampFwd0 + ampBack0 * ampBack0;
    }

    void print() {
        if (tail) {
            System.out.println("0 " + getEnergy0());
        }
        for (int i = 0; i < max; i++) {
            int height = ampFwd[i].length;
            System.out.print(i + 1);
            for (int j = 0; j < height; j++) {
                double fwd = ampFwd[i][j];
                double back = ampBack[i][j];
                System.out.print(" " + (fwd * fwd + back * back));
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        Tree tree = new Tree(100, true);
        while (!tree.isEnd()) {
            System.out.println(tree.max + ": " + tree.getTotalEnergy());
            tree.step();
        }
        //tree.print();
    }
}
