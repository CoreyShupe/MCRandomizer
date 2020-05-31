package me.fixed.mcrandomizer.nms;

import me.fixed.mcrandomizer.SnapshotSerializable;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public interface Randomizer {
    @NotNull Logger getLogger();

    boolean blockLootTableSupported();

    default void randomizeBlockLootTable() {
        info("Randomizing block loot table...");
        randomizeBlockLootTable0();
        info("Block loot table randomized.");
    }

    void randomizeBlockLootTable0();

    boolean entityLootTableSupported();

    default void randomizeEntityLootTable() {
        info("Randomizing entity loot table...");
        randomizeEntityLootTable0();
        info("Entity loot table randomized.");
    }

    void randomizeEntityLootTable0();

    boolean chestLootTableSupported();

    default void randomizeChestLootTable() {
        info("Randomizing chest loot table...");
        randomizeChestLootTable0();
        info("Chest loot table randomized.");
    }

    void randomizeChestLootTable0();

    default void loadSnapshot(@NotNull File file) {
        info("Loading loot table snapshot.");
        try (FileInputStream fileStream = new FileInputStream(file)) {
            try (ObjectInputStream objectStream = new ObjectInputStream(fileStream)) {
                SnapshotSerializable snapshotSerializable = (SnapshotSerializable) objectStream.readObject();
                loadSnapshot(snapshotSerializable.getSnapshot());
            }
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        info("Loot table snapshot loaded.");
    }

    void loadSnapshot(@NotNull Map<String, Map<String, Integer>> snapshot);

    default void saveSnapshot(@NotNull File file) {
        info("Generating a snapshot of current loot table.");
        try (FileOutputStream fileStream = new FileOutputStream(file)) {
            try (ObjectOutputStream objectStream = new ObjectOutputStream(fileStream)) {
                objectStream.writeObject(new SnapshotSerializable(generateSnapshot()));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        info("Snapshot generated.");
    }

    @NotNull Map<String, Map<String, Integer>> generateSnapshot();

    default void info(@NotNull String message) {
        getLogger().log(Level.INFO, message);
    }

    static @NotNull Randomizer loadRandomizer(@NotNull JavaPlugin plugin) {
        String version = plugin.getServer().getClass().getPackage().getName().split("\\.")[3];
        switch (version) {
            case "v1_13_R1":
                return new Randomizer1_13_R1(plugin, plugin.getLogger());
            case "v1_13_R2":
                return new Randomizer1_13_R2(plugin, plugin.getLogger());
            case "v1_14_R1":
                return new Randomizer1_14_R1(plugin.getLogger());
            case "v1_15_R1":
                return new Randomizer1_15_R1(plugin.getLogger());
            default:
                throw new UnsupportedOperationException("Version " + version + " unsupported");
        }
    }
}
