package com.itsschatten.yggdrasil.wands.regions;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Signifies a Cuboid region.
 */
public class Cuboid {

    /**
     * The minimum x point.
     */
    private final int xMin;

    /**
     * The maximum x point.
     */
    private final int xMax;

    /**
     * The minimum y point.
     */
    private final int yMin;

    /**
     * The maximum y point.
     */
    private final int yMax;

    /**
     * The minimum z point.
     */
    private final int zMin;

    /**
     * The maximum z point.
     */
    private final int zMax;

    /**
     * Centered x.
     */
    private final double xMinCentered, xMaxCentered;

    /**
     * Centered y.
     */
    private final double yMinCentered, yMaxCentered;

    /**
     * Centered z.
     */
    private final double zMinCentered, zMaxCentered;

    /**
     * The {@link World} this region belongs too.
     */
    private final World world;

    /**
     * Used to make the region.
     *
     * @param point1 The region's first {@link Location}.
     * @param point2 The region's second {@link Location}.
     */
    public Cuboid(final @NotNull Location point1, final @NotNull Location point2) {
        this.xMin = Math.min(point1.getBlockX(), point2.getBlockX());
        this.xMax = Math.max(point1.getBlockX(), point2.getBlockX());
        this.yMin = Math.min(point1.getBlockY(), point2.getBlockY());
        this.yMax = Math.max(point1.getBlockY(), point2.getBlockY());
        this.zMin = Math.min(point1.getBlockZ(), point2.getBlockZ());
        this.zMax = Math.max(point1.getBlockZ(), point2.getBlockZ());
        this.world = point1.getWorld();
        this.xMinCentered = this.xMin + 0.5;
        this.xMaxCentered = this.xMax + 0.5;
        this.yMinCentered = this.yMin + 0.5;
        this.yMaxCentered = this.yMax + 0.5;
        this.zMinCentered = this.zMin + 0.5;
        this.zMaxCentered = this.zMax + 0.5;
    }

    /**
     * Gets a list of blocks within the region.
     *
     * @return The list of blocks within a region.
     */
    public List<Block> getBlocks() {
        final List<Block> blocks = new ArrayList<>(this.getTotalBlockSize());
        for (int x = this.xMin; x <= this.xMax; ++x) {
            for (int y = this.yMin; y <= this.yMax; ++y) {
                for (int z = this.zMin; z <= this.zMax; ++z) {
                    final Block b = this.world.getBlockAt(x, y, z);
                    blocks.add(b);
                }
            }
        }
        return blocks;
    }

    /**
     * An {@link Iterator} of blocks.
     *
     * @return the {@link #getBlocks} Iterator.
     */
    public Iterator<Block> getBlockIterator() {
        return getBlocks().iterator();
    }

    /**
     * Gets the center location.
     *
     * @return The center location.
     */
    public Location getCenter() {
        return new Location(this.world, (double) (this.xMax - this.xMin) / 2 + this.xMin, ((double) (this.yMax - this.yMin) / 2 + this.yMin), ((double) (this.zMax - this.zMin) / 2 + this.zMin));
    }

    /**
     * Gets the max points top block.
     *
     * @return A {@link Location}.
     */
    public Location getFirstTopBlock() {
        return getLocation(xMax, zMax, yMax);
    }

    /**
     * Gets the min points top block.
     *
     * @return A {@link Location}.
     */
    public Location getSecondTopBlock() {
        return getLocation(xMin, zMin, yMin);
    }

    /**
     * Gets the location.
     *
     * @param xMin The x point.
     * @param zMin The z point.
     * @param yMin The y.
     * @return A {@link Location}
     */
    @NotNull
    private Location getLocation(int xMin, int zMin, int yMin) {
        Location second;

        for (int i = yMax + 1; i > yMin; i--) {
            final Block toCheck = world.getBlockAt(xMin, i, zMin);
            if (!toCheck.isEmpty() || toCheck.getType() != Material.AIR) {
                second = toCheck.getLocation();
                return second;
            }
        }
        return new Location(this.world, xMin, yMin, zMin);
    }

