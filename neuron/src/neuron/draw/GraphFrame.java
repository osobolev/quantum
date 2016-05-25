package neuron.draw;

import common.draw.GraphGuiUtil;
import common.draw.PanelOptions;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public final class GraphFrame extends JFrame {

    private static final PanelOptions PO = new PanelOptions(true, false, true);

    private final JSpinner tfStartImpulses;
    private final JTextField tfTauPeriod;
    private final JTextField tfTauRestore;
    private final JTextField tfDT;
    private final GraphGuiUtil util;

    public GraphFrame(File file, Readable source) {
        Preferences prefs = GraphGuiUtil.getPrefs();
        int initial = Integer.parseInt(prefs.get("initial.packets", "100"));
        tfStartImpulses = new JSpinner(new SpinnerNumberModel(initial, 1, 999, 1));
        tfTauPeriod = new JTextField(prefs.get("tau.period", "1"), 4);
        tfTauRestore = new JTextField(prefs.get("tau.restore", "1"), 4);
        tfDT = new JTextField(prefs.get("mean.dt", "1"), 4);
        tfStartImpulses.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                saveSettings();
            }
        });
        DocumentListener documentListener = new DocumentListener() {

            public void insertUpdate(DocumentEvent e) {
                saveSettings();
            }

            public void removeUpdate(DocumentEvent e) {
                saveSettings();
            }

            public void changedUpdate(DocumentEvent e) {
                saveSettings();
            }
        };
        tfTauPeriod.getDocument().addDocumentListener(documentListener);
        tfTauRestore.getDocument().addDocumentListener(documentListener);
        tfDT.getDocument().addDocumentListener(documentListener);
        util = new GraphGuiUtil(PO, this, "Neuron", file, source, new NeuronScheduleFactory(tfStartImpulses, tfTauPeriod, tfTauRestore, tfDT), false);
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
        bar.addSeparator();
        bar.add(util.optionsAction);
        bar.addSeparator();
        bar.add(util.speed);
        JPanel si = new JPanel();
        si.add(new JLabel("Impulses:"));
        si.add(tfStartImpulses);
        si.add(new JLabel("TauP:"));
        si.add(tfTauPeriod);
        si.add(new JLabel("TauR:"));
        si.add(tfTauRestore);
        si.add(new JLabel("DT:"));
        si.add(tfDT);
        bar.add(si);
        add(bar, BorderLayout.NORTH);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void saveSettings() {
        Preferences prefs = GraphGuiUtil.getPrefs();
        prefs.put("initial.packets", tfStartImpulses.getValue().toString());
        prefs.put("tau.period", tfTauPeriod.getText());
        prefs.put("tau.restore", tfTauRestore.getText());
        prefs.put("mean.dt", tfDT.getText());
        try {
            prefs.flush();
        } catch (BackingStoreException ex) {
            // ignore
        }
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
}
