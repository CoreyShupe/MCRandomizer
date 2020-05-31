package me.fixed.mcrandomizer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SectionedHashMap<K, V> extends HashMap<K, V> {
    @NotNull private final Logger logger;
    @NotNull private final Map<String, Pair<List<K>, List<V>>> sectionedMap;
    @NotNull private final Function<K, String> sectionGetter;

    public SectionedHashMap(@NotNull Logger logger, @NotNull Map<K, V> backingMap, @NotNull Function<K, @NotNull String> sectionGetter) {
        super(backingMap);
        this.logger = logger;
        this.sectionedMap = new HashMap<>();
        this.sectionGetter = sectionGetter;
        computeSections();
    }

    public @NotNull Map<String, Map<String, Integer>> snapshot(@NotNull Function<K, String> kToStringFunction) {
        Map<String, Map<String, Integer>> map = new HashMap<>(sectionedMap.size());
        sectionedMap.forEach((section, pair) -> {
            List<K> k = pair.getLeft();
            Map<String, Integer> stringIntegerMap = new HashMap<>(k.size());
            for (int i = 0; i < k.size(); i++) {
                stringIntegerMap.put(kToStringFunction.apply(k.get(i)), i);
            }
            map.put(section, stringIntegerMap);
        });
        return map;
    }

    public void loadSnapshot(@NotNull Map<String, Map<String, Integer>> snapshot, @NotNull Function<String, K> stringKFunction) {
        snapshot.forEach((section, map) -> {
            if (sectionedMap.containsKey(section)) {
                List<V> right = sectionedMap.get(section).getRight();
                map.forEach((kString, i) -> replace(stringKFunction.apply(kString), right.get(i)));
            }
        });
    }

    public void shuffleSection(String section) {
        Pair<List<K>, List<V>> pair = sectionedMap.get(section);
        List<K> k = pair.getLeft();
        List<V> v = pair.getRight();
        Collections.shuffle(k);
        for (int i = 0; i < k.size(); i++) {
            replace(k.get(i), v.get(i));
        }
    }

    private void computeSections() {
        logger.log(Level.INFO, "Computing custom sections...");
        sectionedMap.clear();
        forEach((k, v) -> addToSection(sectionGetter.apply(k), k, v));
    }

    private void addSection(@NotNull String section) {
        logger.log(Level.INFO, "ADDING SECTION: [" + section + "]");
        sectionedMap.put(section, new Pair<>(new ArrayList<>(), new ArrayList<>()));
    }

    private void addToSection(@NotNull String section, @NotNull K k, @NotNull V v) {
        if (!sectionedMap.containsKey(section)) {
            addSection(section);
        }
        Pair<List<K>, List<V>> pair = sectionedMap.get(section);
        pair.getLeft().add(k);
        pair.getRight().add(v);
    }

    @Override public @Nullable V put(K k, V v) {
        boolean alterMap = !containsKey(k);
        V got = super.put(k, v);
        if (alterMap) {
            computeSections();
        }
        return got;
    }

    @Override public V putIfAbsent(K k, V v) {
        boolean alterMap = !containsKey(k);
        V got = super.putIfAbsent(k, v);
        if (alterMap) {
            computeSections();
        }
        return got;
    }
}
