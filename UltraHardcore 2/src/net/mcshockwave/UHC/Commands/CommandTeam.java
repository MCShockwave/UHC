package net.mcshockwave.UHC.Commands;

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

			if (args.length > 0) {
				String c = args[0];

				if (c.equalsIgnoreCase("list")) {
					UltraHC.nts.getMenu(p, false).open(p);
				}

				if (c.equalsIgnoreCase("create")) {
					NumberTeam nt;
					if (args.length > 1) {
						nt = UltraHC.nts.createTeam(args[1], p.getName());
					} else {
						nt = UltraHC.nts.createTeam(null, p.getName());
					}

					nt.addPlayer(p.getName());

					p.sendMessage("§aCreated team with ID " + nt.id
							+ (nt.password == null ? "" : " and password " + nt.password));
				}

				if (c.equalsIgnoreCase("join")) {
					if (args.length > 1) {
						String pass = null;
						int id = Integer.parseInt(args[1]);
						NumberTeam nt = UltraHC.nts.getFromId(id);
						if (args.length > 2) {
							pass = args[2];
						}

						if (nt != null) {
							if (nt.password == null || pass != null && nt.password != null && pass.equals(nt.password)) {
								p.sendMessage("§aJoined team " + id + "!");
								nt.addPlayer(p.getName());
							} else {
								p.sendMessage("§cInvalid password: '" + pass + "'");
								p.sendMessage("§eRemember, passwords are cAsE sEnsITiVE!");
							}
						} else {
							p.sendMessage("§cInvalid team: " + id);
						}
					}
				}

				if (c.equalsIgnoreCase("leave")) {
					if (UltraHC.nts.getTeam(p.getName()) != null) {
						NumberTeam nt = UltraHC.nts.getTeam(p.getName());
						nt.removePlayer(p.getName());
						p.sendMessage("§cLeft team " + nt.id + "!");
					}
				}
			} else {
				p.sendMessage("§8----- §a[Team Commands] §8-----");
				p.sendMessage("§e(Optional) <Required>");
				p.sendMessage("§b/team list §a- List all teams in a nice menu");
				p.sendMessage("§b/team create (password) §a- Create a team (Including password makes password-locked team)");
				p.sendMessage("§b/team join <ID> (password) §a- Join a team with the given ID (Include password if needed)");
				p.sendMessage("§b/team leave §a- Leave your team (If nobody is on the team, it deletes it)");
			}

		}

		return false;
	}
}
