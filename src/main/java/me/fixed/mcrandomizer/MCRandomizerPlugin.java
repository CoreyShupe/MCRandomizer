package me.fixed.mcrandomizer;

import me.fixed.mcrandomizer.nms.Randomizer;
import org.bukkit.plugin.java.JavaPlugin;

public class MCRandomizerPlugin extends JavaPlugin {
    @Override public void onEnable() {
        saveDefaultConfig();
        Randomizer randomizer = Randomizer.loadRandomizer(getLogger(), getServer());
        if (getConfig().getBoolean("randomize-block-drops", false)) {
            randomizer.randomizeBlockLootTable();
        }
        if (getConfig().getBoolean("randomize-entity-drops", false)) {
            randomizer.randomizeEntityLootTable();
        }
        if (getConfig().getBoolean("randomize-chest-loot", false)) {
            randomizer.randomizeChestLootTable();
        }
    }
}
