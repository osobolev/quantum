package quantum.qtree;

import common.draw.VersionUtil;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.IOException;

public final class AnimationFrame extends JFrame {

    private static final int DEFAULT_N = 1000;
    private static final int DELAY = 200;

    private final AnimationPanel panel;
    private final Timer timer = new Timer(DELAY, e -> calculate());
    private final JSlider delaySlider = new JSlider(0, 1000, DELAY);
    private final JSlider scaleSlider = new JSlider(0, 100, 0);
    private final JButton btnPicture = new JButton("Save GIF");
    private final JButton btnData = new JButton("Save TXT");
    private final JButton btnStart = new JButton("Run");
    private final JSpinner txtCount;
    private final JSpinner txtVal1;
    private final JSpinner txtVal2;
    private final JSpinner txtTail = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
    private final JCheckBox cbSymmetric = new JCheckBox("Symmetric start");
    private final JComboBox<TreeMode> chCase = new JComboBox<>(TreeMode.values());

    private ICalculation calculation = null;

    public AnimationFrame(int count, int val) {
        super(VersionUtil.getTitle(VersionUtil.loadRevision(), "Tree"));
        txtCount = new JSpinner(new SpinnerNumberModel(count, 10, 10000, 10));
        txtVal1 = new JSpinner(new SpinnerNumberModel(val, 1, 10, 1));
        txtVal2 = new JSpinner(new SpinnerNumberModel(val, 1, 10, 1));
        chCase.setSelectedItem(TreeMode.PASCAL);
        chCase.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                enableDisable();
            }
        });
        enableDisable();

        this.panel = new AnimationPanel();
        add(panel, BorderLayout.CENTER);

        JPanel up = new JPanel();
        up.add(new JLabel("Steps:"));
        up.add(txtCount);
        up.add(new JLabel("k1="));
        up.add(txtVal1);
        up.add(new JLabel("k2="));
        up.add(txtVal2);
        up.add(new JLabel("Tail="));
        up.add(txtTail);
        up.add(chCase);
        up.add(cbSymmetric);
        up.add(btnStart);
        btnStart.addActionListener(e -> {
            int count1 = ((Integer) txtCount.getValue()).intValue();
            int val1 = ((Integer) txtVal1.getValue()).intValue();
            int val2 = ((Integer) txtVal2.getValue()).intValue();
            int tail = ((Integer) txtTail.getValue()).intValue();
            boolean symmetric = cbSymmetric.isSelected();
            TreeMode mode = (TreeMode) chCase.getSelectedItem();
            calculation = mode.create(count1, val1, val2, symmetric, tail);
            panel.setCount(calculation.getCount(), calculation.getCenter());
            timer.start();
        });
        add(up, BorderLayout.NORTH);

        JPanel down = new JPanel();
        down.add(new JLabel("Delay:"));
        down.add(delaySlider);
        down.add(new JLabel("Scale:"));
        down.add(scaleSlider);
        down.add(btnPicture);
        down.add(btnData);
        delaySlider.addChangeListener(e -> timer.setDelay(delaySlider.getValue()));
        delaySlider.setPaintTicks(true);
        delaySlider.setMajorTickSpacing(100);
        scaleSlider.addChangeListener(e -> setScale());
        scaleSlider.setPaintTicks(true);
        scaleSlider.setMajorTickSpacing(10);
        setScale();
        btnPicture.addActionListener(e -> saveGif());
        btnData.addActionListener(e -> saveText());
        add(down, BorderLayout.SOUTH);

        try {
            setDefaultCloseOperation(EXIT_ON_CLOSE);
        } catch (Exception ex) {
            // ignore
        }
        pack();
        setLocationRelativeTo(null);
        setExtendedState(MAXIMIZED_BOTH);
        setVisible(true);
    }

    private void setScale() {
        panel.setScale(Math.pow(10, scaleSlider.getValue() / 10.0));
    }

    private void calculate() {
        if (calculation != null) {
            double[] energy = calculation.oneStep();
            if (energy == null) {
                timer.stop();
            } else {
                panel.setData(energy, calculation.getSpeed());
            }
        } else {
            timer.stop();
        }
    }

    private void saveGif() {
        timer.stop();
        JFileChooser chooser = new JFileChooser();
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("GIF files", "gif"));
        int res = chooser.showSaveDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            try {
                panel.savePicture(chooser.getSelectedFile());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, ex);
            }
        }
        timer.start();
    }

    private void saveText() {
        timer.stop();
        JFileChooser chooser = new JFileChooser();
        int res = chooser.showSaveDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            try {
                File file = chooser.getSelectedFile();
                if (calculation == null || !calculation.print(file)) {
                    panel.saveText(file);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, ex);
            }
        }
        timer.start();
    }

    public static void main(String[] args) {
        int n = QuantumTree2.parseArg(args, 0, DEFAULT_N);
        int val = QuantumTree2.parseArg(args, 1, 2);
        new AnimationFrame(n, val);
    }

    private void enableDisable() {
        TreeMode mode = (TreeMode) chCase.getSelectedItem();
        boolean advanced = mode == TreeMode.SIMPLE2;
        txtVal2.setEnabled(advanced);
        cbSymmetric.setEnabled(advanced);
        txtTail.setEnabled(mode == TreeMode.SIMPLE0);
    }
}
