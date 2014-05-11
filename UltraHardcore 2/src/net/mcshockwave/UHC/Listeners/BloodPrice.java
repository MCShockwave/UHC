package net.mcshockwave.UHC.Listeners;

import net.mcshockwave.UHC.UltraHC;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

public class BloodPrice implements Listener {

	public static BukkitTask	timer	= null;

	public static int			time	= 0;

	public static final String	pre		= "§8[§4Blood Price§8] §c";

	public static void start(final float mins) {
		Bukkit.broadcastMessage(pre + "Blood Price has been started! (interval: " + mins + " minutes)");

		timer = Bukkit.getScheduler().runTaskTimer(UltraHC.ins, new Runnable() {
			public void run() {
				time--;
				if (time < 0) {
					time = (int) (mins * 60);
				}

				if (time == 300) {
					Bukkit.broadcastMessage(pre + "5 minutes until next damage!");
				}
				if (time == 180) {
					Bukkit.broadcastMessage(pre + "3 minutes until next damage!");
				}
				if (time == 120) {
					Bukkit.broadcastMessage(pre + "2 minutes until next damage!");
				}
				if (time == 60) {
					Bukkit.broadcastMessage(pre + "1 minute until next damage!");
				}
				if (time == 30 || time == 15 || time == 10) {
					Bukkit.broadcastMessage(pre + time + " seconds until next damage!");
				}
				if (time == 0) {
					Player dam = UltraHC.getMaximumHealth();
					UltraHC.rerandomize();
					if (dam == null) {
						Bukkit.broadcastMessage(pre + "Nobody was damaged!");
					}
					
					Bukkit.broadcastMessage(pre + dam.getName() + " has been damaged!");
					double hp = dam.getHealth();
					hp -= 6;
					if (hp <= 0) {
						hp = 1;
					}
					dam.setHealth(hp);
					dam.damage(0);
				}
			}
		}, 20, 20);
	}

	public static void stop() {
		Bukkit.broadcastMessage(pre + "Blood Price has been stopped!");

		timer.cancel();
	}

}
