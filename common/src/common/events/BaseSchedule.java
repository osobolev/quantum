package common.events;

import common.draw.NodeState;
import common.graph.Graph;
import common.math.Arithmetic;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public abstract class BaseSchedule<E> extends ISchedule {

    protected final Graph g;
    protected final Arithmetic a;
    protected final TreeMap<Number, List<E>> list = new TreeMap<>();

    protected final Object lock = new Object();
    protected final EventData<E> extractor;

    protected BaseSchedule(Graph g, Arithmetic a, EventData<E> extractor) {
        this.g = g;
        this.a = a;
        this.extractor = extractor;
    }

    protected final void schedule(Number time, double amp, int edge, boolean forward) {
        E newEntry = newEvent(amp, edge, forward);
        ScheduleUtil.schedule(a, list, time, newEntry);
    }

    protected abstract E newEvent(double amp, int edge, boolean forward);

    public final ShowState showPhotons(double time) {
        return ScheduleUtil.showPhotons(g, list, time, this, getNodeState(time), extractor);
    }

    protected abstract Map<Integer, ? extends NodeState> getNodeState(double time);

    public String toString() {
        return ScheduleUtil.toString(list, extractor);
    }
}
