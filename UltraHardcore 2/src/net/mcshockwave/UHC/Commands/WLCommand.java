package net.mcshockwave.UHC.Commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class WLCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (sender.isOp()) {

			if (args.length == 0) {
				String pre = "§c /" + label + " ";
				sender.sendMessage(new String[] { pre + "list - See the whitelisted players",
						pre + "add/remove - Add/remove player from whitelist", pre + "clear - clear the whitelist" });
			} else {
				String c = args[0];
				
				if (c.equalsIgnoreCase("list")) {
					Bukkit.dispatchCommand(sender, "whitelist list");
				} else if (c.equalsIgnoreCase("add") || c.equalsIgnoreCase("remove")) {
					Bukkit.dispatchCommand(sender, "whitelist " + c + " " + args[1]);
				} else if (c.equalsIgnoreCase("clear")) {
					sender.sendMessage("Cleared whitelist of all non-op players");

					for (OfflinePlayer op : Bukkit.getWhitelistedPlayers()) {
						if (op.isOp()) {
							continue;
						}
						op.setWhitelisted(false);
					}
				}
			}

		}

		return false;
	}

}
