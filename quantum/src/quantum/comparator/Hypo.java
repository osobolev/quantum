package quantum.comparator;

import common.math.PolyUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

public final class Hypo {

    private static int getNumber(String str) {
        int i = 0;
        while (i < str.length()) {
            char ch = str.charAt(i);
            if (ch >= '0' && ch <= '9') {
                int p = str.lastIndexOf('.');
                String num;
                if (p < 0) {
                    num = str.substring(i);
                } else {
                    num = str.substring(i, p);
                }
                return Integer.parseInt(num);
            }
            i++;
        }
        return 0;
    }

    private static final class FileTable {

        final int divisor;
        final TableReader data;

        private FileTable(File file) throws IOException {
            divisor = getNumber(file.getName());
            data = TableReader.read(file);
        }
    }

    private static FileTable[] read(File dir) throws IOException {
        File[] files = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().startsWith("out");
            }
        });
        FileTable[] tables = new FileTable[files.length];
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            tables[i] = new FileTable(file);
        }
        Arrays.sort(tables, new Comparator<FileTable>() {
            public int compare(FileTable f1, FileTable f2) {
                return f1.divisor - f2.divisor;
            }
        });
        return tables;
    }

    public static void main(String[] args) throws IOException {
        String hpath = args.length < 1 ? "C:\\work\\projects\\quantum\\cpp\\h" : args[0];
        String vpath = args.length < 2 ? "C:\\work\\projects\\quantum\\cpp\\v" : args[1];
        FileTable[] h = read(new File(hpath));
        FileTable[] v = read(new File(vpath));
        for (int i = 0; i < v.length; i++) {
            TableReader tv = v[i].data;
            TableReader th = h[i].data;
            int deg = 5;
            double[] cv = PolyUtil.leastSquares(tv.times, tv.columns.get(0), deg);
            double[] ch = PolyUtil.leastSquares(th.times, th.columns.get(0), deg);
            System.out.println((v[i].divisor + h[i].divisor) / 2);
            double lenv = Math.sqrt(2) / v[i].divisor;
            double lenh = Math.sqrt(2) / h[i].divisor;
            double diff = cv[3] - ch[3];
            System.out.println((cv[4] - ch[4]) + " " + diff + " " + cv[3]);
//            for (int j = 0; j < Math.min(tv.times.size(), th.times.size()); j++) {
//                double nv = tv.columns.get(0).get(j).doubleValue() / Math.pow(tv.times.get(j).doubleValue(), 3) * lenv;
//                double nh = th.columns.get(0).get(j).doubleValue() / Math.pow(th.times.get(j).doubleValue(), 3) * lenh;
//                double diff = nv - nh;
//                System.out.println("\t" + diff);
//            }
        }
    }
}
