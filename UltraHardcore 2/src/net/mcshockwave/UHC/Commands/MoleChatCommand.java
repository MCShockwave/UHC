package net.mcshockwave.UHC.Commands;

import net.mcshockwave.UHC.Listeners.MoleListener;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MoleChatCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (MoleListener.isMole(sender.getName()) && sender instanceof Player) {
			Player p = (Player) sender;
			if (args.length == 0) {
				p.sendMessage(MoleListener.molePre + "Usage: /mc [Message]");
				return false;
			} else {
				String mes = args[0];

				for (int i = 1; i < args.length; i++) {
					mes += " " + args[i];
				}

				MoleListener.sendToMoles("§a[§lMC§a] §f" + p.getDisplayName() + ": " + mes);
			}
		}
		return false;
	}

}
