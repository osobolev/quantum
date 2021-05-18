package common.unused;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Random;

@SuppressWarnings("unchecked")
public final class LList<T extends Comparable<T>> implements Iterable<T> {

    private static final int CAPACITY = 1000;

    private static final class Entry<T> {

        private final Object[] list;
        private int size = 0;
        private Entry<T> prev;
        private Entry<T> next;

        Entry() {
            list = new Object[CAPACITY];
        }

        @Override
        public String toString() {
            StringBuilder buf = new StringBuilder();
            for (int j = 0; j < size; j++) {
                Object i = list[j];
                buf.append(' ').append(i);
            }
            return buf.toString();
        }
    }

    private Entry<T> head = null;
    private Entry<T> tail = null;
    private int size = 0;

    public void binarySearch(T value, Found found) {
        if (size <= 0) {
            found.index = -1;
            found.entry = null;
            found.shift = 0;
            return;
        }
        int low = 0;
        int high = size - 1;
        Entry<T> elow = head;
        int shiftLow = 0; // индекс elow.list[0] во всем списке
        while (low <= high) {
            int mid = (low + high) >>> 1;
            int delta = mid - shiftLow;
            Entry<T> emid = elow;
            int shiftMid = shiftLow;
            while (delta >= emid.size) {
                shiftMid += emid.size;
                delta -= emid.size;
                emid = emid.next;
            }
            T midVal = (T) emid.list[delta];
            int cmp = midVal.compareTo(value);
            if (cmp < 0) {
                low = mid + 1;
                if (low - shiftMid < emid.size) {
                    elow = emid;
                    shiftLow = shiftMid;
                } else {
                    elow = emid.next;
                    shiftLow = shiftMid + emid.size;
                }
            } else if (cmp > 0) {
                high = mid - 1;
            } else {
                found.index = mid;
                found.entry = emid;
                found.shift = shiftMid;
                return;
            }
        }
        found.index = -(low + 1);
        if (elow == null) {
            found.entry = tail;
            found.shift = size - tail.size;
        } else {
            found.entry = elow;
            found.shift = shiftLow;
        }
    }

    public void insert(Found where, T value) {
        if (size <= 0) {
            Entry<T> entry = new Entry<>();
            entry.size = 1;
            entry.list[0] = value;
            head = tail = entry;
        } else {
            int index = where.index - where.shift;
            Entry<?> whereEntry = (Entry<?>) where.entry;
            int entrySize = whereEntry.size;
            if (entrySize >= CAPACITY) {
                if (index == entrySize) {
                    Entry<?> next = whereEntry.next;
                    if (next != null && next.size < CAPACITY) {
                        System.arraycopy(next.list, 0, next.list, 1, next.size);
                        next.list[0] = value;
                        next.size++;
                    } else {
                        Entry<T> entry = new Entry<>();
                        entry.size = 1;
                        entry.list[0] = value;

                        insertAfter(where, entry);
                    }
                } else {
                    Entry<?> next = whereEntry.next;
                    if (next != null && next.size < CAPACITY) {
                        System.arraycopy(next.list, 0, next.list, 1, next.size);
                        next.list[0] = whereEntry.list[entrySize - 1];
                        next.size++;
                        System.arraycopy(whereEntry.list, index, whereEntry.list, index + 1, entrySize - index - 1);
                        whereEntry.list[index] = value;
                    } else {
                        Entry<T> entry = new Entry<>();
                        entry.size = 1;
                        entry.list[0] = whereEntry.list[entrySize - 1];
                        System.arraycopy(whereEntry.list, index, whereEntry.list, index + 1, entrySize - index - 1);
                        whereEntry.list[index] = value;

                        insertAfter(where, entry);
                    }
                }
            } else {
                System.arraycopy(whereEntry.list, index, whereEntry.list, index + 1, entrySize - index);
                whereEntry.list[index] = value;
                whereEntry.size++;
            }
        }
        size++;
    }

    private void insertAfter(Found where, Entry<T> entry) {
        Entry<T> whereEntry = (Entry<T>) where.entry;
        entry.prev = whereEntry;
        entry.next = whereEntry.next;
        whereEntry.next = entry;
        if (entry.next != null) {
            entry.next.prev = entry;
        }
        if (whereEntry == tail) {
            tail = entry;
        }
    }

    public T getAt(Found found) {
        Entry<?> foundEntry = (Entry<?>) found.entry;
        if (foundEntry == null)
            return null;
        int index = found.index - found.shift;
        if (index < foundEntry.size) {
            return (T) foundEntry.list[index];
        } else {
            Entry<?> next = foundEntry.next;
            if (next != null) {
                return (T) next.list[0];
            } else {
                return null;
            }
        }
    }

    public T getBefore(Found found) {
        Entry<?> foundEntry = (Entry<?>) found.entry;
        if (foundEntry == null)
            return null;
        int index = found.index - found.shift;
        if (index > 0) {
            return (T) foundEntry.list[index - 1];
        } else {
            Entry<?> prev = foundEntry.prev;
            if (prev != null) {
                return (T) prev.list[prev.size - 1];
            } else {
                return null;
            }
        }
    }

    public T deleteFirst() {
        T ret = (T) head.list[0];
        if (head.size <= 1) {
            Entry<T> second = head.next;
            if (second != null) {
                second.prev = null;
                head = second;
            } else {
                head = tail = null;
            }
        } else {
            System.arraycopy(head.list, 1, head.list, 0, head.size - 1);
            head.size--;
        }
        size--;
        return ret;
    }

    public T getFirst() {
        return (T) head.list[0];
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        Entry<T> e = head;
        while (e != null) {
            buf.append('|');
            for (int i = 0; i < e.size; i++) {
                buf.append(' ').append(e.list[i]);
            }
            e = e.next;
        }
        return buf.toString();
    }

    public Iterator<T> iterator() {
        return new Iterator<T>() {

            private Entry<T> current = head;
            private int index = 0;

            public boolean hasNext() {
                return current != null;
            }

            public T next() {
                T ret = (T) current.list[index++];
                if (index >= current.size) {
                    current = current.next;
                    index = 0;
                }
                return ret;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public static void main(String[] args) throws FileNotFoundException {
        Random rnd = new Random(0);
        LList<Integer> list = new LList<>();
        long t0 = System.currentTimeMillis();
        Found found = new Found();
        for (int i = 0; i < 100000; i++) {
            int value = rnd.nextInt();
            list.binarySearch(value, found);
            if (found.index < 0) {
                found.transform();
            }
            list.insert(found, value);
            if (i % 3 == 0) {
                list.deleteFirst();
            }
        }
        System.out.println(System.currentTimeMillis() - t0);
        PrintWriter pw = new PrintWriter("C:\\llist");
        for (Integer e : list) {
            pw.println(e);
        }
        pw.close();
    }
}
