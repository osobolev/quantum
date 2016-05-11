package neuron.events;

import common.draw.NodeState;

import java.awt.*;

final class DisplayNodeState implements NodeState {

    boolean pregnant;
    int count;
    double toRelease;
    boolean waiting;

    public void paint(Graphics g, FontMetrics fm, int x, int y) {
        StringBuilder buf = new StringBuilder();
        if (count > 0) {
            buf.append(count);
        }
        if (waiting) {
            int dots = (int) Math.round(10 * toRelease);
            if (dots > 0) {
                dots(dots, buf);
            }
        }
        String str = buf.toString();
        g.setColor(pregnant ? Color.red : Color.black);
        g.drawString(str, x - fm.stringWidth(str) / 2, y - fm.getHeight() / 2 + fm.getAscent());
    }

    private static void dots(int n, StringBuilder buf) {
        for (int i = 0; i < n; i++) {
            buf.append('.');
        }
    }
}
