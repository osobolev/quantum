package common.draw;

public final class PanelOptions {

    final boolean directed;
    final boolean poly;
    final boolean weighted;

    public PanelOptions(boolean directed, boolean poly, boolean weighted) {
        this.directed = directed;
        this.poly = poly;
        this.weighted = weighted;
    }
}
