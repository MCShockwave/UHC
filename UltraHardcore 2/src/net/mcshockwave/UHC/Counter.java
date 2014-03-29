package net.mcshockwave.UHC;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.TimeUnit;

public class Counter {

	public long	startTime;
	public long	runCount	= 0;
	public long	runCountMin	= 0;
	private BukkitTask	co	= null, coMi = null;
	public Runnable		run	= null;

	public long getTime() {
		return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startTime);
	}

	public String getTimeString() {
		return String.format("%d:%02d:%02d", getHours(), getMins(), getSecs());
	}

	public long getSecs() {
		return getTime() % 60;
	}

	public long getMins() {
		return getTime() % 3600 / 60;
	}

	public long getTotalMins() {
		return (long) Math.floor(getTime() / 60);
	}

	public long getHours() {
		return getTime() / 3600;
	}

	public void setRunnable(Runnable run) {
		this.run = run;
	}

	public void start() {
		startTime = System.currentTimeMillis();
		co = Bukkit.getScheduler().runTaskTimerAsynchronously(UltraHC.ins, new Runnable() {
			public void run() {
				long time = getTime();
				if (runCount % 60 > time % 60) { // skipped min
					runCount = (long) (60 * Math.floor(runCount / 60));
				} else
					runCount = time;
				if (run == null)
					return;
				run.run();
			}
		}, 20, 20);
		coMi = Bukkit.getScheduler().runTaskTimerAsynchronously(UltraHC.ins, new Runnable() {
			public void run() {
				runCountMin = getTotalMins();
			}
		}, 0, 120);
	}

	public void stop() {
		if (co != null) {
			co.cancel();
			co = null;
		}
		if (coMi != null) {
			coMi.cancel();
			coMi = null;
		}
	}

}
