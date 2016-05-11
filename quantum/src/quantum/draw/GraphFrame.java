package quantum.draw;

import common.draw.GraphGuiUtil;
import common.draw.PanelOptions;
import quantum.comparator.CompareFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public final class GraphFrame extends JFrame {

    static final PanelOptions PO = new PanelOptions(false, true, false);

    private final GraphGuiUtil util;

    public GraphFrame(File file, Readable source) {
        util = new GraphGuiUtil(PO, this, "Graph tool", file, source, new QuantumScheduleFactory());
        util.addTo(this);
        JToolBar bar = new JToolBar();
        bar.add(new AbstractAction("New") {
            public void actionPerformed(ActionEvent e) {
                util.newGraph();
            }
        });
        bar.add(new AbstractAction("Open") {
            public void actionPerformed(ActionEvent e) {
                util.saveOrLoad(false, true);
            }
        });
        bar.add(new AbstractAction("Save") {
            public void actionPerformed(ActionEvent e) {
                util.saveOrLoad(true, false);
            }
        });
        bar.add(new AbstractAction("Save as") {
            public void actionPerformed(ActionEvent e) {
                util.saveOrLoad(true, true);
            }
        });
        bar.add(new AbstractAction("Export") {
            public void actionPerformed(ActionEvent e) {
                util.export();
            }
        });
        bar.addSeparator();
        JButton btnRun = bar.add(util.runAction);
        util.setRunButton(btnRun);
        bar.add(util.pauseAction);
        bar.add(new AbstractAction("Run in C") {
            public void actionPerformed(ActionEvent e) {
                runC();
            }
        });
        bar.addSeparator();
        bar.add(util.optionsAction);
        bar.add(new AbstractAction("Compare") {
            public void actionPerformed(ActionEvent e) {
                new CompareFrame(util.getTimes(), util.getStats(), false);
            }
        });
        bar.addSeparator();
        bar.add(util.speed);
        add(bar, BorderLayout.NORTH);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void dispose() {
        if (util.checkSave()) {
            super.dispose();
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        File file = args.length > 0 ? new File(args[0]) : null;
        new GraphFrame(file, null);
    }

    private void runC() {
        File file = util.saveToC();
        if (file == null) {
            return;
        }
        File exe = new File("grcalc.exe");
        if (!exe.isFile()) {
            JOptionPane.showMessageDialog(this, "Cannot find executable file " + exe.getAbsolutePath(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        File status = new File("status.txt");
        status.delete();
        File stop = new File("stop.txt");
        stop.delete();
        try {
            File out = new File("out.txt");
            Process process = Runtime.getRuntime().exec(new String[] {
                exe.getAbsolutePath(),
                "-t", "0",
                "-o", "\"" + out.getAbsolutePath() + "\"",
                "-i", "\"" + status.getAbsolutePath() + "\"",
                "-s", "\"" + stop.getAbsolutePath() + "\"",
                "\"" + file.getAbsolutePath() + "\""
            });
            new RunCDialog(this, status, stop, out, process);
        } catch (Exception ex) {
            util.handleException(ex);
        }
    }
}
