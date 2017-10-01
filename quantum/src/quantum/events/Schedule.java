package quantum.events;

import common.draw.NodeState;
import common.draw.RunMode;
import common.events.BaseSchedule;
import common.events.EventData;
import common.events.ScheduleUtil;
import common.events.StatResult;
import common.graph.Graph;
import common.math.Arithmetic;

import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class Schedule extends BaseSchedule<SmallEntry> {

    private final double ampTol;
    private final RunMode runMode;
    private Number currentTime;
    private int maxCount;
    private Number maxTime;
    private Number addDelay;
    private Number epsilon;

    public Schedule(Graph g, double ampTol, RunMode runMode, Arithmetic a) {
        super(g, a, new EventData<SmallEntry>() {

            public boolean isStat(SmallEntry event) {
                return true;
            }

            public int getEdge(SmallEntry event) {
                return event.edge;
            }

            public double getAmp(SmallEntry event) {
                return event.amp;
            }

            public boolean isForward(SmallEntry event) {
                return event.forward;
            }
        });
        this.ampTol = ampTol;
        this.runMode = runMode;
        this.currentTime = a.zero();
        this.maxTime = a.zero();
        this.addDelay = a.zero();
        this.epsilon = a.zero();
    }

    public void firstPhotons() {
        synchronized (lock) {
            double amp = 1;
            for (int i = 0; i < g.getEdgeNum(); i++) {
                Boolean startForward = g.isStartForward(i);
                if (startForward != null) {
                    double len = g.getEdgeLength(i).doubleValue();
                    double half = len / 2.0;
                    Number t0 = a.evaluate(half);
                    schedule(t0, amp, i, startForward.booleanValue());
                    double eps = len - half;
                    addDelay = a.evaluate(-eps);
                    epsilon = a.evaluate(eps);
                }
            }
        }
    }

    protected SmallEntry newEvent(double amp, int edge, boolean forward) {
        return new SmallEntry(amp, edge, forward);
    }

    private static int index(boolean forward) {
        return forward ? 0 : 1;
    }

    public boolean next() {
        synchronized (lock) {
            Number first = list.firstKey();
            List<SmallEntry> e = list.remove(first);
            double[][][] amplitudes = new double[g.getVertexNum()][g.getEdgeNum()][2];
            boolean[][][] present = new boolean[g.getVertexNum()][g.getEdgeNum()][2];
            boolean[][] dirs = new boolean[g.getVertexNum()][g.getEdgeNum()];
            currentTime = first;
            if (runMode == RunMode.NO_BACK_REALLY) {
                // todo: учет петель
                boolean[][] incoming = new boolean[g.getVertexNum()][g.getEdgeNum()];
                for (SmallEntry entry : e) {
                    int vertex = g.getEdgeSide(entry.edge, entry.forward);
                    incoming[vertex][entry.edge] = true;
                }
                for (SmallEntry entry : e) {
                    int vertex = g.getEdgeSide(entry.edge, entry.forward);
                    int[] out = g.goingOut(vertex);
                    if (out.length == 1) {
                        int edge = out[0];
                        int direction = index(g.direction(vertex, edge));
                        present[vertex][edge][direction] = true;
                        amplitudes[vertex][edge][direction] -= entry.amp;
                    } else {
                        int outgoing = 0;
                        for (int edge : out) {
                            if (!incoming[vertex][edge]) {
                                outgoing++;
                            }
                        }
                        if (outgoing > 0) {
                            double k = 1.0 / outgoing;
                            double forward = k * entry.amp;
                            for (int edge : out) {
                                if (!incoming[vertex][edge]) {
                                    boolean outDir = g.direction(vertex, edge);
                                    int direction = index(outDir);
                                    present[vertex][edge][direction] = true;
                                    amplitudes[vertex][edge][direction] += forward;
                                }
                            }
                        }
                    }
                }
            } else {
                for (SmallEntry entry : e) {
                    int vertex = g.getEdgeSide(entry.edge, entry.forward);
                    int[] out = g.goingOut(vertex);
                    if (out.length == 1) {
                        int edge = out[0];
                        int direction = index(g.direction(vertex, edge));
                        present[vertex][edge][direction] = true;
                        amplitudes[vertex][edge][direction] -= entry.amp;
                    } else {
                        double k = runMode == RunMode.NORMAL ? 2.0 / out.length : 1.0 / (out.length - 1);
                        double forward = k * entry.amp;
                        for (int edge : out) {
                            boolean outDir;
                            if (g.isLoop(edge)) {
                                outDir = dirs[vertex][edge];
                                dirs[vertex][edge] = !outDir;
                            } else {
                                outDir = g.direction(vertex, edge);
                            }
                            boolean back = edge == entry.edge && outDir != entry.forward;
                            double add;
                            if (runMode == RunMode.NORMAL) {
                                if (back) {
                                    add = (k - 1) * entry.amp;
                                } else {
                                    add = forward;
                                }
                            } else {
                                if (back) {
                                    continue;
                                } else {
                                    add = forward;
                                }
                            }
                            int direction = index(outDir);
                            present[vertex][edge][direction] = true;
                            amplitudes[vertex][edge][direction] += add;
                        }
                    }
                }
            }
            for (int vertex = 0; vertex < g.getVertexNum(); vertex++) {
                for (int edge = 0; edge < g.getEdgeNum(); edge++) {
                    for (int dir = 0; dir < 2; dir++) {
                        if (!present[vertex][edge][dir])
                            continue;
                        double amp = amplitudes[vertex][edge][dir];
                        if (Math.abs(amp) <= ampTol)
                            continue;
                        Number len = g.getEdgeLength(edge);
                        Number nextTime = a.add(currentTime, len);
                        schedule(nextTime, amp, edge, dir == 0);
                    }
                }
            }
            int count = 0;
            for (List<SmallEntry> entries : list.values()) {
                count += entries.size();
            }
            if (count > maxCount) {
                maxCount = count;
                maxTime = currentTime;
            }
            return true;
        }
    }

    public double getCurrentTime() {
        synchronized (lock) {
            return a.add(currentTime, addDelay).doubleValue();
        }
    }

    public StatResult getStat() {
        synchronized (lock) {
            return ScheduleUtil.getStat(g, list, a.add(currentTime, addDelay), a.add(maxTime, addDelay), epsilon, extractor);
        }
    }

    public void dumpPhotons(PrintWriter pw) {
        for (Map.Entry<Number, List<SmallEntry>> entries : list.entrySet()) {
            pw.printf(Locale.US, "Time = %.16f%n", entries.getKey().doubleValue());
            boolean was = false;
            for (SmallEntry entry : entries.getValue()) {
                if (!was) {
                    was = true;
                    pw.printf("%d ", g.getEdgeSide(entry.edge, entry.forward));
                }
                pw.printf(Locale.US, " %d(%.16e)", entry.edge, entry.amp);
            }
            pw.println();
        }
    }

    protected Map<Integer, ? extends NodeState> getNodeState(double time) {
        return null;
    }
}
