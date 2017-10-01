package neuron.draw;

import common.draw.RunMode;
import common.events.ISchedule;
import common.events.InitException;
import common.events.ScheduleFactory;
import common.graph.Graph;
import common.math.Arithmetic;
import neuron.events.Schedule;

import javax.swing.*;

final class NeuronScheduleFactory implements ScheduleFactory {

    private final JSpinner spinner;
    private final JTextField tfTauPeriod;
    private final JTextField tfTauRestore;
    private final JTextField tfDT;

    NeuronScheduleFactory(JSpinner spinner, JTextField tfTauPeriod, JTextField tfTauRestore, JTextField tfDT) {
        this.spinner = spinner;
        this.tfTauPeriod = tfTauPeriod;
        this.tfTauRestore = tfTauRestore;
        this.tfDT = tfDT;
    }

    public ISchedule newSchedule(Graph g, double ampTol, RunMode runMode, Arithmetic a) throws InitException {
        int numImpulses = ((Number) spinner.getValue()).intValue();
        double tauPeriod = parseTau(tfTauPeriod);
        double tauRestore = parseTau(tfTauRestore);
        return new Schedule(g, a, numImpulses, tauPeriod, tauRestore, tfDT);
    }

    private static double parseTau(final JTextField tfTau) throws InitException {
        try {
            return Double.parseDouble(tfTau.getText().replace(',', '.'));
        } catch (NumberFormatException ex) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    tfTau.requestFocusInWindow();
                }
            });
            throw new InitException("Enter valid tau value");
        }
    }
}
