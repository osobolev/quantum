package common.events;

import common.graph.Graph;

public final class StatResult {

    public final Number currentTime;
    public final int numPhotons;
    public final Number maxTime;
    public final double totalEnergy;
    public final double[] edgeEnergy;
    public final int[] edgeNum;
    public final double[] nodeValue;
    public final Graph g;

    public StatResult(Number currentTime, int numPhotons, Number maxTime, double totalEnergy,
                      double[] edgeEnergy, int[] edgeNum, double[] nodeValue,
                      Graph g) {
        this.currentTime = currentTime;
        this.numPhotons = numPhotons;
        this.maxTime = maxTime;
        this.totalEnergy = totalEnergy;
        this.edgeEnergy = edgeEnergy;
        this.edgeNum = edgeNum;
        this.nodeValue = nodeValue;
        this.g = g;
    }
}
