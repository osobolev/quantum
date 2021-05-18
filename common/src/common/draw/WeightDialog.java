package common.draw;

import common.math.Arithmetic;
import common.math.JSRunner;

import javax.swing.*;
import java.awt.*;

final class WeightDialog extends JDialog {

    private final JSRunner runner = new JSRunner(Arithmetic.createArithmetic(0, 0));

    private final JTextField tfWeight;
    private final JComboBox<String> chStart = new JComboBox<>(new String[] {"-", "Forward", "Back"});
    private final JCheckBox cbStart = new JCheckBox("Start edge");
    private final JSpinner tfFrom = new JSpinner(new SpinnerNumberModel(1, 1, 1000000, 1));
    private final JSpinner tfTo = new JSpinner(new SpinnerNumberModel(1, 1, 1000000, 1));
    private final JSpinner tfStep = new JSpinner(new SpinnerNumberModel(1, 1, 1000000, 1));
    private final JButton btnOk = new JButton("OK");
    private final JButton btnCancel = new JButton("Cancel");

    private final boolean directed;

    private boolean ok = false;
    private String result;
    private int[] range = null;

    WeightDialog(Frame owner, SequenceRunner runner, String value, Boolean direction, boolean directed) {
        super(owner, "Enter weight", true);
        this.directed = directed;
        result = value;

        if (directed) {
            cbStart.setSelected(direction != null);
        } else {
            if (direction == null) {
                chStart.setSelectedIndex(0);
            } else {
                chStart.setSelectedIndex(direction.booleanValue() ? 1 : 2);
            }
        }

        tfWeight = new JTextField(value, 10);

        JPanel center1 = new JPanel(new GridBagLayout());
        center1.add(new JLabel("Weight:"), new GridBagConstraints(
            0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0
        ));
        center1.add(tfWeight, new GridBagConstraints(
            1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 0, 5), 0, 0
        ));

        if (directed) {
            center1.add(cbStart, new GridBagConstraints(
                1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0
            ));
        } else {
            center1.add(new JLabel("Initial direction:"), new GridBagConstraints(
                0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0
            ));
            center1.add(chStart, new GridBagConstraints(
                1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0
            ));
        }

        JTabbedPane tab = new JTabbedPane();
        tab.addTab("Edge", center1);
        if (runner != null) {
            JPanel center2 = new JPanel(new GridBagLayout());
            center2.add(new JLabel("From:"), new GridBagConstraints(
                0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0
            ));
            center2.add(tfFrom, new GridBagConstraints(
                1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 5), 0, 0
            ));
            center2.add(new JLabel("To:"), new GridBagConstraints(
                0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0
            ));
            center2.add(tfTo, new GridBagConstraints(
                1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 5), 0, 0
            ));
            center2.add(new JLabel("Step:"), new GridBagConstraints(
                0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0
            ));
            center2.add(tfStep, new GridBagConstraints(
                1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0
            ));
            tab.addTab("Run", center2);
        }

        JPanel down = new JPanel();
        down.add(btnOk);
        down.add(btnCancel);
        btnOk.addActionListener(e -> {
            if (tab.getSelectedIndex() == 1) {
                int from = ((Number) tfFrom.getValue()).intValue();
                int to = ((Number) tfTo.getValue()).intValue();
                int step = ((Number) tfStep.getValue()).intValue();
                range = new int[] {from, to, step};
                dispose();
            } else {
                String s = tfWeight.getText().replace(',', '.');
                if (validate(s)) {
                    result = s;
                    ok = true;
                    dispose();
                }
            }
        });
        btnCancel.addActionListener(e -> dispose());
        add(tab, BorderLayout.CENTER);
        add(down, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(btnOk);

        SwingUtilities.invokeLater(tfWeight::requestFocusInWindow);

        pack();
        setLocationRelativeTo(owner);
        setVisible(true);
    }

    private boolean validate(String text) {
        Number number = runner.evaluate(text);
        if (number != null)
            return true;
        JOptionPane.showMessageDialog(this, "Enter valid expression", "Error", JOptionPane.ERROR_MESSAGE);
        tfWeight.requestFocusInWindow();
        return false;
    }

    boolean isOk() {
        return ok;
    }

    int[] getRange() {
        return range;
    }

    String getResult() {
        return result;
    }

    Boolean getStartDirection() {
        if (directed) {
            return cbStart.isSelected() ? true : null;
        } else {
            int i = chStart.getSelectedIndex();
            if (i == 0) {
                return null;
            } else {
                return i == 1;
            }
        }
    }
}
