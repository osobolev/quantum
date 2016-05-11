package quantum.comparator;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public final class CompareFrame extends JFrame {

    private final ComparePanel1 panel1;
    private final JSlider slider1;
    private final ComparePanel2 panel2;
    private final JSlider slider2;

    private final JSpinner spin1 = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));
    private final JSpinner spin2 = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));
    private final JSpinner spin3 = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));

    private void syncPanel1() {
        panel1.setEnd(slider1.getValue());
    }

    private void syncPanel2() {
        panel2.setStart(slider2.getValue());
    }

    public CompareFrame(List<Double> times, List<Double> photons, boolean standalone) {
        super("Compare");
        JTabbedPane tab = new JTabbedPane();

        panel1 = new ComparePanel1(times, photons);
        slider1 = new JSlider(0, times.size(), times.size());
        slider1.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                syncPanel1();
            }
        });
        syncPanel1();
        JPanel p1 = new JPanel(new BorderLayout());
        p1.add(panel1, BorderLayout.CENTER);
        p1.add(slider1, BorderLayout.SOUTH);
        JPanel up1 = new JPanel();
        up1.add(new JLabel("Degree 1:"));
        up1.add(spin1);
        up1.add(new JLabel("Degree 2:"));
        up1.add(spin2);
        up1.add(new JLabel("Degree 3:"));
        up1.add(spin3);
        p1.add(up1, BorderLayout.NORTH);
        tab.addTab("Poly", p1);

        panel2 = new ComparePanel2(times, photons);
        slider2 = new JSlider(0, times.size(), times.size() / 10);
        slider2.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                syncPanel2();
            }
        });
        syncPanel2();
        JPanel p2 = new JPanel(new BorderLayout());
        p2.add(panel2, BorderLayout.CENTER);
        p2.add(slider2, BorderLayout.SOUTH);
        tab.addTab("Asympt", p2);

        add(tab, BorderLayout.CENTER);

        setDefaultCloseOperation(standalone ? EXIT_ON_CLOSE : DISPOSE_ON_CLOSE);

        ChangeListener listener = new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                setCompare();
            }
        };
        spin1.addChangeListener(listener);
        spin2.addChangeListener(listener);
        spin3.addChangeListener(listener);
        setCompare();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void setCompare() {
        SortedSet<Integer> set = new TreeSet<Integer>();
        set.add((Integer) spin1.getValue());
        set.add((Integer) spin2.getValue());
        set.add((Integer) spin3.getValue());
        List<Integer> degrees = new ArrayList<Integer>(set);
        panel1.setCompare(degrees);
        panel2.setCompare(degrees);
    }

    public static void main(String[] args) throws IOException {
        String name;
        if (args.length > 0) {
            name = args[0];
        } else {
            name = "D:\\home\\oleg\\miscprogs\\quantum\\cpp\\newer\\x_2b.out";
        }
        TableReader data = TableReader.read(name);
        List<Double> main = data.columns.get(0);
        List<Double> rest = new ArrayList<Double>(main.size());
        for (int i = 0; i < main.size(); i++) {
            Double value = main.get(i);
            double time = data.times.get(i).doubleValue();
            //int sub = (int) Math.round(1.0076825e-7 * Math.pow(time, 5) + 2.44317e-6 * Math.pow(time, 4) - 1.632e-5 * Math.pow(time, 3) + 3.727e-3 * Math.pow(time, 2));
            //int sub = (int) Math.round(3e-10 * Math.pow(time, 9) + 7.1e-8 * Math.pow(time, 8));
            int sub = 0;
            rest.add(value.doubleValue() - sub);
        }
        new CompareFrame(data.times, rest, true);
    }
}
