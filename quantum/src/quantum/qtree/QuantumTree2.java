package quantum.qtree;

public final class QuantumTree2 {

    public static int parseArg(String[] args, int n, int defValue) {
        if (args.length <= n) {
            return defValue;
        } else {
            return Integer.parseInt(args[n]);
        }
    }

    public static void main(String[] args) {
        int n = parseArg(args, 0, 100);
        int val = parseArg(args, 1, 2);
        for (int i = 0; i < n; i++) {
            System.out.printf("\t%10d", i);
        }
        System.out.println();
        Calculation calculation = new Calculation(n, val);
        for (int len = 2; len <= n; len++) {
            double[] energy = calculation.oneStep();
            for (double e : energy) {
                System.out.printf("\t%10.6f", e);
            }
            System.out.println();
        }
    }
}
