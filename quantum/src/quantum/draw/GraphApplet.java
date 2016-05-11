package quantum.draw;

import javax.swing.*;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public final class GraphApplet extends JApplet {

    private GraphFrame frame = null;

    public void init() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Readable source = null;
                try {
                    URL url = getCodeBase().toURI().resolve("sample.graph").toURL();
                    URLConnection conn = url.openConnection();
                    source = new InputStreamReader(conn.getInputStream(), "Cp1251");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                frame = new GraphFrame(null, source);
            }
        });
    }

    public void destroy() {
        if (frame != null) {
            frame.dispose();
        }
    }
}
