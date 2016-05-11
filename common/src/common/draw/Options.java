package common.draw;

import common.events.ISchedule;

import java.io.File;
import java.util.prefs.Preferences;

class Options {

    final double ampTol;
    final double timeTol;
    final int precision;
    final boolean useLog;
    final File logFile;
    final StatModeEnum logMode;

    Options(double ampTol, double timeTol, int precision,
            boolean useLog, File logFile, StatModeEnum logMode) {
        this.ampTol = ampTol;
        this.timeTol = timeTol;
        this.precision = precision;
        this.useLog = useLog;
        this.logFile = logFile;
        this.logMode = logMode;
    }

    static File getDefaultFile() {
        if (System.getProperty("os.name").toLowerCase().contains("window")) {
            File[] roots = File.listRoots();
            if (roots != null && roots.length > 0) {
                for (File root : roots) {
                    String str = root.getAbsolutePath().toLowerCase();
                    if (str.startsWith("a") || str.startsWith("b"))
                        continue;
                    return new File(root, "graphlog.txt");
                }
            }
        }
        return new File("graphlog.txt");
    }

    static Options load(Preferences prefs) {
        double ampTol = prefs.getDouble("amp.tol", ISchedule.AMP_EPS);
        double timeTol = prefs.getDouble("time.tol", ISchedule.TIME_EPS);
        int precision = prefs.getInt("precision", 0);
        boolean useLog = prefs.getBoolean("log.use", false);
        String logFileName = prefs.get("log.file", getDefaultFile().getAbsolutePath());
        String logFileMode = prefs.get("log.mode", StatModeEnum.NUM_BY_LEN_BY_NUM.name());
        return new Options(
            ampTol, timeTol, precision, useLog, new File(logFileName),
            StatModeEnum.valueOf(logFileMode)
        );
    }

    void save(Preferences prefs) {
        prefs.putDouble("amp.tol", ampTol);
        prefs.putDouble("time.tol", timeTol);
        prefs.putInt("precision", precision);
        prefs.putBoolean("log.use", useLog);
        prefs.put("log.file", logFile == null ? null : logFile.getAbsolutePath());
        prefs.put("log.mode", logMode.name());
    }
}
