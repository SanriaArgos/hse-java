package hse.java.lectures.lesson7.dau;

import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class DauService {
    LocalDate day = LocalDate.now();
    Map<Integer, Set<Integer>> usersByAuthor = new HashMap<>();
    Map<Integer, Long> previousDayStatistics = new HashMap<>();

    void updateDay() {
        LocalDate today = LocalDate.now();
        if (today.equals(day)) return;

        if (today.equals(day.plusDays(1))) {
            previousDayStatistics = new HashMap<>();
            for (Map.Entry<Integer, Set<Integer>> entry : usersByAuthor.entrySet()) {
                previousDayStatistics.put(entry.getKey(), (long) entry.getValue().size());
            }
        } else previousDayStatistics = new HashMap<>();

        usersByAuthor = new HashMap<>();
        day = today;
    }

    public synchronized void postEvent(Event event) {
        if (event == null) throw new IllegalArgumentException();

        updateDay();
        Set<Integer> users = usersByAuthor.get(event.authorId());
        if (users == null) {
            users = new HashSet<>();
            usersByAuthor.put(event.authorId(), users);
        }

        users.add(event.userId());
    }


    public synchronized Map<Integer, Long> getDauStatistics(List<Integer> authorIds) {
        if (authorIds == null) throw new IllegalArgumentException();

        updateDay();
        Map<Integer, Long> result = new HashMap<>();
        for (Integer authorId : authorIds) {
            Long value = previousDayStatistics.get(authorId);
            if (value == null) value = 0L;
            result.put(authorId, value);
        }
        return result;
    }

    public synchronized Long getAuthorDauStatistics(int authorId) {
        updateDay();
        Long value = previousDayStatistics.get(authorId);

        if (value == null) return 0L;
        return value;
    }
}
