package common.unused;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public final class AList<T extends Comparable<T>> extends AbstractList<T> {

    private final ArrayList<T> list = new ArrayList<T>();

    public void binarySearch(T value, Found found) {
        found.index = Collections.binarySearch(list, value);
    }

    public T getAt(Found found) {
        int i = found.index;
        if (i < list.size()) {
            return list.get(i);
        } else {
            return null;
        }
    }

    public T getBefore(Found found) {
        int i = found.index;
        if (i > 0) {
            return list.get(i - 1);
        } else {
            return null;
        }
    }

    public T deleteFirst() {
        return list.remove(0);
    }

    public T getFirst() {
        return list.get(0);
    }

    public void insert(Found where, T value) {
        list.add(where.index, value);
    }

    public Iterator<T> iterator() {
        return list.iterator();
    }
}
