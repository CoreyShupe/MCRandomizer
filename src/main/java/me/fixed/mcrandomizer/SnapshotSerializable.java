package me.fixed.mcrandomizer;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Map;

public class SnapshotSerializable implements Serializable {
    private static final long serialVersionUID = 1L;
    @NotNull private final Map<String, Map<String, Integer>> snapshot;

    public SnapshotSerializable(@NotNull Map<String, Map<String, Integer>> snapshot) {
        this.snapshot = snapshot;
    }

    public @NotNull Map<String, Map<String, Integer>> getSnapshot() {
        return snapshot;
    }
}
