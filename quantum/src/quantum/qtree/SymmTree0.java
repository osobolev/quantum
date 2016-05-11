package quantum.qtree;

final class SymmTree0 extends ICalculation {

    private final SymmTree tree;
    private double[] ampForw0;
    private double[] ampBack0;
    private double[] newAmpForw0;
    private double[] newAmpBack0;
    private final int len0;

    SymmTree0(int treeLength, int val, int len0) {
        tree = new SymmTree(treeLength, val);
        ampForw0 = new double[len0];
        ampBack0 = new double[len0];
        newAmpForw0 = new double[len0];
        newAmpBack0 = new double[len0];
        this.len0 = len0;
        ampForw0[0] = 1;
    }

    public double[] oneStep() {
        Double treeOut = tree.prepareStep();
        if (treeOut == null)
            return null;
        newAmpBack0[len0 - 1] = treeOut.doubleValue();
        System.arraycopy(ampBack0, 1, newAmpBack0, 0, len0 - 1);
        newAmpForw0[0] = -ampBack0[0];
        System.arraycopy(ampForw0, 0, newAmpForw0, 1, len0 - 1);
        tree.completeStep(ampForw0[len0 - 1] - tree.k13 * treeOut.doubleValue());
        {
            double[] tmp = ampBack0;
            ampBack0 = newAmpBack0;
            newAmpBack0 = tmp;
        }
        {
            double[] tmp = ampForw0;
            ampForw0 = newAmpForw0;
            newAmpForw0 = tmp;
        }
        double[] treeStat = tree.getStat();
        double[] result = new double[len0 + treeStat.length];
        System.arraycopy(treeStat, 0, result, len0, treeStat.length);
        for (int i = 0; i < len0; i++) {
            result[i] = ampForw0[i] * ampForw0[i] + ampBack0[i] * ampBack0[i];
        }
        return result;
    }

    public int getCount() {
        return len0 + tree.len;
    }

    public double getSpeed() {
        return 0;
    }
}
