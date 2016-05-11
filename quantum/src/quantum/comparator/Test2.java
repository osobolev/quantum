package quantum.comparator;

import common.math.PolyUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

public class Test2 {

    public static void main(String[] args) throws IOException {
        double t1 = Math.sqrt(11);
        double t2 = Math.sqrt(3);
        double t3 = Math.sqrt(2);
        double t4 = Math.sqrt(5);
        double t5 = Math.sqrt(7);
        double ed = 1.0 / 96.0 * (t1 - t4) / (t1 * t2 * t4 * t5);
        System.out.println(ed);
        System.out.println();
//        double c1 = output("C:\\work\\projects\\quantum\\cpp\\out1.txt");
//        double c2 = output("C:\\work\\projects\\quantum\\cpp\\out2.txt");
//        System.out.println(c2 - c1);
//        System.out.println(ed / (c2 - c1));
//        System.exit(1);
        File[] files = new File("C:\\work\\projects\\quantum\\cpp\\cmp").listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().startsWith("out_");
            }
        });
        double[] k = new double[files.length];
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            TableReader tr = TableReader.read(file);
            double[] coeff = PolyUtil.leastSquares(tr.times, tr.columns.get(0), 5);
            k[i] = coeff[3];
        }
        for (int i = 0; i < k.length; i++) {
            for (int j = 0; j < i; j++) {
                double diff = k[i] - k[j];
                System.out.println(files[i].getName() + " " + files[j].getName() + " " + diff);
            }
        }
    }

    private static double output(String file) throws IOException {
        TableReader tr = TableReader.read(file);
        double[] coeff = PolyUtil.leastSquares(tr.times, tr.columns.get(0), 5);
        System.out.println(coeff[4] + " " + coeff[3]);
//        for (int i = coeff.length - 1; i >= 0; i--) {
//            System.out.println(coeff[i]);
//        }
        return coeff[3];
    }
}
