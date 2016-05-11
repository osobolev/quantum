package quantum.qtree;

public final class SymmTree {

    /*
     * Achtung! Теперь это не амплитуда, а (амплитуда * val^(j/2))
     */
    private double[] ampForw;
    private double[] ampBack;
    private double[] newAmpForw;
    private double[] newAmpBack;

    final int len;
    final double k23;
    final double k13;
    private final double sqr;

    private int i = 0;
    private double speed = 0;

    public SymmTree(int len, int val) {
        ampForw = new double[len];
        ampBack = new double[len];
        newAmpForw = new double[len];
        newAmpBack = new double[len];
        this.len = len;

        k23 = 2. / (val + 1);
        k13 = k23 - 1;
        sqr = Math.sqrt(val);
    }

    public void init(double amp) {
        ampForw[0] = amp; // * val ^ (0/2)
    }

    /**
     * @return амплитуда, вытекающая из дерева
     */
    public Double prepareStep() {
        if (i >= len - 1)
            return null;

        newAmpBack[0] = ampBack[1] * k23 * sqr + ampForw[0] * k13;
        newAmpForw[0] = ampBack[0] * k13;
        int num = i + 2;
        for (int j = 1; j < num; j++) {
            newAmpForw[j] = ampForw[j - 1] * k23 * sqr - ampBack[j] * k13;

            double a23 = ampForw[j] * k13;
            if (j + 1 < len) {
                newAmpBack[j] = ampBack[j + 1] * k23 * sqr + a23;
            } else {
                newAmpBack[j] = a23;
            }
        }

        return ampBack[0];
    }

    /**
     * @param addAmp амплитуда, втекающая в дерево
     */
    public void completeStep(double addAmp) {
        newAmpForw[0] += addAmp;
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
        i++;
    }

    public double[] getStat() {
        int num = i + 1;
        double[] energyLevels = new double[num];
        double max = 0;
        int maxj = -1;
        for (int j = 0; j < num; j++) {
            double af = ampForw[j];
            double ab = ampBack[j];
            if (af * ab != 0) {
                System.out.println(af + " <-> " + ab);
            }
            double energy = af * af + ab * ab;
            energyLevels[j] = energy;
            if (j > 10 && energy > max) {
                max = energy;
                maxj = j;
            }
        }

        if (maxj >= 0) {
            speed = (double) maxj / (num - 1);
        }
        return energyLevels;
    }

    public double getSpeed() {
        return speed;
    }
}
