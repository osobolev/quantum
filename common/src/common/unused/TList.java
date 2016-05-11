package common.unused;

import java.util.Iterator;
import java.util.TreeMap;

public final class TList<T extends Comparable<T>> extends AbstractList<T> {

    private final TreeMap<T, T> list = new TreeMap<T, T>();

    public void binarySearch(T value, Found found) {
        Comparable<T> existing = list.get(value);
        if (existing != null) {
            found.index = 0;
            found.shift = 1;
            found.entry = existing;
        } else {
            found.index = -1;
            found.shift = 0;
            found.entry = value;
        }
    }

    @SuppressWarnings("unchecked")
    public T getAt(Found found) {
        if (found.shift != 0) {
            return (T) found.entry;
        } else {
            return list.ceilingKey((T) found.entry);
        }
    }

    @SuppressWarnings("unchecked")
    public T getBefore(Found found) {
        return list.lowerKey((T) found.entry);
    }

    public T deleteFirst() {
        T ret = list.firstKey();
        list.remove(ret);
        return ret;
    }

    public T getFirst() {
        return list.firstKey();
    }

    public void insert(Found where, T value) {
        list.put(value, value);
    }

    public Iterator<T> iterator() {
        return list.keySet().iterator();
    }
}
