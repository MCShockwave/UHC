package net.mcshockwave.UHC.Commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WLCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (sender.isOp()) {

			if (args.length == 0) {
				String pre = "§c /" + label + " ";
				sender.sendMessage(new String[] { pre + "list - See the whitelisted players",
						pre + "add/remove - Add/remove player from whitelist", pre + "clear - clear the whitelist",
						pre + "addall - add all online players to whitelist" });
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
