package quantum;

import common.events.ISchedule;
import common.events.StatResult;
import common.graph.Graph;
import common.graph.SimpleEdge;
import common.graph.model.GraphModel;
import common.math.Arithmetic;
import quantum.events.Schedule;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

// todo: передавать double в бинарном виде;
// todo: почему кол-во фотонов не зависит от длины ребра?
// todo: когда-то фотоны все-таки изничтожаются?
public final class ProfRun {

    public static ISchedule loadGraph(File file, double timeTol, int precision) throws FileNotFoundException {
        Arithmetic a = Arithmetic.createArithmetic(timeTol, precision);
        GraphModel model = new GraphModel();
        model.load(new FileReader(file));
        SimpleEdge[] edges = model.toSimple(a);
        int numVertex = model.getNumVertex();
        Graph g = new Graph(new Integer[numVertex], edges);
        ISchedule schedule = new Schedule(g, ISchedule.AMP_EPS, a);
        schedule.firstPhotons();
        return schedule;
    }

    public static void main(String[] args) throws IOException {
        ISchedule schedule = loadGraph(new File("D:\\home\\oleg\\miscprogs\\quantum\\new\\graphs\\f_3_1.graph"), 1e-10, 0);
        long t0 = System.currentTimeMillis();
        while (true) {
            if (schedule.getCurrentTime() >= 150)
                break;
            schedule.next();
        }
        System.out.println(System.currentTimeMillis() - t0);
        StatResult stat = schedule.getStat();
        System.out.println(stat.numPhotons + " " + (stat.totalEnergy - 1));
        for (int i = 0; i < stat.edgeNum.length; i++) {
            int num = stat.edgeNum[i];
            System.out.println((i + 1) + ": " + stat.edgeEnergy[i] + " / " + num);
        }
    }
}
