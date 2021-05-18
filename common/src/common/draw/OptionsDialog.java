package common.draw;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

final class OptionsDialog extends JDialog {

    private final JTextField tfAmpTol = new JTextField(10);
    private final JTextField tfTimeTol = new JTextField(10);
    private final JTextField tfPrecision = new JTextField(10);
    private final JCheckBox cbLog = new JCheckBox("Print to log");
    private final JTextField tfFile = new JTextField(10);
    private final JComboBox<StatModeEnum> chStatMode = new JComboBox<>(StatModeEnum.values());
    private final JComboBox<RunMode> chRunMode = new JComboBox<>(RunMode.values());
    private final JButton btnOk = new JButton("OK");
    private final JButton btnCancel = new JButton("Cancel");

    private boolean ok = false;
    private Options result;

    OptionsDialog(Frame owner, Options options) {
        super(owner, "Options", true);
        this.result = options;

        ToolTipManager.sharedInstance().setDismissDelay(60000);

        JPanel center = new JPanel(new GridBagLayout());
        JLabel lblAmptol = new JLabel("Amplitude tolerance:");
        lblAmptol.setToolTipText(
            "<html>Minimal amplitude of packet to be considered existing</html>"
        );
        center.add(lblAmptol, new GridBagConstraints(
            0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0
        ));
        center.add(tfAmpTol, new GridBagConstraints(
            1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0
        ));
        JLabel lblTimetol = new JLabel("Time tolerance:");
        lblTimetol.setToolTipText(
            "<html>Distance between two packets when they are considered collided.<br>" +
            "Should be >0 because of rounding errors, but sufficiently small</html>"
        );
        center.add(lblTimetol, new GridBagConstraints(
            0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0
        ));
        center.add(tfTimeTol, new GridBagConstraints(
            1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0
        ));
        JLabel lblPrecision = new JLabel("Precision:");
        lblPrecision.setToolTipText(
            "<html>Number of digits used for calculation:<br>" +
            "0 to use native doubles (~14 digits precision),<br>" +
            ">0 to use Apfloat library (much slower, but more precision)</html>"
        );
        center.add(lblPrecision, new GridBagConstraints(
            0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0
        ));
        center.add(tfPrecision, new GridBagConstraints(
            1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0
        ));
        center.add(cbLog, new GridBagConstraints(
            0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0
        ));
        center.add(tfFile, new GridBagConstraints(
            1, 3, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0
        ));
        center.add(new JLabel("Log mode:"), new GridBagConstraints(
            0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0
        ));
        center.add(chStatMode, new GridBagConstraints(
            1, 4, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0
        ));
        center.add(new JLabel("Run mode:"), new GridBagConstraints(
            0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0
        ));
        center.add(chRunMode, new GridBagConstraints(
            1, 5, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0
        ));
        tfAmpTol.setText(String.valueOf(options.ampTol));
        tfTimeTol.setText(String.valueOf(options.timeTol));
        tfPrecision.setText(String.valueOf(options.precision));
        cbLog.setSelected(options.useLog);
        tfFile.setText(options.logFile.getAbsolutePath());
        chStatMode.setSelectedItem(options.logMode);
        chRunMode.setSelectedItem(options.runMode);
        JPanel down = new JPanel();
        down.add(btnOk);
        down.add(btnCancel);
        cbLog.addActionListener(e -> loggingChanged());
        loggingChanged();
        btnOk.addActionListener(e -> {
            result = validateInput();
            if (result != null) {
                ok = true;
                dispose();
            }
        });
        btnCancel.addActionListener(e -> dispose());
        add(center, BorderLayout.CENTER);
        add(down, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(btnOk);
        pack();
        setLocationRelativeTo(owner);
        setVisible(true);
    }

    private void loggingChanged() {
        boolean on = cbLog.isSelected();
        tfFile.setEnabled(on);
        chStatMode.setEnabled(on);
    }

    private Options validateInput() {
        double ampTol;
        try {
            ampTol = Double.parseDouble(tfAmpTol.getText().replace(',', '.'));
        } catch (NumberFormatException nfex) {
            JOptionPane.showMessageDialog(this, "Enter valid amplitude tolerance", "Error", JOptionPane.ERROR_MESSAGE);
            tfAmpTol.requestFocusInWindow();
            return null;
        }
        double timeTol;
        try {
            timeTol = Double.parseDouble(tfTimeTol.getText().replace(',', '.'));
        } catch (NumberFormatException nfex) {
            JOptionPane.showMessageDialog(this, "Enter valid time tolerance", "Error", JOptionPane.ERROR_MESSAGE);
            tfTimeTol.requestFocusInWindow();
            return null;
        }
        int precision;
        try {
            precision = Integer.parseInt(tfPrecision.getText());
        } catch (NumberFormatException nfex) {
            JOptionPane.showMessageDialog(this, "Enter valid precision", "Error", JOptionPane.ERROR_MESSAGE);
            tfPrecision.requestFocusInWindow();
            return null;
        }
        boolean useLog = cbLog.isSelected();
        File logFile = new File(tfFile.getText());
        if (useLog) {
            try {
                logFile = logFile.getCanonicalFile();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Enter valid file name", "Error", JOptionPane.ERROR_MESSAGE);
                tfFile.requestFocusInWindow();
                return null;
            }
        }
        return new Options(
            ampTol, timeTol, precision, useLog, logFile,
            (StatModeEnum) chStatMode.getSelectedItem(), (RunMode) chRunMode.getSelectedItem()
        );
    }

    boolean isOk() {
        return ok;
    }

    Options getResult() {
        return result;
    }
}
