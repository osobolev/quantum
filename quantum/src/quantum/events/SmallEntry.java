package quantum.events;

final class SmallEntry {

    final double amp;
    final int edge;
    final boolean forward;

    SmallEntry(double amp, int edge, boolean forward) {
        this.amp = amp;
        this.edge = edge;
        this.forward = forward;
    }
}
