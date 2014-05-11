package net.mcshockwave.UHC.Listeners;

import net.mcshockwave.UHC.UltraHC;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

public class WeakestLink implements Listener {

	public static BukkitTask	timer	= null;

	public static int			time	= 0;

	public static final String	pre		= "§8[§3Weakest Link§8] §b";

	public static void start(final float mins) {
		Bukkit.broadcastMessage(pre + "Weakest Link has been started! (interval: " + mins + " minutes)");

		timer = Bukkit.getScheduler().runTaskTimer(UltraHC.ins, new Runnable() {
			public void run() {
				time--;
				if (time < 0) {
					time = (int) (mins * 60);
				}

				if (time == 300) {
					Bukkit.broadcastMessage(pre + "5 minutes until next death!");
				}
				if (time == 180) {
					Bukkit.broadcastMessage(pre + "3 minutes until next death!");
				}
				if (time == 120) {
					Bukkit.broadcastMessage(pre + "2 minutes until next death!");
				}
				if (time == 60) {
					Bukkit.broadcastMessage(pre + "1 minute until next death!");
				}
				if (time == 30 || time == 15 || time == 10) {
					Bukkit.broadcastMessage(pre + time + " seconds until next death!");
				}
				if (time == 0) {
					Player kill = UltraHC.getLowestHealth();
					UltraHC.rerandomize();
					if (kill == null) {
						Bukkit.broadcastMessage(pre + "Nobody was killed!");
					}
					
					Bukkit.broadcastMessage(pre + kill.getName() + " has been killed!");
					kill.damage(kill.getHealth());
				}
			}
		}, 20, 20);
	}

	public static void stop() {
		Bukkit.broadcastMessage(pre + "Weakest Link has been stopped!");

		timer.cancel();
	}

}
