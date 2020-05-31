package me.fixed.mcrandomizer.nms;

import me.fixed.mcrandomizer.SectionedHashMap;
import net.minecraft.server.v1_14_R1.LootTable;
import net.minecraft.server.v1_14_R1.LootTableRegistry;
import net.minecraft.server.v1_14_R1.MinecraftKey;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_14_R1.CraftServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Randomizer1_14_R1 extends MapInjectorRandomizer<MinecraftKey, LootTable> {
    private final static Field LOOT_TABLE_MAP_FIELD; // not null due to static assertion

    static {
        LOOT_TABLE_MAP_FIELD = lootTableMapField();
        assert LOOT_TABLE_MAP_FIELD != null;
    }

    private static @Nullable Field lootTableMapField() {
        try {
            return LootTableRegistry.class.getDeclaredField("c");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Randomizer1_14_R1(@NotNull Logger logger) {
        super(logger, MinecraftKey::new, MinecraftKey::getKey);
    }

    @Override public @NotNull SectionedHashMap<MinecraftKey, LootTable> injectTable() {
        getLogger().log(Level.INFO, "Injecting custom table");
        LootTableRegistry registry = ((CraftServer) Bukkit.getServer()).getServer().getLootTableRegistry();
        try {
            LOOT_TABLE_MAP_FIELD.setAccessible(true);
            @SuppressWarnings("unchecked") Map<MinecraftKey, LootTable> c = (Map<MinecraftKey, LootTable>) LOOT_TABLE_MAP_FIELD.get(registry);
            SectionedHashMap<MinecraftKey, LootTable> map = new SectionedHashMap<>(getLogger(), c, key -> key.getKey().split("/")[0]);
            LOOT_TABLE_MAP_FIELD.set(registry, map);
            LOOT_TABLE_MAP_FIELD.setAccessible(false);
            getLogger().log(Level.INFO, "Custom table injected");
            return map;
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
            getLogger().log(Level.SEVERE, "Failed to inject table, randomization will not work.");
            return new SectionedHashMap<>(getLogger(), new HashMap<>(), MinecraftKey::getKey);
        }
    }

    @Override public boolean blockLootTableSupported() {
        return true;
    }

    @Override public boolean entityLootTableSupported() {
        return true;
    }

    @Override public boolean chestLootTableSupported() {
        return true;
    }
}
