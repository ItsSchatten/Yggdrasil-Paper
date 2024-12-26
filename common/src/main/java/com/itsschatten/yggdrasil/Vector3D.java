package com.itsschatten.yggdrasil;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a position in a 3D space.
 * More specifically, this represents a block in a Minecraft world.
 */
public class Vector3D {

    /**
     * The 'x' coordinate.
     */
    public int x;

    /**
     * The 'y' coordinate.
     */
    public int y;

    /**
     * The 'z' coordinate.
     */
    public int z;

    /**
     * Constructs a new Vector3D with the values provided.
     *
     * @param x The X coordinate.
     * @param y The Y coordinate.
     * @param z The Z coordinate.
     */
    public Vector3D(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Generates a new Vector3D from a location. This uses the exact block values.
     *
     * @param loc The location to use to create a Vector3D.
     * @return Returns a new Vector3D.
     */
    @Contract("_ -> new")
    public static @NotNull Vector3D fromLocation(@NotNull Location loc) {
        return new Vector3D(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    /**
     * Converts a Vector3D into a {@link Location}
     *
     * @param world  The world to use with the {@link Location}.
     * @param vector The vector to use to get the location.
     * @return Returns a new {@link Location} with the values of the Vector in the world provided.
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull Location toLocation(final World world, @NotNull Vector3D vector) {
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
