package net.mcshockwave.UHC.Listeners;

import net.mcshockwave.UHC.UltraHC;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

public class ChumpHandler implements Listener {

	public static BukkitTask	timer	= null;

	public static int			time	= 0;

	public static final String	pre		= "§8[§2Chump Charity§8] §a";

	public static void start(final float mins) {
		Bukkit.broadcastMessage(pre + "Chump Charity has been started! (interval: " + mins + " minutes)");

		timer = Bukkit.getScheduler().runTaskTimer(UltraHC.ins, new Runnable() {
			public void run() {
				time--;
				if (time < 0) {
					time = (int) (mins * 60);
				}

				if (time == 300) {
					Bukkit.broadcastMessage(pre + "5 minutes until next heal!");
				}
				if (time == 180) {
					Bukkit.broadcastMessage(pre + "3 minutes until next heal!");
				}
				if (time == 120) {
					Bukkit.broadcastMessage(pre + "2 minutes until next heal!");
				}
				if (time == 60) {
					Bukkit.broadcastMessage(pre + "1 minute until next heal!");
				}
				if (time == 30 || time == 15 || time == 10) {
					Bukkit.broadcastMessage(pre + time + " seconds until next heal!");
				}
				if (time == 0) {
					Player heal = UltraHC.getLowestHealth();
					UltraHC.rerandomize();
					if (heal == null) {
						Bukkit.broadcastMessage(pre + "Nobody was healed!");
					}

					Bukkit.broadcastMessage(pre + heal.getName() + " has been healed to full health!");
					heal.setHealth(heal.getMaxHealth());
				}
			}
		}, 20, 20);
	}

	public static void stop() {
		Bukkit.broadcastMessage(pre + "Chump Charity has been stopped!");

		timer.cancel();
	}

}
