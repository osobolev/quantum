package quantum.qtree;

final class SymmTree2 extends ICalculation {

    private final SymmTree tree1;
    private final SymmTree tree2;

    SymmTree2(int treeLength, int val1, int val2, boolean symmetric) {
        tree1 = new SymmTree(treeLength, val1);
        tree2 = new SymmTree(treeLength, val2);
        if (symmetric) {
            double a = Math.sqrt(2) / 2;
            tree1.init(a);
            tree2.init(a);
        } else {
            tree1.init(1);
        }
    }

    public double[] oneStep() {
        Double treeOut1 = tree1.prepareStep();
        if (treeOut1 == null)
            return null;
        Double treeOut2 = tree2.prepareStep();
        if (treeOut2 == null)
            return null;
        tree1.completeStep(treeOut2.doubleValue() - tree1.k13 * treeOut1.doubleValue());
        tree2.completeStep(treeOut1.doubleValue() - tree2.k13 * treeOut2.doubleValue());
        double[] treeStat1 = tree1.getStat();
        double[] treeStat2 = tree2.getStat();
        double[] result = new double[tree1.len + treeStat2.length];
        for (int i = 0; i < treeStat1.length; i++) {
            result[tree1.len - i - 1] = treeStat1[i];
        }
        System.arraycopy(treeStat2, 0, result, tree1.len, treeStat2.length);
        return result;
    }

    public int getCount() {
        return tree1.len + tree2.len;
    }

    public int getCenter() {
        return tree1.len;
    }

    public double getSpeed() {
        return 0;
    }
}
