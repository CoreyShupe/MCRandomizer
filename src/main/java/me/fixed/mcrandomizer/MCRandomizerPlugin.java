package me.fixed.mcrandomizer;

import me.fixed.mcrandomizer.nms.Randomizer;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.logging.Level;

public class MCRandomizerPlugin extends JavaPlugin {
    @NotNull private final File snapshotFile = new File(getDataFolder(), "snapshot.bin");
    @Nullable private Randomizer randomizer;

    @Override public void onEnable() {
        saveDefaultConfig();
        randomizer = Randomizer.loadRandomizer(this);
        if (getConfig().getBoolean("prioritize-snapshot", false) && snapshotFile.exists()) {
            randomizer.loadSnapshot(snapshotFile);
        } else {
            if (getConfig().getBoolean("randomize-block-drops", false) && randomizer.blockLootTableSupported()) {
                randomizer.randomizeBlockLootTable();
            }
            if (getConfig().getBoolean("randomize-entity-drops", false) && randomizer.entityLootTableSupported()) {
                randomizer.randomizeEntityLootTable();
            }
            if (getConfig().getBoolean("randomize-chest-loot", false) && randomizer.chestLootTableSupported()) {
                randomizer.randomizeChestLootTable();
            }
        }
        PluginCommand command = getCommand("randomizer");
        assert command != null;
        RandomizerCommand randomizerCommand = new RandomizerCommand(this);
        command.setExecutor(randomizerCommand);
        command.setTabCompleter(randomizerCommand);
    }

    @Override public void onDisable() {
        HandlerList.unregisterAll(this);
    }

    public void saveSnapshot() {
        if (randomizer == null) throw new IllegalStateException("Snapshot saved before randomizer initialized.");
        if (!getDataFolder().exists() && !getDataFolder().mkdirs()) {
            getLogger().log(Level.SEVERE, "Failed to save data folder.");
            return;
        }
        randomizer.saveSnapshot(snapshotFile);
    }

    public @NotNull Randomizer getRandomizer() {
        if (randomizer == null) throw new IllegalStateException("Randomizer accessed before initialized.");
        return randomizer;
    }
}
