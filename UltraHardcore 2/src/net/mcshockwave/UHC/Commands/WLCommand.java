package net.mcshockwave.UHC.Commands;

import net.mcshockwave.UHC.UltraHC;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class WLCommand implements CommandExecutor {

	public static BukkitTask	wloff	= null;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (sender.isOp()) {

			if (args.length == 0) {
				String pre = "§c /" + label + " ";
				sender.sendMessage(new String[] { pre + "list - See the whitelisted players",
						pre + "add/remove - Add/remove player from whitelist", pre + "clear - clear the whitelist",
						pre + "addall - add all online players to whitelist",
						pre + "offat - [EXPEREMENTAL] turn whitelist off at certain time (format: HH:MM UTC)" });
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
				} else if (c.equalsIgnoreCase("offat")) {
					SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
					sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

					try {
						Date date = sdf.parse(args[1]);
						sender.sendMessage("Parsed as: " + date.toString() + " Off at: " + date.getTime() + " millis");

						long timeUntil = date.getTime() - System.currentTimeMillis();
						long timeTicks = timeUntil / 50;

						sender.sendMessage("Time until whitelist off: " + timeUntil + " millis (" + timeTicks
								+ " ticks)");

						if (wloff != null) {
							wloff.cancel();
						}

						wloff = Bukkit.getScheduler().runTaskLater(UltraHC.ins, new Runnable() {
							public void run() {
								Bukkit.setWhitelist(false);

								Bukkit.broadcastMessage("§eWhitelist has been turned off by Scheduler");
							}
						}, timeTicks);
					} catch (ParseException e) {
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

		for (OfflinePlayer op : Bukkit.getOfflinePlayers()) {
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
