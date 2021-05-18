package common.draw;

import common.events.StatResult;

import java.text.DecimalFormat;
import java.util.Iterator;

abstract class StatMode {

    private static final DecimalFormat df8 = new DecimalFormat("#.########");
    private static final DecimalFormat df4 = new DecimalFormat("#.####");
    private static final DecimalFormat df2 = new DecimalFormat("#.##");

    private abstract static class EdgeStatMode extends StatMode {

        protected EdgeStatMode(String text) {
            super(text);
        }

        public final String getEdgeValue(StatResult stat, int edge) {
            DecimalFormat format = getFormat();
            double value = getNumberValue(stat, edge);
            return format == null ? String.valueOf(value) : format.format(value);
        }

        public final String getNodeValue(StatResult stat, int node) {
            return null;
        }

        public final Iterator<String> getValues(StatResult stat) {
            return new IntIterator(stat.g.getEdgeNum()) {
                protected String getString(int i) {
                    return getEdgeValue(stat, i);
                }
            };
        }

        protected abstract DecimalFormat getFormat();

        protected abstract double getNumberValue(StatResult stat, int edge);
    }

    private abstract static class NodeStatMode extends StatMode {

        protected NodeStatMode(String text) {
            super(text);
        }

        public final String getEdgeValue(StatResult stat, int edge) {
            return null;
        }

        public final Iterator<String> getValues(StatResult stat) {
            return new IntIterator(stat.g.getVertexNum()) {
                protected String getString(int i) {
                    return getNodeValue(stat, i);
                }
            };
        }
    }

    static final StatMode ENERGY = new EdgeStatMode("Sum Energy") {

        protected DecimalFormat getFormat() {
            return df4;
        }

        protected double getNumberValue(StatResult stat, int edge) {
            return stat.edgeEnergy[edge];
        }
    };
    static final StatMode ENERGY_BY_LEN = new EdgeStatMode("Energy/Length") {

        protected DecimalFormat getFormat() {
            return df4;
        }

        protected double getNumberValue(StatResult stat, int edge) {
            return stat.edgeEnergy[edge] / stat.g.getEdgeLength(edge).doubleValue();
        }
    };
    static final StatMode NUMBER = new EdgeStatMode("Num Packets") {

        protected DecimalFormat getFormat() {
            return null;
        }

        protected double getNumberValue(StatResult stat, int edge) {
            return stat.edgeNum[edge];
        }
    };
    static final StatMode NUM_BY_LEN = new EdgeStatMode("Packets/Length") {

        protected DecimalFormat getFormat() {
            return df2;
        }

        protected double getNumberValue(StatResult stat, int edge) {
            return stat.edgeNum[edge] / stat.g.getEdgeLength(edge).doubleValue();
        }
    };
    static final StatMode NUM_BY_LEN_BY_NUM = new EdgeStatMode("Packets/Length/Total") {

        protected DecimalFormat getFormat() {
            return df8;
        }

        protected double getNumberValue(StatResult stat, int edge) {
            return stat.edgeNum[edge] / stat.g.getEdgeLength(edge).doubleValue() / stat.numPhotons;
        }
    };
    static final StatMode NODE = new NodeStatMode("Packets/DT") {
        public String getNodeValue(StatResult stat, int node) {
            return df2.format(stat.nodeValue[node]);
        }
    };

    private final String text;

    private StatMode(String text) {
        this.text = text;
    }

    public final String toString() {
        return text;
    }

    public abstract String getEdgeValue(StatResult stat, int edge);

    public abstract String getNodeValue(StatResult stat, int node);

    public Iterator<String> getValues(StatResult stat) {
        return new IntIterator(stat.g.getEdgeNum()) {
            protected String getString(int i) {
                return getEdgeValue(stat, i);
            }
        };
    }
}
