package quantum.draw;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;

final class RunCDialog extends JDialog {

    private final class WatchThread implements Runnable {

        private String getTime() {
            try {
                if (status.exists()) {
                    BufferedReader rdr = new BufferedReader(new FileReader(status));
                    try {
                        String str = rdr.readLine();
                        double time = Double.parseDouble(str);
                        return NumberFormat.getNumberInstance().format(time);
                    } finally {
                        rdr.close();
                    }
                } else {
                    return null;
                }
            } catch (Exception ex) {
                return null;
            }
        }

        private String digit2(int n) {
            if (n < 10) {
                return "0" + n;
            } else {
                return String.valueOf(n);
            }
        }

        private String formatTime(long secs) {
            int sec = (int) (secs % 60);
            secs /= 60;
            int min = (int) (secs % 60);
            int hour = (int) (secs / 60);
            if (hour > 0) {
                return hour + ":" + digit2(min) + ":" + digit2(sec);
            } else {
                return min + ":" + digit2(sec);
            }
        }

        public void run() {
            long t0 = System.currentTimeMillis();
            while (true) {
                try {
                    process.exitValue(); // todo: check exit code - should be 0
                    break;
                } catch (IllegalThreadStateException ex) {
                    // ignore
                }
                final String time = getTime();
                if (time != null) {
                    final String running = formatTime((System.currentTimeMillis() - t0) / 1000);
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            lblTime.setText("Running time: " + running);
                            bar.setString("T=" + time);
                        }
                    });
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    // ignore
                }
            }
            stop.delete();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    bar.setValue(100);
                    dispose();
                    JOptionPane.showMessageDialog(getOwner(), "Результаты до T=" + getTime() + " в файле " + out.getAbsolutePath());
                    status.delete();
                }
            });
        }
    }

    private final File status;
    private final File stop;
    private final File out;
    private final Process process;
    private final JLabel lblTime = new JLabel(" ");
    private final JProgressBar bar = new JProgressBar(0, 100);

    RunCDialog(Frame owner, File status, final File stop, File out, Process process) {
        super(owner, "Running C...", true);
        this.status = status;
        this.stop = stop;
        this.out = out;
        this.process = process;

        bar.setStringPainted(true);
        bar.setIndeterminate(true);
        bar.setString("Starting...");

        JPanel lbl = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lbl.add(lblTime);
        add(lbl, BorderLayout.NORTH);

        add(bar, BorderLayout.CENTER);

        final JButton btnStop = new JButton("Stop");
        btnStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    stop.createNewFile();
                    btnStop.setEnabled(false);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        JPanel butt = new JPanel();
        butt.add(btnStop);
        add(butt, BorderLayout.SOUTH);

        new Thread(new WatchThread()).start();

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
