package quantum.qtree;

import quantum.qtree.pascal.PascalCalculation;

enum TreeMode {
    SIMPLE("Symmetric") {
        ICalculation create(int count, int val1, int val2, boolean symmetric, int tail) {
            return new Calculation(count, val1);
        }
    }, SIMPLE0("With tail") {
        ICalculation create(int count, int val1, int val2, boolean symmetric, int tail) {
            return new SymmTree0(count, val1, tail);
        }
    }, SIMPLE2("Two trees") {
        ICalculation create(int count, int val1, int val2, boolean symmetric, int tail) {
            return new SymmTree2(count, val1, val2, symmetric);
        }
    }, GRAPH("Asymmetric") {
        ICalculation create(int count, int val1, int val2, boolean symmetric, int tail) {
            return new GraphTree(val1, 0, count);
        }
    }, PASCAL("Pascal") {
        ICalculation create(int count, int val1, int val2, boolean symmetric, int tail) {
            return new PascalCalculation(count, true);
        }
    };

    private final String name;

    TreeMode(String name) {
        this.name = name;
    }

    abstract ICalculation create(int count, int val1, int val2, boolean symmetric, int tail);

    public String toString() {
        return name;
    }
}
