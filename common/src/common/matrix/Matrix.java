package common.matrix;

public final class Matrix {

    /**
     * Array for internal storage of elements.
     */
    private final double[][] a;

    /**
     * Row and column dimensions.
     */
    private final int m;
    private final int n;

    /**
     * Construct an m-by-n matrix of zeros.
     *
     * @param m Number of rows.
     * @param n Number of colums.
     */
    private Matrix(int m, int n) {
        this.m = m;
        this.n = n;
        a = new double[m][n];
    }

    /**
     * Construct a matrix from a 2-D array.
     *
     * @param a Two-dimensional array of doubles.
     * @throws IllegalArgumentException All rows must have the same length
     */
    public Matrix(double[][] a) {
        m = a.length;
        n = a[0].length;
        for (int i = 0; i < m; i++) {
            if (a[i].length != n) {
                throw new IllegalArgumentException("All rows must have the same length.");
            }
        }
        this.a = a;
    }

    /**
     * Construct a matrix quickly without checking arguments.
     *
     * @param a Two-dimensional array of doubles.
     * @param m Number of rows.
     * @param n Number of colums.
     */
    public Matrix(double[][] a, int m, int n) {
        this.a = a;
        this.m = m;
        this.n = n;
    }

    /**
     * Access the internal two-dimensional array.
     *
     * @return Pointer to the two-dimensional array of matrix elements.
     */
    public double[][] getArray() {
        return a;
    }

    /**
     * Copy the internal two-dimensional array.
     *
     * @return Two-dimensional array copy of matrix elements.
     */
    public double[][] getArrayCopy() {
        double[][] c = new double[m][n];
        for (int i = 0; i < m; i++) {
            System.arraycopy(a[i], 0, c[i], 0, n);
        }
        return c;
    }

    /**
     * Get row dimension.
     *
     * @return m, the number of rows.
     */
    public int getRowDimension() {
        return m;
    }

    /**
     * Get column dimension.
     *
     * @return n, the number of columns.
     */
    public int getColumnDimension() {
        return n;
    }

    /**
     * Get a submatrix.
     *
     * @param i0 Initial row index
     * @param i1 Final row index
     * @param j0 Initial column index
     * @param j1 Final column index
     * @return A(i0:i1,j0:j1)
     * @throws ArrayIndexOutOfBoundsException Submatrix indices
     */
    public Matrix getMatrix(int i0, int i1, int j0, int j1) {
        Matrix x = new Matrix(i1 - i0 + 1, j1 - j0 + 1);
        double[][] b = x.getArray();
        for (int i = i0; i <= i1; i++) {
            System.arraycopy(a[i], j0, b[i - i0], 0, j1 + 1 - j0);
        }
        return x;
    }

    /**
     * Get a submatrix.
     *
     * @param r  Array of row indices.
     * @param j0 Initial column index
     * @param j1 Final column index
     * @return A(r(:),j0:j1)
     * @throws ArrayIndexOutOfBoundsException Submatrix indices
     */
    public Matrix getMatrix(int[] r, int j0, int j1) {
        Matrix x = new Matrix(r.length, j1 - j0 + 1);
        double[][] b = x.getArray();
        for (int i = 0; i < r.length; i++) {
            System.arraycopy(a[r[i]], j0, b[i], 0, j1 + 1 - j0);
        }
        return x;
    }

    /**
     * Linear algebraic matrix multiplication, A * B
     *
     * @param b another matrix
     * @return Matrix product, A * B
     * @throws IllegalArgumentException Matrix inner dimensions must agree.
     */
    public Matrix times(Matrix b) {
        if (b.m != n) {
            throw new IllegalArgumentException("Matrix inner dimensions must agree.");
        }
        Matrix x = new Matrix(m, b.n);
        double[][] c = x.getArray();
        double[] bColj = new double[n];
        for (int j = 0; j < b.n; j++) {
            for (int k = 0; k < n; k++) {
                bColj[k] = b.a[k][j];
            }
            for (int i = 0; i < m; i++) {
                double[] aRowi = a[i];
                double s = 0;
                for (int k = 0; k < n; k++) {
                    s += aRowi[k] * bColj[k];
                }
                c[i][j] = s;
            }
        }
        return x;
    }

    /**
     * Matrix transpose.
     *
     * @return A'
     */
    public Matrix transpose() {
        Matrix x = new Matrix(n, m);
        double[][] c = x.getArray();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                c[j][i] = a[i][j];
            }
        }
        return x;
    }

    /**
     * Solve A*X = B
     *
     * @param b right hand side
     * @return solution if A is square, least squares solution otherwise
     */
    public Matrix solve(Matrix b) {
        return m == n ? new LUDecomposition(this).solve(b) : new QRDecomposition(this).solve(b);
    }

    /**
     * Matrix inverse or pseudoinverse
     *
     * @return inverse(A) if A is square, pseudoinverse otherwise.
     */
    public Matrix inverse() {
        return solve(identity(m, m));
    }

    /**
     * Generate identity matrix
     *
     * @param m Number of rows.
     * @param n Number of colums.
     * @return An m-by-n matrix with ones on the diagonal and zeros elsewhere.
     */
    private static Matrix identity(int m, int n) {
        Matrix a = new Matrix(m, n);
        double[][] x = a.getArray();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                x[i][j] = i == j ? 1.0 : 0.0;
            }
        }
        return a;
    }

    public void print(int width) {
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                String s = String.valueOf(a[i][j]);
                int padding = Math.max(1, width - s.length());
                for (int k = 0; k < padding; k++)
                    System.out.print(' ');
                System.out.print(s);
            }
            System.out.println();
        }
        System.out.println();
    }
}
