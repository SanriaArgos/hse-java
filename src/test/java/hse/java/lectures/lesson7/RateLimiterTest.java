package hse.java.lectures.lesson7.limiter;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import java.time.temporal.ChronoUnit;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("limiter")
public class RateLimiterTest {
    @Test
    void badArgs() {
        assertThrows(IllegalArgumentException.class, () -> new RateLimiter(null, 1));
        assertThrows(IllegalArgumentException.class, () -> new RateLimiter(ChronoUnit.HOURS, 1));
        assertThrows(IllegalArgumentException.class, () -> new RateLimiter(ChronoUnit.SECONDS, 0));
        assertThrows(IllegalArgumentException.class, () -> new RateLimiter(ChronoUnit.MINUTES, -1));
    }

    @Test
    void floatingSecond() throws Exception {
        RateLimiter limiter = new RateLimiter(ChronoUnit.SECONDS, 2);
        assertTrue(limiter.check());
        Thread.sleep(700);
        assertTrue(limiter.check());
        Thread.sleep(400);
        assertTrue(limiter.check());
        assertFalse(limiter.check());
    }

}