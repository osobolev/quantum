package common.matrix;

final class QRDecomposition {

    /**
     * Array for internal storage of decomposition.
     */
    private final double[][] qr;

    /**
     * Row and column dimensions.
     */
    private final int m;
    private final int n;

    /**
     * Array for internal storage of diagonal of R.
     *
     * @serial diagonal of R.
     */
    private final double[] rDiag;

    /**
     * QR Decomposition, computed by Householder reflections.
     *
     * @param a Rectangular matrix
     * @return Structure to access R and the Householder vectors and compute Q.
     */
    QRDecomposition(Matrix a) {
        // Initialize.
        qr = a.getArrayCopy();
        m = a.getRowDimension();
        n = a.getColumnDimension();
        rDiag = new double[n];

        // Main loop.
        for (int k = 0; k < n; k++) {
            // Compute 2-norm of k-th column without under/overflow.
            double nrm = 0;
            for (int i = k; i < m; i++) {
                nrm = Maths.hypot(nrm, qr[i][k]);
            }

            if (nrm != 0.0) {
                // Form k-th Householder vector.
                if (qr[k][k] < 0) {
                    nrm = -nrm;
                }
                for (int i = k; i < m; i++) {
                    qr[i][k] /= nrm;
                }
                qr[k][k] += 1.0;

                // Apply transformation to remaining columns.
                for (int j = k + 1; j < n; j++) {
                    double s = 0.0;
                    for (int i = k; i < m; i++) {
                        s += qr[i][k] * qr[i][j];
                    }
                    s = -s / qr[k][k];
                    for (int i = k; i < m; i++) {
                        qr[i][j] += s * qr[i][k];
                    }
                }
            }
            rDiag[k] = -nrm;
        }
    }

    /**
     * Is the matrix full rank?
     *
     * @return true if R, and hence A, has full rank.
     */
    private boolean isFullRank() {
        for (int j = 0; j < n; j++) {
            if (rDiag[j] == 0)
                return false;
        }
        return true;
    }

    /**
     * Least squares solution of A*X = B
     *
     * @param b A Matrix with as many rows as A and any number of columns.
     * @return X that minimizes the two norm of Q*R*X-B.
     * @throws IllegalArgumentException Matrix row dimensions must agree.
     * @throws RuntimeException         Matrix is rank deficient.
     */
    Matrix solve(Matrix b) {
        if (b.getRowDimension() != m) {
            throw new IllegalArgumentException("Matrix row dimensions must agree.");
        }
        if (!this.isFullRank()) {
            throw new RuntimeException("Matrix is rank deficient.");
        }

        // Copy right hand side
        int nx = b.getColumnDimension();
        double[][] x = b.getArrayCopy();

        // Compute Y = transpose(Q)*B
        for (int k = 0; k < n; k++) {
            for (int j = 0; j < nx; j++) {
                double s = 0.0;
                for (int i = k; i < m; i++) {
                    s += qr[i][k] * x[i][j];
                }
                s = -s / qr[k][k];
                for (int i = k; i < m; i++) {
                    x[i][j] += s * qr[i][k];
                }
            }
        }
        // Solve R*X = Y;
        for (int k = n - 1; k >= 0; k--) {
            for (int j = 0; j < nx; j++) {
                x[k][j] /= rDiag[k];
            }
            for (int i = 0; i < k; i++) {
                for (int j = 0; j < nx; j++) {
                    x[i][j] -= x[k][j] * qr[i][k];
                }
            }
        }
        return new Matrix(x, n, nx).getMatrix(0, n - 1, 0, nx - 1);
    }
}
