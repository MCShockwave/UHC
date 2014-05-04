package net.mcshockwave.UHC.Commands;

import net.mcshockwave.UHC.BanManager;
import net.mcshockwave.UHC.Utils.ListUtils;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BanningCommands implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender.isOp()) {

			if (label.equalsIgnoreCase("permban")) {
				if (args.length > 0) {
					String toBan = args[0];
					String reason = ListUtils.arrayToString(ListUtils.subarray(args, 1));

					BanManager.setBanned(toBan, -1, reason, sender.getName());
					Bukkit.broadcastMessage("§e" + toBan + " was banned by " + sender.getName()
							+ " permanently for reason " + reason);
				}
			}

			if (label.equalsIgnoreCase("gameban")) {
				if (args.length > 1) {
					String toBan = args[0];
					String gameString = args[1];
					int games = -1;
					try {
						games = Integer.parseInt(gameString);
					} catch (Exception e) {
						sender.sendMessage("§cSyntax: /gameban {name} {games} {reason}");
						return false;
					}
					String reason = ListUtils.arrayToString(ListUtils.subarray(args, 2));

					BanManager.setBanned(toBan, games, reason, sender.getName());
					Bukkit.broadcastMessage("§e" + toBan + " was banned by " + sender.getName() + " for " + games
							+ " games for reason " + reason);
				}
			}

			if (label.equalsIgnoreCase("incrban")) {
				if (args.length > 0) {
					String c = args[0];

					if (c.equalsIgnoreCase("add")) {
						int gms = Integer.parseInt(args[1]);
						BanManager.incrGames(gms);
						sender.sendMessage("§aIncremented all bans by " + gms + " games");
					}

					if (c.equalsIgnoreCase("get")) {
						String name = args[1];

						if (!BanManager.isBanned(name)) {
							sender.sendMessage("§cPlayer is not banned");
							return true;
						}

						String reason = BanManager.getBanReason(name);
						sender.sendMessage("§eBan Reason for player " + name + ":");
						sender.sendMessage(reason);
					}
				}
			}

		} else {
			sender.sendMessage("§cYou are not op!");
		}

		return false;
	}
}
