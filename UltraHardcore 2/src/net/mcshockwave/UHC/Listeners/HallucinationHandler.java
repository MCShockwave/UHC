package net.mcshockwave.UHC.Listeners;

import net.mcshockwave.UHC.Utils.LocUtils;
import net.mcshockwave.UHC.UltraHC;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Random;

public class HallucinationHandler {

	public static BukkitRunnable	sched	= null;
	public static Random			rand	= new Random();

	public static void onStartGame() {
		sched = new BukkitRunnable() {
			public void run() {
				if (rand.nextInt(40) == 0) {
					ArrayList<Player> alive = UltraHC.getAlive();
					Player p = alive.get(rand.nextInt(alive.size()));

					SoundEffect ef = getRandomEffect(p);

					Location l = LocUtils.addRand(p.getLocation().clone(), 16, 10, 16);
					if (ef.s == Sound.CREEPER_HISS || ef.s == Sound.ARROW_HIT) {
						l = LocUtils.addRand(p.getLocation(), 5, 1, 5);
					}
					if (ef.vol > 5) {
						l = LocUtils.addRand(p.getLocation(), 50, 25, 50).add(0, -25, 0);
					}

					if (UltraHC.ts.isTeamGame()) {
						Team t = UltraHC.score.getPlayerTeam(p);
						for (OfflinePlayer op : t.getPlayers()) {
							if (op.isOnline()) {
								ef.play(p.getPlayer(), l);
							}
						}
					} else {
						ef.play(p, l);
					}

					for (Player p2 : Bukkit.getOnlinePlayers()) {
						if (UltraHC.specs.contains(p2.getName())) {
							ef.play(p2, l);
						}
					}
				}
			}
		};
		sched.runTaskTimer(UltraHC.ins, 100, 12);
	}

	public static ArrayList<SoundEffect> getList(Player p) {
		ArrayList<SoundEffect> ret = new ArrayList<>();

		for (SoundEffect ef : player) {
			ret.add(ef);
		}

		if (p.getLocation().getY() < 40) {
			for (SoundEffect ef : mobs) {
				ret.add(ef);
			}
		}

		return ret;
	}

	public static SoundEffect getRandomEffect(Player p) {
		ArrayList<SoundEffect> efs = getList(p);

		SoundEffect r = efs.get(rand.nextInt(efs.size()));

		return r;
	}

	public static SoundEffect[]	player	= { n(Sound.EXPLODE, 10f, 1f), n(Sound.ITEM_PICKUP), n(Sound.HURT_FLESH),
			n(Sound.ARROW_HIT), n(Sound.STEP_GRAVEL), n(Sound.STEP_STONE), n(Sound.STEP_GRASS) };
	public static SoundEffect[]	mobs	= { n(Sound.ZOMBIE_IDLE), n(Sound.SKELETON_IDLE), n(Sound.SPIDER_IDLE),
			n(Sound.CREEPER_HISS), n(Sound.EXPLODE, 10f, 1f), n(Sound.SHOOT_ARROW), n(Sound.ARROW_HIT),
			n(Sound.ZOMBIE_HURT), n(Sound.SKELETON_HURT), n(Sound.SPIDER_DEATH), n(Sound.CREEPER_DEATH),
			n(Sound.DIG_GRAVEL), n(Sound.DIG_STONE), n(Sound.ITEM_BREAK) };

	public static void onEndGame() {
		sched.cancel();
	}

	public static class SoundEffect {
		public Sound	s;
		public float	pit;
		public float	vol;

		private SoundEffect(Sound s, float vol, float pit) {
			this.s = s;
			this.pit = pit;
			this.vol = vol;
		}

		public void play(Player p, Location l) {
			p.playSound(l, s, vol, pit);
		}
	}

	public static SoundEffect n(Sound s, float vol, float pit) {
		return new SoundEffect(s, vol, pit);
	}

	public static SoundEffect n(Sound s) {
		return new SoundEffect(s, 1, 1);
	}

}
