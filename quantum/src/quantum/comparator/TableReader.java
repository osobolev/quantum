package quantum.comparator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public final class TableReader {

    public final List<Double> times;
    public final List<List<Double>> columns;

    private TableReader(List<Double> times, List<List<Double>> columns) {
        this.times = times;
        this.columns = columns;
    }

    public static TableReader read(String fileName) throws IOException {
        return read(new File(fileName));
    }

    public static TableReader read(File file) throws IOException {
        BufferedReader rdr = new BufferedReader(new FileReader(file));
        List<Double> times = new ArrayList<Double>();
        List<List<Double>> columns = new ArrayList<List<Double>>();
        while (true) {
            String line = rdr.readLine();
            if (line == null)
                break;
            int p = line.indexOf('\t');
            double time = Double.parseDouble(line.substring(0, p).trim());
            times.add(time);
            line = line.substring(p + 1);
            StringTokenizer tok = new StringTokenizer(line, "\t");
            int col = 0;
            while (tok.hasMoreTokens()) {
                String t = tok.nextToken();
                if (t.indexOf('.') >= 0)
                    continue;
                while (columns.size() <= col) {
                    columns.add(new ArrayList<Double>());
                }
                columns.get(col).add(new Double(t.trim()));
                col++;
            }
        }
        rdr.close();
        return new TableReader(times, columns);
    }
}
