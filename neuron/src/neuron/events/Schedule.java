package neuron.events;

import common.draw.NodeState;
import common.events.BaseSchedule;
import common.events.EventData;
import common.events.ScheduleUtil;
import common.events.StatResult;
import common.graph.Graph;
import common.math.Arithmetic;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Schedule extends BaseSchedule<Event> {

    private Number currentTime;
    private final State[] states;
    private final int numImpulses;
    private final Number tauPeriod;
    private final Number tauRestore;
    private final Density[] densities;
    private final JTextField tfDT;

    private int impulseCount = 0;

    public Schedule(Graph g, Arithmetic a, int numImpulses, double tauPeriod, double tauRestore, JTextField tfDT) {
        super(g, a, new EventData<Event>() {

            public boolean isStat(Event event) {
                return event instanceof InEvent;
            }

            public int getEdge(Event event) {
                return ((InEvent) event).edge;
            }

            public double getAmp(Event event) {
                return 1.0;
            }

            public boolean isForward(Event event) {
                return true;
            }
        });
        this.currentTime = a.zero();
        this.numImpulses = numImpulses;
        this.tfDT = tfDT;
        this.tauPeriod = a.evaluate(tauPeriod);
        this.tauRestore = a.evaluate(tauRestore);
        int vertexNum = g.getVertexNum();
        this.states = new State[vertexNum];
        this.densities = new Density[vertexNum];
        for (int i = 0; i < vertexNum; i++) {
            states[i] = new State();
            densities[i] = new Density();
        }
    }

    public void firstPhotons() {
        synchronized (lock) {
            ScheduleUtil.schedule(a, list, currentTime, new InitEvent());
        }
    }

    protected Event newEvent(double amp, int edge, boolean forward) {
        return new InEvent(edge);
    }

    public boolean next() {
        synchronized (lock) {
            if (list.isEmpty())
                return false;
            Number first = list.firstKey();
            List<Event> e = list.remove(first);
            boolean[] shouldEmitNow = new boolean[g.getVertexNum()];
            currentTime = first;
            for (Event event : e) {
                if (event instanceof InEvent) {
                    InEvent ie = (InEvent) event;
                    int vertex = g.getEdgeSide(ie.edge, true);
                    densities[vertex].add(currentTime.doubleValue());
                    State state = states[vertex];
                    state.count++;
                } else if (event instanceof OutEvent) {
                    OutEvent oe = (OutEvent) event;
                    shouldEmitNow[oe.vertex] = true;
                } else if (event instanceof InitEvent) {
                    for (int edge = 0; edge < g.getEdgeNum(); edge++) {
                        Boolean startForward = g.isStartForward(edge);
                        if (startForward != null) {
                            if (impulseCount < numImpulses) {
                                schedule(a.add(currentTime, g.getEdgeLength(edge)), 1.0, edge, true);
                                impulseCount++;
                            }
                            if (impulseCount < numImpulses) {
                                ScheduleUtil.schedule(a, list, a.add(currentTime, tauPeriod), new InitEvent());
                            }
                        }
                    }
                }
            }
            for (int i = 0; i < states.length; i++) {
                State state = states[i];
                boolean shouldEmit = false;
                if (shouldEmitNow[i]) {
                    state.count = 0; // todo
                    shouldEmit = true;
                    // todo: what if count >> 3?
                } else {
                    Integer iweight = g.getVertexWeight(i);
                    int weight = iweight == null ? g.goingOutFwd(i).length : iweight.intValue();
                    if (weight > 0 && state.count >= weight) {
                        state.count = 0; // todo
                        Number afterTau = state.lastEmitted == null ? null : a.add(state.lastEmitted, tauRestore);
                        // lastEmitted + tau < currentTime
                        if (afterTau != null && a.compare(afterTau, currentTime) > 0) {
                            ScheduleUtil.schedule(a, list, afterTau, new OutEvent(i));
                        } else {
                            shouldEmit = true;
                        }
                    }
                }
                if (shouldEmit) {
                    state.lastEmitted = currentTime;
                    int[] out = g.goingOutFwd(i);
                    for (int edge : out) {
                        Number len = g.getEdgeLength(edge);
                        Number nextTime = a.add(currentTime, len);
                        ScheduleUtil.schedule(a, list, nextTime, new InEvent(edge));
                    }
                }
            }
            return true;
        }
    }

    public double getCurrentTime() {
        synchronized (lock) {
            return currentTime.doubleValue();
        }
    }

    public StatResult getStat() {
        synchronized (lock) {
            StatResult stat = ScheduleUtil.getStat(g, list, currentTime, null, null, extractor);
            modifyStat(stat);
            return stat;
        }
    }

    private void modifyStat(StatResult stat) {
        double time = stat.currentTime.doubleValue();
        String strDT = tfDT.getText();
        double period = time;
        if (strDT.length() > 0) {
            try {
                period = Double.parseDouble(strDT);
            } catch (NumberFormatException nfex) {
                // ignore
            }
        }
        for (int vertex = 0; vertex < stat.nodeValue.length; vertex++) {
            stat.nodeValue[vertex] = densities[vertex].getDensity(time, period) / period;
        }
    }

    protected Map<Integer, ? extends NodeState> getNodeState(double time) {
        Map<Integer, DisplayNodeState> map = new HashMap<>();
        double tauPeriod = this.tauPeriod.doubleValue();
        double tauRestore = this.tauRestore.doubleValue();
        for (int i = 0; i < states.length; i++) {
            State state = states[i];
            DisplayNodeState dstate = new DisplayNodeState();
            dstate.count = state.count;
            map.put(i, dstate);
            if (state.lastEmitted != null) {
                double lastEmitted = state.lastEmitted.doubleValue();
                if (lastEmitted + tauRestore > time) {
                    dstate.toRelease = (lastEmitted + tauRestore  - time) / tauRestore; // todo: ???
                    dstate.waiting = true;
                }
            }
        }
        for (Map.Entry<Number, List<Event>> entry : list.entrySet()) {
            double arrival = entry.getKey().doubleValue();
            for (Event e : entry.getValue()) {
                if (e instanceof OutEvent) {
                    OutEvent oe = (OutEvent) e;
                    DisplayNodeState state = map.get(oe.vertex);
                    if (state == null) {
                        state = new DisplayNodeState();
                        map.put(oe.vertex, state);
                        state.waiting = true;
                        double started = arrival - tauPeriod;
                        state.toRelease = 1 - (time - started) / tauPeriod; // todo: ???
                    }
                    state.pregnant = true;
                }
            }
        }
        return map;
    }
}
