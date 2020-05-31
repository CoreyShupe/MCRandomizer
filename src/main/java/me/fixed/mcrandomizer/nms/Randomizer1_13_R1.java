package me.fixed.mcrandomizer.nms;

import me.fixed.mcrandomizer.SectionedHashMap;
import net.minecraft.server.v1_13_R1.LootTable;
import net.minecraft.server.v1_13_R1.LootTableRegistry;
import net.minecraft.server.v1_13_R1.MinecraftKey;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_13_R1.CraftServer;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Randomizer1_13_R1 extends ExtendedListenerRandomizer<MinecraftKey, LootTable> {
    private final static Field LOOT_TABLE_MAP_FIELD; // not null due to static assertion

    static {
        LOOT_TABLE_MAP_FIELD = lootTableMapField();
        assert LOOT_TABLE_MAP_FIELD != null;
    }

    private static @Nullable Field lootTableMapField() {
        try {
            return LootTableRegistry.class.getDeclaredField("e");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Randomizer1_13_R1(@NotNull JavaPlugin plugin, @NotNull Logger logger) {
        super(plugin, logger, MinecraftKey::new, MinecraftKey::getKey);
    }

    @Override public @NotNull SectionedHashMap<MinecraftKey, LootTable> injectTable() {
        getLogger().log(Level.INFO, "Injecting custom table");
        LootTableRegistry registry = ((CraftServer) Bukkit.getServer()).getServer().aP();
        try {
            LOOT_TABLE_MAP_FIELD.setAccessible(true);
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(LOOT_TABLE_MAP_FIELD, LOOT_TABLE_MAP_FIELD.getModifiers() & ~Modifier.FINAL);
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

    @Override public boolean entityLootTableSupported() {
        return true;
    }

    @Override public boolean chestLootTableSupported() {
        return true;
    }
}
