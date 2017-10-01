package common.events;

import common.draw.RunMode;
import common.graph.Graph;
import common.math.Arithmetic;

public interface ScheduleFactory {

    ISchedule newSchedule(Graph g, double ampTol, RunMode runMode, Arithmetic a) throws InitException;
}
