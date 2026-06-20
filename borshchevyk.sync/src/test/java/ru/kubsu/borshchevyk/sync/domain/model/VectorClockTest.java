package ru.kubsu.borshchevyk.sync.domain.model;

import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class VectorClockTest {

    @Test
    void testIncrement() {
        VectorClock clock = new VectorClock();
        clock = clock.increment("A");
        assertEquals(1L, clock.getClocks().get("A"));

        clock = clock.increment("A");
        assertEquals(2L, clock.getClocks().get("A"));

        clock = clock.increment("B");
        assertEquals(2L, clock.getClocks().get("A"));
        assertEquals(1L, clock.getClocks().get("B"));
    }

    @Test
    void testMerge() {
        VectorClock clock1 = new VectorClock(Map.of("A", 2L, "B", 1L));
        VectorClock clock2 = new VectorClock(Map.of("A", 1L, "B", 3L, "C", 1L));

        VectorClock merged = clock1.merge(clock2);
        assertEquals(2L, merged.getClocks().get("A"));
        assertEquals(3L, merged.getClocks().get("B"));
        assertEquals(1L, merged.getClocks().get("C"));
    }

    @Test
    void testIsBefore() {
        VectorClock clock1 = new VectorClock(Map.of("A", 1L, "B", 1L));
        VectorClock clock2 = new VectorClock(Map.of("A", 1L, "B", 2L));

        assertTrue(clock1.isBefore(clock2));
        assertFalse(clock2.isBefore(clock1));
        assertFalse(clock1.isBefore(clock1));
    }

    @Test
    void testIsAfter() {
        VectorClock clock1 = new VectorClock(Map.of("A", 1L, "B", 2L));
        VectorClock clock2 = new VectorClock(Map.of("A", 1L, "B", 1L));

        assertTrue(clock1.isAfter(clock2));
        assertFalse(clock2.isAfter(clock1));
        assertFalse(clock1.isAfter(clock1));
    }

    @Test
    void testIsConcurrent() {
        VectorClock clock1 = new VectorClock(Map.of("A", 2L, "B", 1L));
        VectorClock clock2 = new VectorClock(Map.of("A", 1L, "B", 2L));

        assertTrue(clock1.isConcurrent(clock2));
        assertTrue(clock2.isConcurrent(clock1));

        VectorClock clock3 = new VectorClock(Map.of("A", 2L, "B", 2L));
        assertFalse(clock1.isConcurrent(clock3)); // clock1 is strictly before clock3
    }

    @Test
    void testEqualsAndHashCode() {
        VectorClock clock1 = new VectorClock(Map.of("A", 1L));
        VectorClock clock2 = new VectorClock(Map.of("A", 1L, "B", 0L));

        assertEquals(clock1, clock2);
        assertEquals(clock1.hashCode(), clock2.hashCode());
    }
}
