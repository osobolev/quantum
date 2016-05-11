package quantum.qtree;

import javax.swing.*;

public final class TreeApplet extends JApplet {

    private AnimationFrame frame = null;

    public void init() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                frame = new AnimationFrame(1000, 2);
            }
        });
    }

    public void destroy() {
        if (frame != null) {
            frame.dispose();
        }
    }
}
