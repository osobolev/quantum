package common.unused;

public abstract class AbstractList<T extends Comparable<T>> implements Iterable<T> {

    public abstract void binarySearch(T value, Found found);

    public abstract T getAt(Found found);

    public abstract T getBefore(Found found);

    public abstract T deleteFirst();

    public abstract T getFirst();

    public abstract void insert(Found where, T value);
}
