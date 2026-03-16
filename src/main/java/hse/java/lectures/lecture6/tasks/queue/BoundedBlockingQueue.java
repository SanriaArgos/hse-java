package hse.java.lectures.lecture6.tasks.queue;

public class BoundedBlockingQueue<T> {

    T[] items;
    public BoundedBlockingQueue(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException();
        items = (T[]) new Object[capacity];
    }

    int size, tail;
    public synchronized void put(T item) throws InterruptedException {
        if (item == null) throw new NullPointerException();
        while (size == items.length) wait();
        items[tail] = item;
        if (++tail == items.length) tail = 0;
        size += 1;
        notifyAll();
    }

    int head;
    public synchronized T take() throws InterruptedException {
        while (size == 0) wait();
        T item = items[head];
        items[head] = null;
        if (++head == items.length) head = 0;
        size -= 1;
        notifyAll();
        return item;
    }

    public synchronized int size() {
        return size;
    }

    public int capacity() {
        return items.length;
    }
}
