package quantum.qtree.pascal;

import java.util.Arrays;

public class RTree {

    private static final int N = 10;

    private static final Rational k3 = new Rational(2, 3);
    private static final Rational k3_1 = k3.sub(Rational.ONE);
    private static final Rational k4 = new Rational(2, 4);
    private static final Rational k4_1 = k4.sub(Rational.ONE);

    private Rational[][] ampFwd;
    private Rational[][] ampBack;

    private Rational[][] newFwd;
    private Rational[][] newBack;

    private int max = 1;

    public RTree() {
        ampFwd = new Rational[N][];
        ampBack = new Rational[N][];
        newFwd = new Rational[N][];
        newBack = new Rational[N][];
        for (int i = 0; i < N; i++) {
            int n = 2 * (i + 1);
            ampFwd[i] = new Rational[n];
            ampBack[i] = new Rational[n];
            newFwd[i] = new Rational[n];
            newBack[i] = new Rational[n];
        }
        ampFwd[0][0] = Rational.ONE;
        ampFwd[0][1] = Rational.ONE;
        ampBack[0][0] = Rational.ZERO;
        ampBack[0][1] = Rational.ZERO;
    }

    boolean isEnd() {
        return max >= N;
    }

    void step() {
        for (int i = 0; i <= max; i++) {
            Arrays.fill(newFwd[i], Rational.ZERO);
            Arrays.fill(newBack[i], Rational.ZERO);
        }
        for (int i = 0; i < max; i++) {
            int height = ampFwd[i].length;
            for (int j = 0; j < height; j++) {
                Rational fwd = ampFwd[i][j];
                Rational back = ampBack[i][j];
                if (j == 0) {
                    newFwd[i + 1][j] = newFwd[i + 1][j].add(k3.mul(fwd));
                    newFwd[i + 1][j + 1] = newFwd[i + 1][j + 1].add(k3.mul(fwd));
                    newBack[i][j] = newBack[i][j].add((k3_1.mul(fwd)));

                    if (i == 0) {
                        newFwd[i][j + 1] = newFwd[i][j + 1].add(back);
                    } else {
                        newFwd[i][j] = newFwd[i][j].add(k3_1.mul(back));
                        newFwd[i][j + 1] = newFwd[i][j + 1].add(k3.mul(back));
                        newBack[i - 1][j] = newBack[i - 1][j].add(k3.mul(back));
                    }
                } else if (j == height - 1) {
                    newFwd[i + 1][j + 1] = newFwd[i + 1][j + 1].add(k3.mul(fwd));
                    newFwd[i + 1][j + 2] = newFwd[i + 1][j + 2].add(k3.mul(fwd));
                    newBack[i][j] = newBack[i][j].add(k3_1.mul(fwd));

                    if (i == 0) {
                        newFwd[i][j - 1] = newFwd[i][j - 1].add(back);
                    } else {
                        newFwd[i][j] = newFwd[i][j].add(k3_1.mul(back));
                        newFwd[i][j - 1] = newFwd[i][j - 1].add(k3.mul(back));
                        newBack[i - 1][j - 2] = newBack[i - 1][j - 2].add(k3.mul(back));
                    }
                } else {
                    if (j % 2 == 0) {
                        newBack[i][j - 1] = newBack[i][j - 1].add(k4.mul(fwd)); // goes to [i][j-1]
                        newFwd[i + 1][j] = newFwd[i + 1][j].add(k4.mul(fwd)); // goes to [i+1][j]
                        newFwd[i + 1][j + 1] = newFwd[i + 1][j + 1].add(k4.mul(fwd)); // goes to [i+1][j+1]
                        newBack[i][j] = newBack[i][j].add(k4_1.mul(fwd)); // goes back to [i][j]

                        if (j == height - 2) {
                            newFwd[i][j] = newFwd[i][j].add(k3_1.mul(back));
                            newFwd[i][j + 1] = newFwd[i][j + 1].add(k3.mul(back));
                            newBack[i - 1][j - 1] = newBack[i - 1][j - 1].add(k3.mul(back));
                        } else {
                            newFwd[i][j] = newFwd[i][j].add(k4_1.mul(back));
                            newFwd[i][j + 1] = newFwd[i][j + 1].add(k4.mul(back));
                            newBack[i - 1][j - 1] = newBack[i - 1][j - 1].add(k4.mul(back));
                            newBack[i - 1][j] = newBack[i - 1][j].add(k4.mul(back));
                        }
                    } else {
                        newBack[i][j + 1] = newBack[i][j + 1].add(k4.mul(fwd)); // goes to [i][j+1]
                        newFwd[i + 1][j + 1] = newFwd[i + 1][j + 1].add(k4.mul(fwd)); // goes to [i+1][j+1]
                        newFwd[i + 1][j + 2] = newFwd[i + 1][j + 2].add(k4.mul(fwd)); // goes to [i+1][j+2]
                        newBack[i][j] = newBack[i][j].add(k4_1.mul(fwd)); // goes back to [i][j]

                        if (j == 1) {
                            newFwd[i][j] = newFwd[i][j].add(k3_1.mul(back));
                            newFwd[i][j - 1] = newFwd[i][j - 1].add(k3.mul(back));
                            newBack[i - 1][j - 1] = newBack[i - 1][j - 1].add(k3.mul(back));
                        } else {
                            newFwd[i][j] = newFwd[i][j].add(k4_1.mul(back));
                            newFwd[i][j - 1] = newFwd[i][j - 1].add(k4.mul(back));
                            newBack[i - 1][j - 2] = newBack[i - 1][j - 2].add(k4.mul(back));
                            newBack[i - 1][j - 1] = newBack[i - 1][j - 1].add(k4.mul(back));
                        }
                    }
                }
            }
        }
        max++;
        {
            Rational[][] tempFwd = newFwd;
            newFwd = ampFwd;
            ampFwd = tempFwd;
            Rational[][] tempBack = newBack;
            newBack = ampBack;
            ampBack = tempBack;
        }
    }

    double getTotalEnergy() {
        double sum = 0;
        for (int i = 0; i < max; i++) {
            int height = ampFwd[i].length;
            for (int j = 0; j < height; j++) {
                double fwd = ampFwd[i][j].toDouble();
                double back = ampBack[i][j].toDouble();
                sum += fwd * fwd;
                sum += back * back;
            }
        }
        return sum;
    }

    void print() {
        for (int i = 0; i < max; i++) {
            int height = ampFwd[i].length;
            System.out.print(i + 1);
            for (int j = 0; j < height; j++) {
                Rational fwd = ampFwd[i][j];
                Rational back = ampBack[i][j];
                Rational e = fwd.mul(fwd).add(back.mul(back));
                System.out.print(" " + e);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        RTree tree = new RTree();
        while (!tree.isEnd()) {
            tree.step();
            System.out.println("Step " + tree.max + ": " + tree.getTotalEnergy());
            tree.print();
            System.out.println();
        }
    }
}
