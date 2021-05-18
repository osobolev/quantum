package quantum.qtree;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

final class GraphTree extends ICalculation {

    private double[] ampForw;
    private double[] ampBack;
    private double[] newAmpForw;
    private double[] newAmpBack;

    private final SymmTree[] trees;
    private final double[] contact;

    private final int len;
    private final int n;
    private final int treeLength;

    private final double k23 = 2. / 3.;
    private final double k13 = -1. / 3.;

    /**
     * @param initNode от 0 до (2^(len - 1) - 1)
     */
    GraphTree(int len, int initNode, int treeLength) {
        this.len = len;
        this.n = (1 << len) - 1;
        this.treeLength = treeLength;
        ampForw = new double[n];
        ampBack = new double[n];
        newAmpForw = new double[n];
        newAmpBack = new double[n];
        trees = new SymmTree[n + 1];
        contact = new double[trees.length];
        for (int i = 0; i < trees.length; i++) {
            trees[i] = new SymmTree(treeLength, 2);
        }

/*
        int j = (1 << len - 1) - 1 + initNode;
        double a0 = 1. / Math.sqrt(3);
        ampBack[j] = a0;
        int treej = initNode * 2;
        trees[treej].init(a0);
        trees[treej + 1].init(a0);
*/
        int j = (1 << len - 1) - 1 + initNode;
        ampForw[j] = 1;
        //ampBack[j] = 1;
    }

    private boolean oneInternalStep() {
        for (int i = 0; i < trees.length; i++) {
            Double step = trees[i].prepareStep();
            if (step == null)
                return false;
            contact[i] = step.doubleValue();
        }

        int numactive = n;
        double a01 = ampBack[1] * k23;
        double a02 = ampBack[2] * k23;
        double a03 = ampForw[0] * k13;
        newAmpBack[0] = a01 + a02 + a03;
        newAmpForw[0] = -ampBack[0];
        for (int j = 1; j < numactive; j++) {
            int n11 = (j - 1) / 2;
            double a11 = ampForw[n11] * k23;
            int n12 = j % 2 != 0 ? j + 1 : j - 1;
            double a12 = ampBack[n12] * k23;
            double a13 = ampBack[j] * k13;
            newAmpForw[j] = a11 + a12 + a13;

            int n21 = 2 * j + 1;
            double a23 = ampForw[j] * k13;
            int n22 = 2 * j + 2;
            if (n21 < n) {
                double a21 = ampBack[n21] * k23;
                double a22 = ampBack[n22] * k23;
                newAmpBack[j] = a21 + a22 + a23;
            } else {
                newAmpBack[j] = a23 + contact[n21 - n] * k23 + contact[n22 - n] * k23;
            }
        }

        for (int i = 0; i < trees.length; i++) {
            int nfeed = (i + n - 1) / 2;
            int nneibor = i % 2 != 0 ? i - 1 : i + 1;
            double add = ampForw[nfeed] * k23 + contact[nneibor] * k23;
            trees[i].completeStep(add);
        }
        {
            double[] tmp = ampBack;
            ampBack = newAmpBack;
            newAmpBack = tmp;
        }
        {
            double[] tmp = ampForw;
            ampForw = newAmpForw;
            newAmpForw = tmp;
        }

        return true;
    }

    private double[] getStat() {
        double[] energyLevels = new double[len];
        int j = 0;
        for (int i = 0; i < len; i++) {
            int count = 1 << i;
            for (int lvl = 0; lvl < count; lvl++) {
                double af = ampForw[j];
                double ab = ampBack[j];
                j++;
                if (af * ab != 0) {
                    System.out.println(af + " <-> " + ab);
                }
                double energy = af * af + ab * ab;
                energyLevels[i] += energy;
            }
        }
        return energyLevels;
    }

    private double[] getAllStat() {
        double[] myStat = getStat();
        double[] treeSum = null;
        for (SymmTree tree : trees) {
            double[] treeStat = tree.getStat();
            if (treeSum == null) {
                treeSum = new double[treeStat.length];
            }
            for (int j = 0; j < treeStat.length; j++) {
                treeSum[j] += treeStat[j];
            }
        }
        double[] ret;
        if (treeSum != null) {
            ret = new double[myStat.length + treeSum.length];
            System.arraycopy(myStat, 0, ret, 0, myStat.length);
            System.arraycopy(treeSum, 0, ret, myStat.length, treeSum.length);
        } else {
            ret = myStat;
        }

        double sum = 0;
        for (double e : ret) {
            sum += e;
        }
        if (Math.abs(sum - 1) >= 1e-5)
            throw new IllegalStateException("WTF?!!! " + (sum - 1));
        return ret;
    }

    public double[] oneStep() {
        if (!oneInternalStep())
            return null;
        return getAllStat();
    }

    public int getCount() {
        return treeLength;
    }

    public double getSpeed() {
        return 0;
    }

    public boolean print(File file) throws IOException {
        PrintWriter pw = new PrintWriter(file);
        int j = 0;
        for (int i = 0; i < len; i++) {
            int count = 1 << i;
            for (int lvl = 0; lvl < count; lvl++) {
                double af = ampForw[j];
                double ab = ampBack[j];
                j++;
                double energy = af * af + ab * ab;
                if (lvl > 0) {
                    pw.print("\t");
                }
                pw.printf("%10.6f", energy);
            }
            pw.println();
        }
        pw.println();
        double[][] treeStat = new double[trees.length][];
        for (int i = 0; i < trees.length; i++) {
            treeStat[i] = trees[i].getStat();
        }
        for (int i = 0; i < treeStat[0].length; i++) {
            for (int tree = 0; tree < trees.length; tree++) {
                if (tree > 0) {
                    pw.print("\t");
                }
                pw.printf("%10.6f", treeStat[tree][i]);
            }
            pw.println();
        }
        pw.close();

        double[] e = new double[n];
        for (int i = 0; i < n; i++) {
            double af = ampForw[i];
            double ab = ampBack[i];
            e[i] = af * af + ab * ab;
        }
        TreePainter.draw(e, treeStat, len, file);

        return true;
    }

    private void runCalculation() {
        while (true) {
            if (!oneInternalStep())
                break;
            double[] ret = getAllStat();
            for (double e : ret) {
                System.out.printf("\t%.2f", e);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        new GraphTree(2, 0, 100).runCalculation();
    }
}
