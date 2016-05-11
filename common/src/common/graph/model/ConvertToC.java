package common.graph.model;

import common.graph.Graph;
import common.math.Arithmetic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public final class ConvertToC {

    public static void main(String[] args) throws FileNotFoundException {
        for (String arg : args) {
            File in = new File(arg);
            String fileName = in.getName();
            int i = fileName.lastIndexOf('.');
            String baseName = fileName.substring(0, i);
            GraphModel model = new GraphModel();
            model.load(new FileReader(in));
            Graph graph = model.toGraph(Arithmetic.createArithmetic(1e-10, 0));
            graph.toC(new File(in.getParentFile(), baseName + ".txt"));
        }
    }
}
