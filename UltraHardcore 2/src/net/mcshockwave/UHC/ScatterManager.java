package net.mcshockwave.UHC;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class ScatterManager {

	public static HashMap<String, Location>	scatterLocs	= new HashMap<>();

	static Random							rand		= new Random();

	public static Location getLocation(String pl) {
		if (scatterLocs.containsKey(pl)) {
			return scatterLocs.get(pl);
		}
		return null;
	}

	public static double mindis(int radius, int count) {
		double totalColumns = (radius * 2) * (radius * 2);
		double minDisSq = (totalColumns / (double) count);

		return minDisSq;
	}

	public static Location[] getScatterLocations(World w, int radius, int count) {
		double minDisSq = mindis(radius, count);

		List<Location> locs = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			boolean good = false;
			double min = minDisSq;
			while (!good) {
				int x = rand.nextInt(radius * 2) - radius;
				int z = rand.nextInt(radius * 2) - radius;

				Location r = new Location(w, x + 0.5, 0, z + 0.5);

				boolean close = false;
				for (Location l : locs) {
					if (l.distanceSquared(r) < min) {
						close = true;
					}
				}
				if (!close && isValidSpawnLocation(r)) {
					locs.add(r);
					good = true;
				} else {
					min -= 1;
				}
			}
		}

		for (Location loc : locs) {
			loc.getChunk().load(true);
			int y = w.getHighestBlockYAt(loc.getBlockX(), loc.getBlockZ());
			loc.setY(y + 2);
		}
		return locs.toArray(new Location[0]);
	}

	public static Material[]	nospawn	= { Material.STATIONARY_WATER, Material.WATER, Material.STATIONARY_LAVA,
			Material.LAVA, Material.CACTUS };

	public static boolean isValidSpawnLocation(Location l) {
		Material m = l.add(0, -1, 0).getBlock().getType();
		boolean noHazard = true;
		if (l.getBlockY() < 48) {
			noHazard = false;
		}
		for (Material no : nospawn) {
			if (m == no) {
				noHazard = false;
			}
		}
		return noHazard;
	}

}
