package net.mcshockwave.UHC.Commands;

import net.mcshockwave.UHC.Option;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandOption implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			
			Option.getGlobalMenu(false).open(p);
		}
		
		return false;
	}
	
}
