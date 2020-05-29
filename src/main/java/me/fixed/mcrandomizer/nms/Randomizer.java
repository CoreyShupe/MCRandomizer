package me.fixed.mcrandomizer.nms;

import org.bukkit.Server;

import java.util.logging.Logger;

public interface Randomizer {
    void randomizeBlockLootTable();

    void randomizeEntityLootTable();

    void randomizeChestLootTable();

    static Randomizer loadRandomizer(Logger logger, Server server) {
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
