package common.draw;

import common.math.Arithmetic;
import common.math.JSRunner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

final class WeightDialog extends JDialog {

    private final JSRunner runner = new JSRunner(Arithmetic.createArithmetic(0, 0));

    private final JTextField tfWeight;
    private final JComboBox chStart = new JComboBox(new String[] {"-", "Forward", "Back"});
    private final JCheckBox cbStart = new JCheckBox("Start edge");
    private final JButton btnOk = new JButton("OK");
    private final JButton btnCancel = new JButton("Cancel");

    private final boolean directed;

    private boolean ok = false;
    private String result;

    WeightDialog(Frame owner, String value, Boolean direction, boolean directed) {
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

        JPanel center = new JPanel();
        center.add(new JLabel("Weight:"));
        tfWeight = new JTextField(value, 10);
        center.add(tfWeight);

        JPanel start = new JPanel();
        if (directed) {
            start.add(cbStart);
        } else {
            start.add(new JLabel("Initial direction:"));
            start.add(chStart);
        }

        JPanel main = new JPanel(new BorderLayout());
        main.add(center, BorderLayout.CENTER);
        main.add(start, BorderLayout.SOUTH);

        JPanel down = new JPanel();
        down.add(btnOk);
        down.add(btnCancel);
        btnOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String s = tfWeight.getText().replace(',', '.');
                if (validate(s)) {
                    result = s;
                    ok = true;
                    dispose();
                }
            }
        });
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        add(main, BorderLayout.CENTER);
        add(down, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(btnOk);
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
