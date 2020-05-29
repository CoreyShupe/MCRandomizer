package me.fixed.mcrandomizer;

import org.jetbrains.annotations.NotNull;

public class Pair<K, V> {
    @NotNull private final K k;
    @NotNull private final V v;

    public Pair(@NotNull K k, @NotNull V v) {
        this.k = k;
        this.v = v;
    }

    public @NotNull K getLeft() {
        return k;
    }

    public @NotNull V getRight() {
        return v;
    }
}
