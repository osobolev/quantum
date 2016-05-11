package quantum.qtree.pascal;

import quantum.qtree.ICalculation;

public final class PascalCalculation extends ICalculation {

    private final int len;
    private final Tree tree;

    public PascalCalculation(int len, boolean tail) {
        this.len = len;
        tree = new Tree(len, tail);
    }

    public double[] oneStep() {
        if (tree.isEnd())
            return null;
        tree.step();
        return tree.getEnergy();
    }

    public int getCount() {
        return len;
    }

    public double getSpeed() {
        return tree.getSpeed();
    }
}
