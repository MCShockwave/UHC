package net.mcshockwave.UHC.Listeners;

import net.mcshockwave.UHC.UltraHC;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class SkyhighHandler {

	public static int			mins		= 45;
	public static int			interval	= 30;

	public static BukkitTask	timer		= null;

	public static void start() {
		Bukkit.broadcastMessage("§e§lSkyhigh on at " + mins + " minutes!");

		timer = new BukkitRunnable() {
			public void run() {
				startSkyhigh();
			}
		}.runTaskLater(UltraHC.ins, mins * 1200);
	}

	public static void stop() {
		if (timer != null) {
			timer.cancel();
		}
	}

	public static void startSkyhigh() {
		if (timer != null) {
			timer.cancel();
		}

		Bukkit.broadcastMessage("§e§lSkyhigh enabled! You must be above y101 to not take damage!");

		timer = new BukkitRunnable() {
			public void run() {
				for (Player p : UltraHC.getAlive()) {
					if (p.getLocation().getY() < 101) {
						p.damage(1);
					}
				}
			}
		}.runTaskTimer(UltraHC.ins, interval * 20, interval * 20);
	}

}
