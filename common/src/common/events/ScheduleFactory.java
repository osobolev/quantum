package common.events;

import common.graph.Graph;
import common.math.Arithmetic;

public interface ScheduleFactory {

    ISchedule newSchedule(Graph g, double ampTol, Arithmetic a) throws InitException;
}
