package common.events;

public abstract class EventData<T> {

    public abstract boolean isStat(T event);

    public abstract int getEdge(T event);

    public abstract double getAmp(T event);

    public abstract boolean isForward(T event);
}
