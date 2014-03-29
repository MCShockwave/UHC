package net.mcshockwave.UHC.Commands;

import net.mcshockwave.UHC.UltraHC;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RestrictCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender.isOp()) {
			String typ = args[0];
			
			UltraHC.maxPlayers = Integer.parseInt(typ);
			sender.sendMessage("§cRestricted to " + typ + " slots");
		}
		return false;
	}

}
