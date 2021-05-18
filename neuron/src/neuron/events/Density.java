package neuron.events;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

final class Density {

    private final SortedSet<Double> times = new TreeSet<>();

    void add(double time) {
        times.add(time);
    }

    int getDensity(double time, double period) {
        Double minTime = time - period;
        if (times.contains(time)) {
            SortedSet<Double> after = times.tailSet(time);
            if (after.size() > 1) {
                Iterator<Double> i = after.iterator();
                i.next();
                Double second = i.next();
                return times.subSet(minTime, second).size();
            } else {
                return times.tailSet(minTime).size();
            }
        } else {
            return times.subSet(minTime, time).size();
        }
    }
}
