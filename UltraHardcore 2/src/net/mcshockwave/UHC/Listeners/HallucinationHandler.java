package net.mcshockwave.UHC.Listeners;

import net.mcshockwave.UHC.NumberedTeamSystem.NumberTeam;
import net.mcshockwave.UHC.UltraHC;
import net.mcshockwave.UHC.Utils.LocUtils;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HallucinationHandler {

	public static BukkitRunnable	sched	= null;
	public static Random			rand	= new Random();

	public static void onStartGame() {
		sched = new BukkitRunnable() {
			public void run() {
				for (Player p : UltraHC.getAlive()) {
					Hallucination hal = Hallucination.values()[rand.nextInt(Hallucination.values().length)];
					if (rand.nextInt(hal.chance) == 0) {
						hal.onActivate(p);
					}
				}
			}
		};
		sched.runTaskTimer(UltraHC.ins, 100, rand.nextInt(5) + 5);
	}

	public static void onEndGame() {
		sched.cancel();
	}

	public static enum Hallucination {
		PLAYER_NAMES(
			2),
		DIGGING(
			100);

		public int	chance;

		private Hallucination(int chance) {
			this.chance = chance;
		}

		public void onActivate(Player p) {
			if (this == PLAYER_NAMES) {
				Location sp = LocUtils.addRand(p.getLocation(), 25, 25, 25);
				if (sp.getBlock().getType() == Material.AIR && sp.add(0, -1, 0).getBlock().getType() != Material.AIR) {
					String[] names = {};
					if (UltraHC.nts.isTeamGame()) {
						NumberTeam nt = UltraHC.nts.teams.get(rand.nextInt(UltraHC.nts.teams.size()));
						if (nt != UltraHC.nts.getTeam(p.getName())) {
							List<String> nml = new ArrayList<>();
							for (Player pl : nt.getOnlinePlayers()) {
								nml.add(nt.t.getPrefix() + pl.getName() + nt.t.getSuffix());
							}
						}
					} else {
						Player c = UltraHC.getAlive().get(rand.nextInt(UltraHC.getAlive().size()));
						if (c != null) {
							names = new String[] { c.getName() };
						}
					}

					for (String s : names) {
						EntityType[] types = { EntityType.CHICKEN, EntityType.COW, EntityType.PIG, EntityType.SHEEP };
						LivingEntity c = (LivingEntity) sp.getWorld()
								.spawnEntity(sp, types[rand.nextInt(types.length)]);
						c.setCustomName(s);
						c.setCustomNameVisible(true);
					}
				}
			}
			if (this == DIGGING) {
				if (p.getLocation().getY() < 40) {
					final Location start = LocUtils.addRand(p.getLocation(), 10, 30, 10).add(0, 15, 0);
					if (start.getBlock().getType() == Material.STONE) {
						for (int y = 0; y < 30; y++) {
							final int y2 = y;
							Bukkit.getScheduler().runTaskLater(UltraHC.ins, new Runnable() {
								public void run() {
									final Block b = start.clone().add(0, -y2, 0).getBlock();
									b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, Material.STONE);
									Bukkit.getScheduler().runTaskLater(UltraHC.ins, new Runnable() {
										public void run() {
											float pitch = 1.2f + ((float) rand.nextInt(4) / (float) 10);
											b.getWorld().playSound(b.getLocation(), Sound.ITEM_PICKUP, 0.8f, pitch);
										}
									}, 2);
								}
							}, 15 * y);
						}
					}
				}
			}
		}
	}
}
