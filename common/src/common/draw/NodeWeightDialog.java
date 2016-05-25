package common.draw;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

final class NodeWeightDialog extends JDialog {

    private final JTextField tfWeight;
    private final JButton btnOk = new JButton("OK");
    private final JButton btnCancel = new JButton("Cancel");

    private boolean ok = false;
    private Integer result;

    NodeWeightDialog(Frame owner, Integer weight) {
        super(owner, "Enter weight", true);
        result = weight;

        JPanel center = new JPanel();
        center.add(new JLabel("Weight:"));
        tfWeight = new JTextField(10);
        center.add(tfWeight);
        if (weight != null) {
            tfWeight.setText(weight.toString());
        }

        JPanel main = new JPanel(new BorderLayout());
        main.add(center, BorderLayout.CENTER);

        JPanel down = new JPanel();
        down.add(btnOk);
        down.add(btnCancel);
        btnOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String s = tfWeight.getText();
                try {
                    result = validate(s);
                    ok = true;
                    dispose();
                } catch (NumberFormatException nfex) {
                    JOptionPane.showMessageDialog(NodeWeightDialog.this, "Enter valid expression", "Error", JOptionPane.ERROR_MESSAGE);
                    tfWeight.requestFocusInWindow();
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

    private static Integer validate(String text) {
        if (text.trim().isEmpty())
            return null;
        return Integer.valueOf(text);
    }

    boolean isOk() {
        return ok;
    }

    Integer getResult() {
        return result;
    }
}
