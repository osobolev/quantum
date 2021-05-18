package common.draw;

import common.events.InitException;
import common.events.ScheduleFactory;

import javax.imageio.stream.FileImageOutputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public abstract class ShowFrameBase extends JFrame {

    static final int IMAGE_TYPE = BufferedImage.TYPE_INT_ARGB;

    private final String title;
    private final GraphPanel panel;

    private final Running running;
    private final Timer timer = new Timer(20, e -> showRunning());
    private final JSlider speed = new JSlider(0, 1000, 500);
    private final int fps;

    protected ShowFrameBase(String[] args, String title, PanelOptions po,
                            ScheduleFactory factory, SequenceRunner runner) throws IOException, InitException {
        super(title);
        this.title = title;

        int fps = args.length > 1 ? Integer.parseInt(args[0]) : 0;
        File file = new File(args[args.length - 1]);

        this.fps = fps;

        this.panel = new GraphPanel(po, StatModeEnum.ENERGY, runner);

        panel.load(file);

        if (fps > 0) {
            String name = file.getName();
            int p = name.lastIndexOf('.');
            String gifName;
            if (p >= 0){
                gifName = name.substring(0, p);
            } else {
                gifName = "output";
            }
            writer = new GifSequenceWriter(new FileImageOutputStream(new File(gifName + ".gif")), IMAGE_TYPE, 20, true);
        }
        timer.stop();

        setTitle();

        Options options = Options.load(GraphGuiUtil.getPrefs());
        this.running = new Running(panel, options, true, factory);

        add(panel, BorderLayout.CENTER);
        JToolBar bar = new JToolBar();
        bar.add(speed);
        add(bar, BorderLayout.NORTH);
        timer.setInitialDelay(0);
        if (fps > 0) {
            JButton btnStart = new JButton(new AbstractAction("Start") {
                public void actionPerformed(ActionEvent e) {
                    doRun();
                }
            });
            JPanel down = new JPanel();
            down.add(btnStart);
            add(down, BorderLayout.SOUTH);
        }

        speed.addChangeListener(e -> setSpeed());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                System.exit(0);
            }
        });
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        if (fps <= 0) {
            doRun();
        }
    }

    private void setSpeed() {
        int value = speed.getValue();
        running.setSpeed(value / 1000.0);
    }

    private void doRun() {
        setSpeed();
        timer.start();
    }

    private GifSequenceWriter writer = null;
    private BufferedImage image = null;
    private int frame = 0;

    private void showRunning() {
        running.refresh();
        if (writer != null) {
            if (frame % fps == 0) {
                if (image == null) {
                    image = new BufferedImage(panel.getWidth(), panel.getHeight(), IMAGE_TYPE);
                }
                Graphics g = image.getGraphics();
                g.setColor(panel.getBackground());
                g.fillRect(0, 0, image.getWidth(), image.getHeight());
                panel.paintComponent(g);
                try {
                    writer.writeToSequence(image);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            frame++;
        }
    }

    private void setTitle() {
        GraphGuiUtil.setTitle(this, panel, title);
    }
}
