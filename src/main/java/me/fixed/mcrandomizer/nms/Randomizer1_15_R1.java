package me.fixed.mcrandomizer.nms;

import me.fixed.mcrandomizer.SectionedHashMap;
import net.minecraft.server.v1_15_R1.LootTable;
import net.minecraft.server.v1_15_R1.LootTableRegistry;
import net.minecraft.server.v1_15_R1.MinecraftKey;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_15_R1.CraftServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Randomizer1_15_R1 implements Randomizer {
    private final static Field LOOT_TABLE_MAP_FIELD; // not null due to static assertion

    static {
        LOOT_TABLE_MAP_FIELD = lootTableMapField();
        assert LOOT_TABLE_MAP_FIELD != null;
    }

    private static Field lootTableMapField() {
        try {
            return LootTableRegistry.class.getDeclaredField("c");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }
    }

    @NotNull private final Logger logger;
    @Nullable private SectionedHashMap<MinecraftKey, LootTable> injectedMap;

    public Randomizer1_15_R1(@NotNull Logger logger) {
        this.logger = logger;
        this.injectedMap = null;
        injectTable();
    }

    @Override public @NotNull Logger getLogger() {
        return logger;
    }

    private void injectTable() {
        logger.log(Level.INFO, "Injecting custom table");
        LootTableRegistry registry = ((CraftServer) Bukkit.getServer()).getServer().getLootTableRegistry();
        try {
            LOOT_TABLE_MAP_FIELD.setAccessible(true);
            @SuppressWarnings("unchecked") Map<MinecraftKey, LootTable> c = (Map<MinecraftKey, LootTable>) LOOT_TABLE_MAP_FIELD.get(registry);
            injectedMap = new SectionedHashMap<>(logger, c, (key -> key.getKey().split("/")[0]));
            LOOT_TABLE_MAP_FIELD.set(registry, injectedMap);
            LOOT_TABLE_MAP_FIELD.setAccessible(false);
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
        logger.log(Level.INFO, "Custom table injected");
    }

    @Override public void randomizeBlockLootTable0() {
        if (injectedMap == null) throw new IllegalStateException("Injected table not yet registered.");
        injectedMap.shuffleSection(Section.BLOCKS.key);
    }

    @Override public void randomizeEntityLootTable0() {
        if (injectedMap == null) throw new IllegalStateException("Injected table not yet registered.");
        injectedMap.shuffleSection(Section.ENTITIES.key);
    }

    @Override public void randomizeChestLootTable0() {
        if (injectedMap == null) throw new IllegalStateException("Injected table not yet registered.");
        injectedMap.shuffleSection(Section.CHESTS.key);
    }

    @Override public void loadSnapshot(@NotNull Map<String, Map<String, Integer>> snapshot) {
        if (injectedMap == null) throw new IllegalStateException("Injected table not yet registered.");
        injectedMap.loadSnapshot(snapshot, MinecraftKey::new);
    }

    @Override public @NotNull Map<String, Map<String, Integer>> generateSnapshot() {
        if (injectedMap == null) throw new IllegalStateException("Injected table not yet registered.");
        return injectedMap.snapshot(MinecraftKey::getKey);
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
