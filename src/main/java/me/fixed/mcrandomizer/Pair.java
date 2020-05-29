package me.fixed.mcrandomizer;

public class Pair<K, V> {
    private final K k;
    private final V v;

    public Pair(K k, V v) {
        this.k = k;
        this.v = v;
    }

    public K getLeft() {
        return k;
    }

    public V getRight() {
        return v;
    }
}
