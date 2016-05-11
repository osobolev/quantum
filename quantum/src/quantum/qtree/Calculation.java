package quantum.qtree;

final class Calculation extends ICalculation {

    private final SymmTree tree;

    Calculation(int len, int val) {
        tree = new SymmTree(len, val);
        tree.init(1);
    }

    public double[] oneStep() {
        Double ampBack0 = tree.prepareStep();
        if (ampBack0 == null)
            return null;
        tree.completeStep(-tree.k23 * ampBack0.doubleValue());
        return tree.getStat();
    }

    public int getCount() {
        return tree.len;
    }

    public double getSpeed() {
        return tree.getSpeed();
    }
}