    /**
     * Gets the distance between the two points.
     *
     * @return The distance, as a double.
     */
    public double getDistance() {
        return this.getFirstPoint().distance(this.getSecondPoint());
    }

    /**
     * Gets the squared distance between the two points.
     *
     * @return The distance squared, as a double.
     */
    public double getDistanceSquared() {
        return this.getFirstPoint().distanceSquared(this.getSecondPoint());
    }

    /**
     * Gets the height of the region.
     *
     * @return The height.
     */
    public int getHeight() {
        return this.yMax - this.yMin + 1;
    }

    /**
     * Gets the minimal point.
     *
     * @return The minimal point's {@link Location}
     */
    public Location getFirstPoint() {
        return new Location(this.world, this.xMin, this.yMin, this.zMin);
    }

    /**
     * Gets the maximum point.
     *
     * @return The maximum point's {@link Location}
     */
    public Location getSecondPoint() {
        return new Location(this.world, this.xMax, this.yMax, this.zMax);
    }

    /**
     * Gets a random location within the region.
     *
     * @return The random {@link Location}
     */
    public Location getRandomLocation() {
        final Random rand = new Random();
        final int x = rand.nextInt(Math.abs(this.xMax - this.xMin) + 1) + this.xMin;
        final int y = rand.nextInt(Math.abs(this.yMax - this.yMin) + 1) + this.yMin;
        final int z = rand.nextInt(Math.abs(this.zMax - this.zMin) + 1) + this.zMin;
        return new Location(this.world, x, y, z);
    }

    /**
     * Gets the total size of the region.
     *
     * @return The total region.
     */
    public int getTotalBlockSize() {
        return this.getHeight() * this.getXWidth() * this.getZWidth();
    }

    /**
     * Get the x width.
     *
     * @return The x's width.
     */
    public int getXWidth() {
        return this.xMax - this.xMin + 1;
    }

    /**
     * Get the z width.
     *
     * @return The z's width.
     */
    public int getZWidth() {
        return this.zMax - this.zMin + 1;
    }

    /**
     * Checks if a {@link Location} is within our region.
     *
     * @param loc The location we are checking.
     * @return True if the location is contained, false otherwise.
     */
    public boolean isIn(final @NotNull Location loc) {
        return loc.getWorld() == this.world && loc.getBlockX() >= this.xMin && loc.getBlockX() <= this.xMax && loc.getBlockY() >= this.yMin && loc.getBlockY() <= this.yMax && loc.getBlockZ() >= this.zMin && loc.getBlockZ() <= this.zMax;
    }

    /**
     * Checks if a {@link Player} is within our region.
     *
     * @param player The player we are checking.
     * @return True if the player is contained, false otherwise.
     */
    public boolean isIn(final @NotNull Player player) {
        return this.isIn(player.getLocation());
    }

    /**
     * Checks if a {@link Location} is within a margin of the region.
     *
     * @param loc    The location we are checking.
     * @param margin The margin to check in.
     * @return True if it is contained within the margin, false otherwise.
     */
    public boolean isWithinMargin(final @NotNull Location loc, final double margin) {
        return loc.getWorld() == this.world && loc.getX() >= this.xMinCentered - margin && loc.getX() <= this.xMaxCentered + margin && loc.getY() >= this.yMinCentered - margin && loc.getY() <= this.yMaxCentered + margin && loc.getZ() >= this.zMinCentered - margin && loc.getZ() <= this.zMaxCentered + margin;
    }

    @Override
    public String toString() {
        return "Cuboid{" +
                "xMin=" + xMin +
                ", xMax=" + xMax +
                ", yMin=" + yMin +
                ", yMax=" + yMax +
                ", zMin=" + zMin +
                ", zMax=" + zMax +
                ", world=" + world +
                '}';
    }

}
