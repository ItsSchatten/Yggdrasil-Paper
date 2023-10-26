package com.itsschatten.yggdrasil;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Vector3D {
    public int x;
    public int y;
    public int z;

    public Vector3D(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Contract("_ -> new")
    public static @NotNull Vector3D fromLocation(@NotNull Location loc) {
        return new Vector3D(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    public static Location toLocation(final World world, @NotNull Vector3D vector) {
        return new Location(world, vector.x, vector.y, vector.z);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Vector3D vec)) {
            return false;
        }
        return x == vec.x && y == vec.y && z == vec.z;
    }

    @Override
    public String toString() {
        return "Vector3D{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
