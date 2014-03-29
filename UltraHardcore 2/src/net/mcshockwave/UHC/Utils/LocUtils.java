package net.mcshockwave.UHC.Utils;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;

public class LocUtils {

	private static Random	rand	= new Random();

	public static boolean isSame(Location l1, Location l2) {
		return l1.getBlockX() == l2.getBlockX() && l1.getBlockY() == l2.getBlockY() && l1.getBlockZ() == l2.getBlockZ();
	}

	public static Location addRand(Location l, int radX, int radY, int radZ) {
		Location l2 = l.clone().add(rand.nextInt(radX * 2) - radX, radY > 0 ? (rand.nextInt(radY * 2) - radY) : 0,
				rand.nextInt(radZ * 2) - radZ);
		return l2;
	}

	public static Vector getVelocity(Location c, Location f) {
		return new Vector(f.getX() - c.getX(), 0.6, f.getZ() - c.getZ()).multiply(5 / f.distance(c));
	}

	public static boolean isInCuboid(Player player, Block block1, Block block2) {
		return isInCuboid(player.getLocation(), block1.getLocation(), block2.getLocation());
	}

	public static boolean isInCuboid(Location checkLoc, Location loc1, Location loc2) {

		double x1 = Math.min(loc1.getX(), loc2.getX());
		double y1 = Math.min(loc1.getY(), loc2.getY());
		double z1 = Math.min(loc1.getZ(), loc2.getZ());

		double x2 = Math.max(loc1.getX(), loc2.getX());
		double y2 = Math.max(loc1.getY(), loc2.getY());
		double z2 = Math.max(loc1.getZ(), loc2.getZ());

		double cx = checkLoc.getX();
		double cy = checkLoc.getY();
		double cz = checkLoc.getZ();

		return (cx > x1 && cx < x2 && cy > y1 && cy < y2 && cz > z1 && cz < z2);
	}

	// no idea how this works, no touching
	// I got this from forums
	public static ArrayList<Location> circle(Player player, Location loc, double radius, int height, boolean hollow,
			boolean sphere, int offsetY) {
		ArrayList<Location> circleblocks = new ArrayList<Location>();
		double cx = loc.getX();
		double cy = loc.getY();
		double cz = loc.getZ();
		for (double x = cx - radius; x <= cx + radius; x++)
			for (double z = cz - radius; z <= cz + radius; z++)
				for (double y = (sphere ? cy - radius : cy); y < (sphere ? cy + radius : cy + height); y++) {
					double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
					if (dist < radius * radius && !(hollow && dist < (radius - 1) * (radius - 1))) {
						Location l = new Location(loc.getWorld(), x, y + offsetY, z);
						circleblocks.add(l);
					}
				}

		return circleblocks;
	}

}
