package hse.java.lectures.lesson7.dau;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DauServiceTest {
    @Test
    void shouldHandleMainCases() {
        DauService service = new DauService();

        service.postEvent(new Event(1, 10));
        service.postEvent(new Event(1, 10));
        service.postEvent(new Event(2, 10));
        service.postEvent(new Event(3, 11));

        assertEquals(Long.valueOf(0), service.getAuthorDauStatistics(10));
        assertEquals(Long.valueOf(0), service.getAuthorDauStatistics(11));
        assertEquals(Long.valueOf(0), service.getAuthorDauStatistics(12));

        service.day = LocalDate.now().minusDays(1);
        assertEquals(Long.valueOf(2), service.getAuthorDauStatistics(10));
        assertEquals(Long.valueOf(1), service.getAuthorDauStatistics(11));
        assertEquals(Long.valueOf(0), service.getAuthorDauStatistics(12));

        Map<Integer, Long> statistics = service.getDauStatistics(Arrays.asList(10, 11, 12));
        assertEquals(Long.valueOf(2), statistics.get(10));
        assertEquals(Long.valueOf(1), statistics.get(11));
        assertEquals(Long.valueOf(0), statistics.get(12));

        service.postEvent(new Event(4, 10));
        assertEquals(Long.valueOf(2), service.getAuthorDauStatistics(10));
    }

    @Test
    void shouldResetStatisticsAfterGap() {
        DauService service = new DauService();
        service.postEvent(new Event(1, 10));
        service.postEvent(new Event(2, 10));

        service.day = LocalDate.now().minusDays(2);
        assertEquals(Long.valueOf(0), service.getAuthorDauStatistics(10));
        assertEquals(Long.valueOf(0), service.getAuthorDauStatistics(11));
    }

    @Test
    void shouldWorkWithConcurrentPosts() {
        DauService service = new DauService();
        ExecutorService pool = Executors.newFixedThreadPool(4);
        List<Callable<Integer>> tasks = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            int userId = i % 25;
            tasks.add(() -> {
                service.postEvent(new Event(userId, 7));
                return 1;
            });
        }

        try {
            List<Future<Integer>> results = pool.invokeAll(tasks);
            for (Future<Integer> future : results) {
                try { future.get(); }
                catch (ExecutionException e) { throw new RuntimeException(e.getCause()); }
                catch (CancellationException e) { throw new RuntimeException(e); }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            pool.shutdown();
            try {
                if (!pool.awaitTermination(5, TimeUnit.SECONDS)) pool.shutdownNow();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }

        service.day = LocalDate.now().minusDays(1);
        assertEquals(Long.valueOf(25), service.getAuthorDauStatistics(7));
    }
}