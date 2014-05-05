package net.mcshockwave.UHC.Commands;

import net.mcshockwave.UHC.NumberedTeamSystem;
import net.mcshockwave.UHC.NumberedTeamSystem.NumberTeam;
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

			if (args.length > 1 && args[0].equalsIgnoreCase("new")) {
				NumberedTeamSystem ts = UltraHC.nts;
				String c = args[1];

				if (c.equalsIgnoreCase("create")) {
					NumberTeam nt = ts.createTeam(args[2], p.getName());
					p.sendMessage("§cCreated team " + nt.id + " with password " + nt.password);
				}
				
				if (c.equalsIgnoreCase("menu")) {
					ts.getMenu(p, true).open(p);
				}
				
				if (c.equalsIgnoreCase("join")) {
					int id = Integer.parseInt(args[2]);
					String targ = p.getName();
					
					if (args.length > 3) {
						targ = args[3];
					}
					
					NumberTeam nt = ts.getFromId(id);
					nt.addPlayer(targ);
				}
				
				if (c.equalsIgnoreCase("leave")) {
					int id = Integer.parseInt(args[2]);
					String targ = p.getName();
					
					if (args.length > 3) {
						targ = args[3];
					}
					
					NumberTeam nt = ts.getFromId(id);
					nt.removePlayer(targ);
				}

				return true;
			}

			if (UltraHC.started) {
				p.sendMessage("§cYou can't use this command after the game has started!");
			} else {
				UltraHC.nts.getMenu(p, false).open(p);
			}

		}

		return false;
	}

}
