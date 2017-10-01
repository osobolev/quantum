package common.draw;

import common.events.*;
import common.graph.Graph;
import common.math.Arithmetic;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormatSymbols;
import java.util.Iterator;

final class Running {

    private final GraphPanel panel;
    private final ISchedule schedule;
    private final Thread calcThread;

    private final boolean showPhotons;

    private boolean running = true;

    private double showing = 0;
    private double delta = 0;
    private boolean inited = false;

    Running(GraphPanel panel, final Options options, boolean showPhotons, ScheduleFactory factory) throws InitException {
        this.panel = panel;
        this.showPhotons = showPhotons;
        Arithmetic a = Arithmetic.createArithmetic(options.timeTol, options.precision);
        Graph graph = panel.toGraph(a, null);
        if (graph.getEdgeNum() <= 0 || graph.getVertexNum() <= 0)
            throw new InitException("Graph is empty");
        this.schedule = factory.newSchedule(graph, options.ampTol, options.runMode, a);

        calcThread = new Thread(new Runnable() {
            public void run() {
                init();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    //
                }
                PrintWriter w = null;
                if (options.useLog) {
                    // todo: move logging to GraphPanel?
                    try {
                        w = new PrintWriter(options.logFile);
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }
                DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance();
                char separator = dfs.getDecimalSeparator();
                while (true) {
                    synchronized (schedule) {
                        if (!running)
                            break;
                    }
                    if (w != null) {
                        StatResult stat = schedule.getStat();
                        w.print(stat.currentTime.toString().replace('.', separator));
                        Iterator<String> values = options.logMode.mode.getValues(stat);
                        while (values.hasNext()) {
                            String value = values.next();
                            w.print("\t" + value.replace('.', separator));
                        }
                        w.println();
                    }
                    if (!schedule.next())
                        break;
                }
                if (w != null) {
                    w.close();
                }
            }
        });
    }

    private void init() {
        if (!inited) {
            inited = true;
            schedule.firstPhotons();
        }
    }

    void start() {
        if (!showPhotons) {
            calcThread.start();
        }
    }

    StatResult getStat() {
        return schedule.getStat();
    }

    ShowState getPhotons() {
        if (showPhotons) {
            init();
            ShowState state = schedule.showPhotons(showing);
            showing += delta;
            return state;
        } else {
            return null;
        }
    }

    void stop() {
        synchronized (schedule) {
            running = false;
        }
    }

    /**
     * @param speed 0 to 1
     */
    void setSpeed(double speed) {
        synchronized (schedule) {
            delta = (Math.pow(10, speed * 2 - 1) - 0.1) * 0.01;
        }
    }

    boolean refresh() {
        StatResult result = getStat();
        ShowState map = getPhotons();
        if (showPhotons && map == null) {
            return false;
        } else {
            panel.setStat(result, map);
            return true;
        }
    }
}
