package ru.kubsu.borshchevyk.sync.domain.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Value Object representing a Vector Clock for causal ordering.
 * Immutable class.
 */
public final class VectorClock {

    private final Map<String, Long> clocks;

    public VectorClock() {
        this.clocks = Collections.emptyMap();
    }

    @com.fasterxml.jackson.annotation.JsonCreator(mode = com.fasterxml.jackson.annotation.JsonCreator.Mode.DELEGATING)
    public VectorClock(Map<String, ?> clocks) {
        if (clocks == null) {
            this.clocks = Collections.emptyMap();
            return;
        }

        Map<String, ?> rawClocks;
        if (clocks.containsKey("clocks") && clocks.get("clocks") instanceof Map) {
            rawClocks = (Map<String, ?>) clocks.get("clocks");
        } else {
            rawClocks = clocks;
        }

        Map<String, Long> converted = new HashMap<>();
        for (Map.Entry<String, ?> entry : rawClocks.entrySet()) {
            Object val = entry.getValue();
            if (val != null) {
                long longVal;
                if (val instanceof Number) {
                    longVal = ((Number) val).longValue();
                } else {
                    try {
                        longVal = Long.parseLong(val.toString().trim());
                    } catch (NumberFormatException e) {
                        continue;
                    }
                }
                converted.put(entry.getKey(), longVal);
            }
        }
        this.clocks = Collections.unmodifiableMap(converted);
    }

    /**
     * Increments the clock for the given nodeId.
     *
     * @param nodeId the node identifier
     * @return a new VectorClock instance with the incremented value
     */
    public VectorClock increment(String nodeId) {
        Map<String, Long> newClocks = new HashMap<>(this.clocks);
        newClocks.put(nodeId, newClocks.getOrDefault(nodeId, 0L) + 1L);
        return new VectorClock(newClocks);
    }

    /**
     * Merges this vector clock with another, taking the maximum of each node's clock.
     *
     * @param other the other vector clock
     * @return a new merged VectorClock instance
     */
    public VectorClock merge(VectorClock other) {
        if (other == null || other.clocks.isEmpty()) {
            return this;
        }

        Map<String, Long> merged = new HashMap<>(this.clocks);
        for (Map.Entry<String, Long> entry : other.clocks.entrySet()) {
            merged.merge(entry.getKey(), entry.getValue(), Math::max);
        }
        return new VectorClock(merged);
    }

    /**
     * Checks if this vector clock is strictly before the other.
     * This <= other AND this != other.
     *
     * @param other the other vector clock
     * @return true if this happens before the other
     */
    public boolean isBefore(VectorClock other) {
        if (other == null) {
            return false;
        }
        return isLessOrEqual(this, other) && !isLessOrEqual(other, this);
    }

    /**
     * Checks if this vector clock is strictly after the other.
     *
     * @param other the other vector clock
     * @return true if this happens after the other
     */
    public boolean isAfter(VectorClock other) {
        if (other == null) {
            return true;
        }
        return other.isBefore(this);
    }

    /**
     * Checks if this vector clock and the other are concurrent.
     * Neither happens before the other.
     *
     * @param other the other vector clock
     * @return true if they are concurrent
     */
    public boolean isConcurrent(VectorClock other) {
        if (other == null) {
            return false; // Or true depending on definition, but usually null is just "empty", which means this > empty, so not concurrent.
        }
        boolean thisLeqOther = isLessOrEqual(this, other);
        boolean otherLeqThis = isLessOrEqual(other, this);
        return !thisLeqOther && !otherLeqThis;
    }
    
    /**
     * Helper to check if a <= b.
     */
    private boolean isLessOrEqual(VectorClock a, VectorClock b) {
        Set<String> allKeys = Stream.concat(a.clocks.keySet().stream(), b.clocks.keySet().stream())
                .collect(Collectors.toSet());

        for (String key : allKeys) {
            long valA = a.clocks.getOrDefault(key, 0L);
            long valB = b.clocks.getOrDefault(key, 0L);
            if (valA > valB) {
                return false;
            }
        }
        return true;
    }

    public Map<String, Long> getClocks() {
        return clocks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VectorClock that = (VectorClock) o;
        
        // Ensure default 0s don't break equality (e.g., {"A":1} == {"A":1, "B":0})
        Set<String> allKeys = Stream.concat(clocks.keySet().stream(), that.clocks.keySet().stream())
                .collect(Collectors.toSet());
                
        for (String key : allKeys) {
            long valThis = clocks.getOrDefault(key, 0L);
            long valThat = that.clocks.getOrDefault(key, 0L);
            if (valThis != valThat) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        // Compute a consistent hash that ignores 0 values.
        int result = 0;
        for (Map.Entry<String, Long> entry : clocks.entrySet()) {
            if (entry.getValue() > 0) {
                result += Objects.hash(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "VectorClock" + clocks;
    }
}
