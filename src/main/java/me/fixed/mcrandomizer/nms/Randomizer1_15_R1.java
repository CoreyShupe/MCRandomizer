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

    @Override public void randomizeBlockLootTable() {
        if (injectedMap == null) throw new IllegalStateException("Injected table not yet registered.");
        logger.log(Level.INFO, "Randomizing block loot table...");
        injectedMap.shuffleSection(Section.BLOCKS.key);
        logger.log(Level.INFO, "Block loot table randomized.");
    }

    @Override public void randomizeEntityLootTable() {
        if (injectedMap == null) throw new IllegalStateException("Injected table not yet registered.");
        logger.log(Level.INFO, "Randomizing entity loot table...");
        injectedMap.shuffleSection(Section.ENTITIES.key);
        logger.log(Level.INFO, "Entity loot table randomized.");
    }

    @Override public void randomizeChestLootTable() {
        if (injectedMap == null) throw new IllegalStateException("Injected table not yet registered.");
        logger.log(Level.INFO, "Randomizing chest loot table...");
        injectedMap.shuffleSection(Section.CHESTS.key);
        logger.log(Level.INFO, "Chest loot table randomized.");
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
