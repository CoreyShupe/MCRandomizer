package me.fixed.mcrandomizer.nms;

import me.fixed.mcrandomizer.SnapshotSerializable;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public interface Randomizer {
    @NotNull Logger getLogger();

    default void randomizeBlockLootTable() {
        info("Randomizing block loot table...");
        randomizeBlockLootTable0();
        info("Block loot table randomized.");
    }

    void randomizeBlockLootTable0();

    default void randomizeEntityLootTable() {
        info("Randomizing entity loot table...");
        randomizeEntityLootTable0();
        info("Entity loot table randomized.");
    }

    void randomizeEntityLootTable0();

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

    static @NotNull Randomizer loadRandomizer(@NotNull Logger logger, @NotNull Server server) {
        String version = server.getClass().getPackage().getName().split("\\.")[3];
        //noinspection SwitchStatementWithTooFewBranches
        switch (version) {
            case "v1_15_R1":
                return new Randomizer1_15_R1(logger);
            default:
                throw new UnsupportedOperationException("Version " + version + " unsupported");
        }
    }
}
