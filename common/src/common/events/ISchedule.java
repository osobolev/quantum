package common.events;

public abstract class ISchedule {

    public static final double TIME_EPS = 1e-10;
    public static final double AMP_EPS = 0;

    public abstract void firstPhotons();

    public abstract boolean next();

    public abstract StatResult getStat();

    public abstract double getCurrentTime();

    public abstract ShowState showPhotons(double time);
}
