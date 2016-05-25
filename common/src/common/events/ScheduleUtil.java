package common.events;

import common.draw.NodeState;
import common.graph.Graph;
import common.math.Arithmetic;

import java.util.*;

public final class ScheduleUtil {

    public static <K extends Number, T> void schedule(Arithmetic a, TreeMap<K, List<T>> list, K time, T newEntry) {
        List<T> existing = list.get(time);
        if (existing != null) {
            existing.add(newEntry);
        } else {
            Map.Entry<K, List<T>> upper = list.ceilingEntry(time);
            List<T> addTo = null;
            if (upper != null) {
                if (a.isNear(time, upper.getKey())) {
                    addTo = upper.getValue();
                }
            }
            if (addTo == null) {
                Map.Entry<K, List<T>> lower = list.floorEntry(time);
                if (lower != null) {
                    if (a.isNear(time, lower.getKey())) {
                        addTo = lower.getValue();
                    }
                }
            }
            if (addTo == null) {
                List<T> newList = new ArrayList<T>();
                newList.add(newEntry);
                list.put(time, newList);
            } else {
                addTo.add(newEntry);
            }
        }
    }

    public static <K extends Number, T> StatResult getStat(Graph g, TreeMap<K, List<T>> list,
                                                           K currentTime, K maxTime, K epsilon, EventData<T> extractor) {
        double sum = 0;
        int count = 0;
        int nedges = g.getEdgeNum();
        double[] energy = new double[nedges];
        int[] edgeNum = new int[nedges];
        double[] nodeValue = new double[g.getVertexNum()];
        for (List<T> entries : list.values()) {
            for (T entry : entries) {
                if (!extractor.isStat(entry))
                    continue;
                double amp = extractor.getAmp(entry);
                double e1 = amp * amp;
                int edge = extractor.getEdge(entry);
                energy[edge] += e1;
                edgeNum[edge]++;
                sum += e1;
                count++;
            }
        }
        return new StatResult(currentTime, count, maxTime, epsilon, sum, energy, edgeNum, nodeValue, g);
    }

    public static <K extends Number, T> ShowState showPhotons(Graph g, TreeMap<K, List<T>> list, double time, ISchedule schedule,
                                                              Map<Integer, ? extends NodeState> nodeMap, EventData<T> extractor) {
        while (true) {
            if (list.isEmpty())
                return null;
            Number first = list.firstKey();
            if (first.doubleValue() < time) {
                schedule.next();
            } else {
                break;
            }
        }
        Map<Integer, List<Double>> map = new HashMap<Integer, List<Double>>();
        for (Map.Entry<K, List<T>> entries : list.entrySet()) {
            double arrival = entries.getKey().doubleValue();
            for (T entry : entries.getValue()) {
                if (!extractor.isStat(entry))
                    continue;
                int edge = extractor.getEdge(entry);
                double edgeLength = g.getEdgeLength(edge).doubleValue();
                double started = arrival - edgeLength;
                double position = (time - started) / edgeLength;
                if (!extractor.isForward(entry)) {
                    position = 1 - position;
                }
                List<Double> showList = map.get(edge);
                if (showList == null) {
                    showList = new ArrayList<Double>();
                    map.put(edge, showList);
                }
                showList.add(position);
            }
        }
        return new ShowState(map, nodeMap);
    }

    public static <K extends Number, T> String toString(TreeMap<K, List<T>> list, EventData<T> extractor) {
        StringBuilder buf = new StringBuilder();
        for (Map.Entry<K, List<T>> entries : list.entrySet()) {
            buf.append(entries.getKey() + ": ");
            boolean first = true;
            for (T entry : entries.getValue()) {
                if (first) {
                    first = false;
                } else {
                    buf.append(", ");
                }
                buf.append("A=" + extractor.getAmp(entry) + " on " + extractor.getEdge(entry));
            }
            buf.append('\n');
        }
        return buf.toString();
    }
}
