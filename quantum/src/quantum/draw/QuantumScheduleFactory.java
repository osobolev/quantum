package quantum.draw;

import common.events.ISchedule;
import common.events.ScheduleFactory;
import common.graph.Graph;
import common.math.Arithmetic;
import quantum.events.Schedule;

final class QuantumScheduleFactory implements ScheduleFactory {

    public ISchedule newSchedule(Graph g, double ampTol, Arithmetic a) {
        return new Schedule(g, ampTol, a);
    }
}
