package net.mcshockwave.UHC.Commands;

import net.mcshockwave.UHC.UltraHC;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTeam implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (sender instanceof Player) {
			Player p = (Player) sender;
			
			if (UltraHC.started) {
				p.sendMessage("§cYou can't use this command after the game has started!");
			} else {
				UltraHC.ts.getTeamSelector(p).open(p);
			}
			
		}
		
		return false;
	}

}
