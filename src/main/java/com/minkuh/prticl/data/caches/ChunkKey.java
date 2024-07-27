package com.minkuh.prticl.data.caches;

import java.util.Objects;

public class ChunkKey {
    private final int x;
    private final int z;

    public ChunkKey(int x, int z) {
        this.x = x;
        this.z = z;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) return false;
        ChunkKey chunkKey = (ChunkKey) obj;
        return x == chunkKey.x && z == chunkKey.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, z);
    }
}