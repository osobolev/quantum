package quantum.qtree;

import java.io.File;
import java.io.IOException;

public abstract class ICalculation {

    public abstract double[] oneStep();

    public abstract int getCount();

    public int getCenter() {
        return -1;
    }

    public abstract double getSpeed();

    public boolean print(File file) throws IOException {
        return false;
    }
}
