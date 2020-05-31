package me.fixed.mcrandomizer.nms;

import me.fixed.mcrandomizer.SectionedHashMap;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;

public abstract class MapInjectorRandomizer<K, V> implements Randomizer {
    @NotNull private final Logger logger;
    @NotNull private final Function<String, K> stringKFunction;
    @NotNull private final Function<K, String> kStringFunction;
    @NotNull private final SectionedHashMap<K, V> table;


    public MapInjectorRandomizer(
            @NotNull Logger logger,
            @NotNull Function<String, K> stringKFunction,
            @NotNull Function<K, String> kStringFunction
    ) {
        this.logger = logger;
        this.stringKFunction = stringKFunction;
        this.kStringFunction = kStringFunction;
        this.table = injectTable();
    }

    @Override public @NotNull Logger getLogger() {
        return logger;
    }

    protected abstract @NotNull SectionedHashMap<K, V> injectTable();

    @Override public void randomizeBlockLootTable0() {
        if (!blockLootTableSupported()) throw new UnsupportedOperationException("Block loot table not supported.");
        table.shuffleSection(Section.BLOCKS.key);
    }

    @Override public void randomizeEntityLootTable0() {
        if (!entityLootTableSupported()) throw new UnsupportedOperationException("Entity loot table not supported.");
        table.shuffleSection(Section.ENTITIES.key);
    }

    @Override public void randomizeChestLootTable0() {
        if (!chestLootTableSupported()) throw new UnsupportedOperationException("Chest loot table not supported.");
        table.shuffleSection(Section.CHESTS.key);
    }

    @Override public void loadSnapshot(@NotNull Map<String, Map<String, Integer>> snapshot) {
        table.loadSnapshot(snapshot, stringKFunction);
    }

    @Override public @NotNull Map<String, Map<String, Integer>> generateSnapshot() {
        return table.snapshot(kStringFunction);
    }

    private enum Section {
        ENTITIES("entities"),
        BLOCKS("blocks"),
        CHESTS("chests");

        @NotNull private final String key;

        Section(@NotNull String key) {
            this.key = key;
        }
    }
}
