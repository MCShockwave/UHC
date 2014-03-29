package net.mcshockwave.UHC.Commands;

import net.mcshockwave.UHC.UltraHC;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SilenceCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender.isOp()) {
			UltraHC.chatSilenced = !UltraHC.chatSilenced;
			
			if (UltraHC.chatSilenced) {
				Bukkit.broadcastMessage("§c§lChat has been silenced by " + sender.getName());
			} else {
				Bukkit.broadcastMessage("§c§lChat has been un-silenced by " + sender.getName());
			}
		}
		return false;
	}

}
