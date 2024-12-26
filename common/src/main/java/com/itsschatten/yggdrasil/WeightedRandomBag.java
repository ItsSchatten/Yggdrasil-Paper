package com.itsschatten.yggdrasil;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Weighted randomness.
 *
 * @param <E> The type in this bag.
 */
public class WeightedRandomBag<E> {

    // Proper map used to gather a random object.
    private final NavigableMap<Double, E> map;

    // Backup map containing all objects keyed to their weight, allowing us to get ALL objects with the same weights.
    private final Multimap<Double, E> backupMap;

    // Our random instance, this is generated on initialization of this class.
    private final Random random;

    // The total sum of ALL weights currently in this bag.
    // This can be used to gather the percentage chance of an item.
    @Getter
    private double total = 0;

    /**
     * Basic implementation that uses {@link ThreadLocalRandom#current()} as its Random instance.
     */
    public WeightedRandomBag() {
        this(ThreadLocalRandom.current());
    }

    /**
     * Creates a new bag with the provided Random instance.
     *
     * @param random The Random instance to use.
     */
    public WeightedRandomBag(Random random) {
        this.random = random;

        this.map = new TreeMap<>();
        this.backupMap = ArrayListMultimap.create();
    }

    /**
     * Adds an object to this bag.
     *
     * @param weight The weight of this object.
     * @param result The object.
     * @return Returns this bag with the added result.
     */
    public final WeightedRandomBag<E> add(double weight, E result) {
        if (weight <= 0) {
            return this;
        }
        total += weight;
        map.put(total, result);
        backupMap.put(weight, result);
        return this;
    }

    /**
     * Adds a map of {@link E} == Weight to this bag.
     *
     * @param map The flipped map to add.
     * @return Returns this weighted bag.
     */
    public final WeightedRandomBag<E> addFlippedMap(final @NotNull Map<E, Double> map) {
        map.forEach((mat, chance) -> this.add(chance, mat));
        return this;
    }

    /**
     * Adds a map of Weights == {@link E} to this bag.
     *
     * @param map The map to add.
     * @return Returns this weighted bag.
     */
    public final WeightedRandomBag<E> addMap(final @NotNull Map<Double, E> map) {
        map.forEach(this::add);
        return this;
    }

    /**
     * Adds a {@link Multimap} to this bag.
     *
     * @param map The map to add.
     * @return This weighted back.
     */
    public final WeightedRandomBag<E> addMultiMap(final @NotNull Multimap<Double, E> map) {
        map.forEach(this::add);
        return this;
    }

    /**
     * Gets a nullable entry from {@link #map}
     *
     * @return Either <code>null</code> or an object from the map.
     */
    @Nullable
    public final E next() {
        double value = random.nextDouble() * total;
        if (map.higherEntry(value) == null) {
            return null;
        }
        return map.higherEntry(value).getValue();
    }

    /**
     * Gets a nullable entry from {@link #map} based ont he provided filter.
     *
     * @param filter Filter options for this map.
     * @return Either <code>null</code> or an object from the map.
     */
    @Nullable
    public final E next(Predicate<? super Map.Entry<Double, E>> filter) {
        double value = random.nextDouble() * total;
        final NavigableMap<Double, E> filtered = new TreeMap<>(map.entrySet().stream().filter(filter).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        if (filtered.higherEntry(value) == null) {
            return null;
        }
        return filtered.higherEntry(value).getValue();
    }

    /**
     * Check if the main map contains an object.
     *
     * @param object The object to check.
     * @return Returns whatever {@link NavigableMap#containsValue(Object)} returns
     */
    public final boolean contains(E object) {
        return map.containsValue(object);
    }

    /**
     * Returns a Map Entry set of all objects in the main map.
     *
     * @return {@link NavigableMap#entrySet()}
     */
    @Contract(pure = true)
    public final @NotNull Set<Map.Entry<Double, E>> getEntries() {
        return map.entrySet();
    }

    /**
     * Gets the main map with weighted values and returns it as an unmodifiable map.
     *
     * @return Returns an unmodifiable map of the main map.
     */
    @Contract(pure = true)
    @Unmodifiable
    public final @NotNull Map<Double, E> getMap() {
        return Collections.unmodifiableMap(map);
    }

    /**
     * Gets the map with raw odd values and returns it as an unmodifiable map.
     *
     * @return Returns an unmodifiable map of the backup map.
     */
    @Unmodifiable
    public final @NotNull Map<Double, Collection<E>> getBackupMap() {
        return Collections.unmodifiableMap(backupMap.asMap());
    }

    /**
     * Utility method to check if the main map is empty.
     *
     * @return If the main map is empty or not.
     */
    public final boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * Serialize the backup map into a string.
     *
     * @return Returns a String of all the objects in this bag.
     */
    public final @NotNull String toRawOdds() {
        final StringBuilder builder = new StringBuilder();
        for (Map.Entry<Double, Collection<E>> entry : backupMap.asMap().entrySet()) {
            for (E result : entry.getValue()) {
                builder.append(result.toString()).append(':')
                        // Uses Lang3 to strip the end '.0'.
                        .append(StringUtils.stripEnd(entry.getKey().toString(), ".0")).append(',');
            }
        }

        final int lastOccurrence = builder.toString().lastIndexOf(",");
        // Should remove the trailing ','
        return builder.substring(0, lastOccurrence);
    }

    @Override
    public String toString() {
        return "WeightedRandomBag{" +
                "map=" + map +
                ", random=" + random +
                ", total=" + total +
                '}';
    }

}