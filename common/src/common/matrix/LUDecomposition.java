package common.matrix;

final class LUDecomposition {

    /**
     * Array for internal storage of decomposition.
     *
     * @serial internal array storage.
     */
    private final double[][] lu;

    /**
     * Row and column dimensions, and pivot sign.
     */
    private final int m;
    private final int n;

    /**
     * Internal storage of pivot vector.
     */
    private final int[] piv;

    /**
     * LU Decomposition
     *
     * @param a Rectangular matrix
     * @return Structure to access L, U and piv.
     */
    LUDecomposition(Matrix a) {
        // Use a "left-looking", dot-product, Crout/Doolittle algorithm.
        lu = a.getArrayCopy();
        m = a.getRowDimension();
        n = a.getColumnDimension();
        piv = new int[m];
        for (int i = 0; i < m; i++) {
            piv[i] = i;
        }
        int pivsign = 1;
        double[] luColj = new double[m];

        // Outer loop.

        for (int j = 0; j < n; j++) {

            // Make a copy of the j-th column to localize references.

            for (int i = 0; i < m; i++) {
                luColj[i] = lu[i][j];
            }

            // Apply previous transformations.

            for (int i = 0; i < m; i++) {
                double[] luRowi = lu[i];

                // Most of the time is spent in the following dot product.

                int kmax = Math.min(i, j);
                double s = 0.0;
                for (int k = 0; k < kmax; k++) {
                    s += luRowi[k] * luColj[k];
                }

                luRowi[j] = luColj[i] -= s;
            }

            // Find pivot and exchange if necessary.

            int p = j;
            for (int i = j + 1; i < m; i++) {
                if (Math.abs(luColj[i]) > Math.abs(luColj[p])) {
                    p = i;
                }
            }
            if (p != j) {
                for (int k = 0; k < n; k++) {
                    double t = lu[p][k];
                    lu[p][k] = lu[j][k];
                    lu[j][k] = t;
                }
                int k = piv[p];
                piv[p] = piv[j];
                piv[j] = k;
                pivsign = -pivsign;
            }

            // Compute multipliers.

            if (j < m && lu[j][j] != 0.0) {
                for (int i = j + 1; i < m; i++) {
                    lu[i][j] /= lu[j][j];
                }
            }
        }
    }

    /**
     * Is the matrix nonsingular?
     *
     * @return true if U, and hence A, is nonsingular.
     */
    private boolean isNonsingular() {
        for (int j = 0; j < n; j++) {
            if (lu[j][j] == 0)
                return false;
        }
        return true;
    }

    /**
     * Solve A*X = B
     *
     * @param b A Matrix with as many rows as A and any number of columns.
     * @return X so that L*U*X = B(piv,:)
     * @throws IllegalArgumentException Matrix row dimensions must agree.
     * @throws RuntimeException         Matrix is singular.
     */
    Matrix solve(Matrix b) {
        if (b.getRowDimension() != m) {
            throw new IllegalArgumentException("Matrix row dimensions must agree.");
        }
        if (!this.isNonsingular()) {
            throw new RuntimeException("Matrix is singular.");
        }

        // Copy right hand side with pivoting
        int nx = b.getColumnDimension();
        Matrix xmat = b.getMatrix(piv, 0, nx - 1);
        double[][] x = xmat.getArray();

        // Solve L*Y = B(piv,:)
        for (int k = 0; k < n; k++) {
            for (int i = k + 1; i < n; i++) {
                for (int j = 0; j < nx; j++) {
                    x[i][j] -= x[k][j] * lu[i][k];
                }
            }
        }
        // Solve U*X = Y;
        for (int k = n - 1; k >= 0; k--) {
            for (int j = 0; j < nx; j++) {
                x[k][j] /= lu[k][k];
            }
            for (int i = 0; i < k; i++) {
                for (int j = 0; j < nx; j++) {
                    x[i][j] -= x[k][j] * lu[i][k];
                }
            }
      }
      return xmat;
   }
}
