package me.fixed.mcrandomizer.nms;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ExtendedListenerRandomizer<K, V> extends MapInjectorRandomizer<K, V> implements Listener {
    @NotNull private final Map<Material, Material> materialMapper;
    @NotNull private final Map<Material, Integer> materialLocationMapper;

    public ExtendedListenerRandomizer(
            @NotNull JavaPlugin plugin,
            @NotNull Logger logger,
            @NotNull Function<String, K> stringKFunction,
            @NotNull Function<K, String> kStringFunction
    ) {
        super(logger, stringKFunction, kStringFunction);
        this.materialMapper = new IdentityHashMap<>();
        this.materialLocationMapper = new IdentityHashMap<>();
        Material[] values = Material.values();
        for (int i = 0; i < values.length; i++) {
            Material material = values[i];
            String name = material.name();
            if (name.contains("BUCKET") ||
                    name.contains("LAVA") ||
                    name.contains("WATER") ||
                    material.isEdible() ||
                    !material.isBlock() ||
                    name.contains("_AIR") ||
                    name.equals("AIR") ||
                    name.equals("KELP")
            ) {
                continue; // skip illegal materials
            }
            materialMapper.put(material, material);
            materialLocationMapper.put(material, i);
        }
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override public boolean blockLootTableSupported() {
        return true;
    }

    @Override public void randomizeBlockLootTable0() {
        SplittableRandom random = new SplittableRandom(System.nanoTime());
        Set<Material> keys = materialMapper.keySet();
        List<Material> values = new ArrayList<>(materialMapper.values());
        keys.forEach(key -> {
            int i = random.nextInt(values.size());
            materialMapper.replace(key, values.get(i));
            values.remove(i);
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true) public void onBreak(BlockBreakEvent event) {
        Material type = event.getBlock().getType();
        if (materialMapper.containsKey(type)) {
            event.getBlock().setType(materialMapper.get(type), false);
        }
    }

    @Override public @NotNull Map<String, Map<String, Integer>> generateSnapshot() {
        Map<String, Integer> currentValueMap = new HashMap<>();
        materialMapper.forEach((material, material2) -> currentValueMap.put(material.name(), materialLocationMapper.get(material2)));
        Map<String, Map<String, Integer>> registrySnapshot = super.generateSnapshot();
        registrySnapshot.put("blocks", currentValueMap);
        return registrySnapshot;
    }

    @Override public void loadSnapshot(@NotNull Map<String, Map<String, Integer>> snapshot) {
        super.loadSnapshot(snapshot);
        Material[] values = Material.values();
        snapshot.get("blocks").forEach((name, location) -> materialMapper.replace(Material.valueOf(name), values[location]));
    }
}
