package hse.java.lectures.lecture6.tasks.synchronizer;

public class StreamingMonitor {
    int[] ids;
    public StreamingMonitor(int[] ids, int ticksPerWriter) {
        this.ids = ids;
        left = ids.length * ticksPerWriter;
    }

    int left, current;
    public synchronized void awaitTurn(int id) throws InterruptedException {
        while (left == 0 || ids[current] != id) wait();
    }

    public synchronized void awaitDone() throws InterruptedException {
        while (left > 0) wait();
    }

    public synchronized void tick() {
        left -= 1;
        if (++current == ids.length) current = 0;
        notifyAll();
    }
}