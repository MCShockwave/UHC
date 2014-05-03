package net.mcshockwave.UHC.worlds;

import net.mcshockwave.UHC.Utils.CustomSignUtils.CustomSign;
import net.mcshockwave.UHC.Utils.CustomSignUtils.SignRunnable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Multiworld {

	public static WorldCreator[]	worlds	= { wc("UHC", Environment.NORMAL, WorldType.NORMAL),
			wc("UHC_nether", Environment.NETHER, WorldType.NORMAL), wc("KitPVP", Environment.NORMAL, WorldType.NORMAL),
			wc("Lobby", Environment.NORMAL, WorldType.FLAT) };

	private static WorldCreator wc(String name, Environment env, WorldType wt) {
		WorldCreator wc = new WorldCreator(name);
		wc.environment(env);
		wc.type(wt);

		if (wt == WorldType.NORMAL && env == Environment.NORMAL) {
			try {
				wc.generator("TerrainControl");
			} catch (Exception e) {
			}
		}
		if (wt == WorldType.FLAT) {
			wc.generator(new ChunkGenerator() {
				@Override
				public List<BlockPopulator> getDefaultPopulators(World world) {
					return new ArrayList<BlockPopulator>();
				}

				@Override
				public byte[][] generateBlockSections(World world, Random random, int chunkx, int chunkz,
						ChunkGenerator.BiomeGrid biomes) {
					return new byte[world.getMaxHeight() / 16][];
				}
			});
		}

		return wc;
	}

	public static void loadAll() {
		for (WorldCreator wc : worlds) {
			final World w = wc.createWorld();

			CustomSign wsign = new CustomSign("§8[Teleport]", "§2" + wc.name(), "", "Click to TP", "[World]",
					wc.name(), "", "");
			wsign.onClick(new SignRunnable() {
				public void run(Player p, Sign s, PlayerInteractEvent e) {
					p.teleport(w.getSpawnLocation().add(0.5, 1, 0.5));
				}
			});
		}
	}

	public static boolean isInUHCWorld(Location l) {
		return isUHCWorld(l.getWorld());
	}

	public static boolean isUHCWorld(World w) {
		return w == getUHC() || w == getNether();
	}

	public static World getUHC() {
		return Bukkit.getWorld("UHC");
	}

	public static World getKit() {
		return Bukkit.getWorld("KitPVP");
	}

	public static World getLobby() {
		return Bukkit.getWorld("Lobby");
	}

	public static World getNether() {
		return Bukkit.getWorld("UHC_nether");
	}

}
