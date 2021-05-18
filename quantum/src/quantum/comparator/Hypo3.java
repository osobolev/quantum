package quantum.comparator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class Hypo3 {

    public static void main(String[] args) throws IOException {
        TableReader a = TableReader.read("C:\\work\\projects\\quantum\\cpp\\geq");
        TableReader b = TableReader.read("C:\\work\\projects\\quantum\\cpp\\diff\\err");
        List<Double> nums = new ArrayList<>();
        int count = Math.min(a.times.size(), b.times.size());
        for (int i = 50; i < count; i++) {
            double n1 = a.columns.get(0).get(i).doubleValue();
            double n2 = b.columns.get(0).get(i).doubleValue();
            double d = n2 - n1;
            nums.add(d);
            System.out.println(d);
        }
//        double[] coeff = PolyUtil.leastSquares(b.times.subList(50, a.times.size()), nums, 3);
//        System.out.println(Arrays.toString(coeff));
    }
}
