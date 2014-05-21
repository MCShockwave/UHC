package net.mcshockwave.UHC.Commands;

import net.mcshockwave.UHC.UltraHC;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class WLCommand implements CommandExecutor {

	public static BukkitTask	wloff	= null;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (sender.isOp()) {

			if (args.length == 0) {
				String pre = "§c /" + label + " ";
				sender.sendMessage(new String[] {
						pre + "list - See the whitelisted players",
						pre + "add/remove [player] - Add/remove player from whitelist",
						pre + "clear - clear the whitelist",
						pre + "addall - add all online players to whitelist",
						pre + "time - get current java time to compare with time.is to get offset for offat",
						pre
								+ "offat [HH:MM] [offset] - [EXPEREMENTAL] turn whitelist off at certain time (format: HH:MM UTC)" });
			} else {
				String c = args[0];

				if (c.equalsIgnoreCase("list")) {
					sender.sendMessage(listWhitelisted());
				} else if (c.equalsIgnoreCase("add")) {
					OfflinePlayer op = getFromString(args[1]);
					op.setWhitelisted(true);
					sender.sendMessage("Added " + op.getName() + " to whitelist");
				} else if (c.equalsIgnoreCase("remove")) {
					OfflinePlayer op = getFromString(args[1]);
					op.setWhitelisted(false);
					sender.sendMessage("Removed " + op.getName() + " from whitelist");
				} else if (c.equalsIgnoreCase("clear")) {
					sender.sendMessage("Cleared whitelist of all non-op players");

					for (OfflinePlayer op : Bukkit.getWhitelistedPlayers()) {
						if (op.isOp()) {
							continue;
						}
						op.setWhitelisted(false);
					}
				} else if (c.equalsIgnoreCase("addall")) {
					for (Player p : Bukkit.getOnlinePlayers()) {
						p.setWhitelisted(true);
					}

					sender.sendMessage("Added all online players to whitelist");
				} else if (c.equalsIgnoreCase("time")) {
					Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

					sender.sendMessage("§cCurrent Time: §f" + cal.getTime().toString());
				} else if (c.equalsIgnoreCase("offat")) {
					try {
						String[] par = args[1].split(":");
						int h = Integer.parseInt(par[0]);
						int m = Integer.parseInt(par[1]);

						Calendar cl = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
						if (cl.get(Calendar.HOUR_OF_DAY) > h) {
							cl.add(Calendar.DAY_OF_MONTH, 1);
						}
						cl.set(Calendar.HOUR_OF_DAY, h);
						cl.set(Calendar.MINUTE, m);
						cl.set(Calendar.SECOND, 0);

						long offsetMillis = Long.parseLong(args[2]) * 1000;

						long now = System.currentTimeMillis() - offsetMillis;
						long wlo = cl.getTimeInMillis();

						long time = wlo - now;

						long ticks = TimeUnit.MILLISECONDS.toSeconds(time) * 20;

						sender.sendMessage("Time until whitelist off: " + ticks + " ticks (" + (ticks / 20)
								+ " seconds)");

						if (wloff != null) {
							wloff.cancel();
						}

						wloff = Bukkit.getScheduler().runTaskLater(UltraHC.ins, new Runnable() {
							public void run() {
								Bukkit.setWhitelist(false);

								Bukkit.broadcastMessage("§eWhitelist has been turned off by Scheduler");
							}
						}, ticks);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		}

		return false;
	}

	public String listWhitelisted() {
		String ret = "";
		String com = ", ";

		for (OfflinePlayer op : Bukkit.getWhitelistedPlayers()) {
			ret += op.getName() + com;
		}

		return ret.substring(0, ret.length() - com.length());
	}

	public OfflinePlayer getFromString(String s) {
		for (OfflinePlayer op : Bukkit.getOfflinePlayers()) {
			if (op.getName().toLowerCase().startsWith(s.toLowerCase())) {
				return op;
			}
		}
		return null;
	}

}
