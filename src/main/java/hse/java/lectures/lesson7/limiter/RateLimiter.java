package hse.java.lectures.lesson7.limiter;

import java.time.temporal.ChronoUnit;

/**
 * Скользящий рейтлимитер: не больше заданного числа успешных {@link #check()} за последнюю секунду или минуту.
 */
public class RateLimiter {

    /**
     * @param unit        длина окна — только {@link ChronoUnit#SECONDS} или {@link ChronoUnit#MINUTES}
     *                    (скользящее окно 1 секунда или 1 минута)
     * @param maxRequests максимум успешных {@link #check()} за окно (должно быть > 0)
     */

    long[] times;
    long window;
    int maxRequests, head, size;

    public RateLimiter(ChronoUnit unit, int maxRequests) {
        if (maxRequests <= 0) throw new IllegalArgumentException();
        if (unit != ChronoUnit.SECONDS && unit != ChronoUnit.MINUTES) throw new IllegalArgumentException();
        this.maxRequests = maxRequests;
        times = new long[maxRequests];
        window = unit == ChronoUnit.MINUTES ? 60000L : 1000L;
    }

    /**
     * Регистрирует попытку и возвращает, разрешена ли она в пределах лимита.
     */
    public boolean check() {
        long now = System.currentTimeMillis();
        while (size > 0 && now - times[head] >= window) {
            if (++head == maxRequests) head = 0;
            size -= 1;
        }

        if (size == maxRequests) return false;

        int position = head + size;
        if (position >= maxRequests) position -= maxRequests;
        times[position] = now;
        size += 1;
        return true;
    }

}
