package net.mcshockwave.UHC.Commands;

import net.mcshockwave.UHC.Option;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandOption implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("setoption")) {

			if (sender.isOp()) {
				Option set = null;
				for (Option o : Option.values()) {
					if (o.name().equalsIgnoreCase(args[0])) {
						set = o;
						break;
					}
				}

				if (set != null) {
					String to = args[1];

					if (isInteger(to)) {
						set.set(Integer.parseInt(to));
					} else if (isBoolean(to)) {
						set.set(Boolean.parseBoolean(to));
					} else {
						set.set(to);
					}

					sender.sendMessage("§aSet option " + set.name + " to value " + to);
					return true;
				}
			}

			return false;
		}

		if (sender instanceof Player) {
			Player p = (Player) sender;

			Option.getOptionsMenu(false).open(p);
		}

		return false;
	}

	public boolean isInteger(String test) {
		try {
			Integer.parseInt(test);
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	public boolean isBoolean(String test) {
		try {
			Boolean.parseBoolean(test);
			return true;
		} catch (Exception e) {
		}
		return false;
	}

}
