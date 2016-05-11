package quantum.comparator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;
import java.util.Scanner;

public final class GraphReader {

    final double[] edges;

    private GraphReader(double[] edges) {
        this.edges = edges;
    }

    static GraphReader read(String fileName) throws FileNotFoundException {
        return read(new File(fileName));
    }

    static GraphReader read(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        scanner.useLocale(Locale.US);
        scanner.nextInt();
        int edgeNum = scanner.nextInt();
        scanner.nextInt();
        double[] edges = new double[edgeNum];
        for (int i = 0; i < edgeNum; i++) {
            double len = scanner.nextDouble();
            edges[i] = len;
        }
        scanner.close();
        return new GraphReader(edges);
    }
}
