package net.mcshockwave.UHC.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HelpOpCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (args.length == 0) {
			return false;
		}

		String mes = "";
		for (int i = 0; i < args.length; i++) {
			mes += args[i] + " ";
		}

		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.isOp() || p == sender) {
				p.sendMessage("§8[§aHelpOp§8] §6" + sender.getName() + "§f: " + mes);
			}
		}

		return false;
	}

}
