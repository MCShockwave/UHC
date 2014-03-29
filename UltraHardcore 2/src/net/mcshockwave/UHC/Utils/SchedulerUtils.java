package net.mcshockwave.UHC.Utils;

import net.mcshockwave.UHC.UltraHC;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;

public class SchedulerUtils {

	public static class SchedulerInstance {
		Object	os[];
		Player	target;
		Entity	targEnt;
		
		private ArrayList<BukkitTask> tasks = new ArrayList<>();
		
		private volatile boolean terminated = false;

		public SchedulerInstance(Object... objects) {
			os = objects;
		}
		
		private int id = -1;

		public void run() {
			id = -1;
			next();
		}
		
		public void next() {
			id++;
			if (id >= os.length) {
				return;
			}
			Object o = os[id];
			if (o instanceof Player) {
				target = (Player) o;
				targEnt = target;
			} else if (o instanceof Entity) {
				targEnt = (Entity) o;
			} else if (o instanceof Location) {
				targEnt.teleport((Location) o);
			} else if (o instanceof String) {
				target.sendMessage((String) o);
			} else if (o instanceof SoundEffect) {
				if (o instanceof GlobalSoundEffect) {
					((GlobalSoundEffect) o).playSound();
				} else
					((SoundEffect) o).playSound(target);
			} else if (o instanceof Runnable) {
				((Runnable) o).run();
			}
			
			if (o instanceof Integer) {
				tasks.add(Bukkit.getScheduler().runTaskLater(UltraHC.ins, new Runnable() {
					public void run() {
						next();
					}
				}, (Integer) o));
			} else {
				next();
			}
			
			if (terminated) {
				return;
			}
		}
		
		public void terminate() {
			for (BukkitTask bt : tasks) {
				if (bt != null) {
					bt.cancel();
				}
			}
			terminated = true;
		}
	}

	public static class SoundEffect {
		public Sound	sound;
		public float	volume;
		public float	pitch;

		public SoundEffect(Sound s, float volume, float pitch) {
			this.sound = s;
			this.volume = volume;
			this.pitch = pitch;
		}

		public void playSound(Player p) {
			playSound(p, p.getEyeLocation());
		}

		public void playSound(Player p, Location l) {
			p.playSound(l, sound, volume, pitch);
		}
	}

	public static class GlobalSoundEffect extends SoundEffect {
		public Location	l;

		public GlobalSoundEffect(Location l, Sound s, float volume, float pitch) {
			super(s, volume, pitch);
			this.l = l;
		}

		public void playSound() {
			l.getWorld().playSound(l, sound, volume, pitch);
		}
	}
	
	ArrayList<Object> os;
	SchedulerInstance thread = null;
	
	public static SchedulerUtils getNew() {
		SchedulerUtils utils = new SchedulerUtils();
		utils.os = new ArrayList<>();
		return utils;
	}
	
	public SchedulerUtils add(Object o) {
		os.add(o);
		return this;
	}
	
	public void execute() {
		SchedulerInstance si = new SchedulerInstance(os.toArray());
		thread = si;
		si.run();
	}
	
	public void terminate() {
		thread.terminate();
	}
	
	public static SchedulerUtils scheduleEvents(Object... objects) {
		SchedulerUtils util = SchedulerUtils.getNew();
		for (Object o : objects) {
			util.add(o);
		}
		util.execute();
		return util;
	}

}
