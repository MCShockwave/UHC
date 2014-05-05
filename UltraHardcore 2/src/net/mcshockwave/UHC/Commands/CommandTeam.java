package net.mcshockwave.UHC.Commands;

import net.mcshockwave.UHC.NumberedTeamSystem.NumberTeam;
import net.mcshockwave.UHC.Option;
import net.mcshockwave.UHC.UltraHC;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTeam implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (sender instanceof Player) {
			final Player p = (Player) sender;

			if (args.length > 0) {
				String c = args[0];

				if (c.equalsIgnoreCase("list")) {
					UltraHC.nts.getMenu(p, false).open(p);
					return true;
				}

				if (UltraHC.started) {
					p.sendMessage("§cThat is not enabled right now!");
					return true;
				}

				if (c.equalsIgnoreCase("create")) {
					if (Option.Team_Limit.getInt() <= 0) {
						p.sendMessage("§cTeams are not enabled!");
						return false;
					}

					if (UltraHC.nts.teams.size() > 63) {
						p.sendMessage("§cToo many teams have been created!");
						return false;
					}

					NumberTeam nt;
					if (args.length > 1) {
						nt = UltraHC.nts.createTeam(args[1], p.getName());
					} else {
						nt = UltraHC.nts.createTeam(null, p.getName());
					}

					final NumberTeam fnt = nt;
					Bukkit.getScheduler().runTaskLater(UltraHC.ins, new Runnable() {
						public void run() {
							fnt.addPlayer(p.getName());
						}
					}, 4l);

					p.sendMessage("§aCreated team with ID " + nt.id
							+ (nt.password == null ? "" : " and password " + nt.password));
				}

				if (c.equalsIgnoreCase("leave")) {
					if (UltraHC.nts.getTeam(p.getName()) != null) {
						NumberTeam nt = UltraHC.nts.getTeam(p.getName());
						nt.removePlayer(p.getName());
						p.sendMessage("§cLeft team " + nt.id + "!");
					}
				}

				// owner only
				NumberTeam nt = UltraHC.nts.getTeam(p.getName());
				if (nt != null && nt.owner.equalsIgnoreCase(p.getName())) {
					if (c.equalsIgnoreCase("setpass")) {
						if (!args[1].equalsIgnoreCase("none")) {
							p.sendMessage("§6Set password from '" + nt.password + "' to '" + args[1] + "'");
							nt.password = args[1];
						} else {
							p.sendMessage("§6Removed password!");
							nt.password = null;
						}
					}

					if (c.equalsIgnoreCase("giveowner")) {
						if (!nt.getPlayers().contains(args[1])) {
							p.sendMessage("§6You can't transfer owner to someone not on the team!");
							return false;
						}
						p.sendMessage("§6Transferred owner to " + args[1]);
						if (Bukkit.getPlayer(args[1]) != null) {
							Bukkit.getPlayer(args[1]).sendMessage("§6You are now the owner of team " + nt.id);
						}
						nt.owner = args[1];
					}

					if (c.equalsIgnoreCase("kick")) {
						String toK = args[1];

						if (nt.getPlayers().contains(toK)) {
							nt.removePlayer(toK);
							p.sendMessage("§6Kicked player " + toK + " from your team.");
							if (Bukkit.getPlayer(toK) != null) {
								Bukkit.getPlayer(toK).sendMessage("§6Kicked from team " + nt.id + " by " + p.getName());
							}
						} else {
							p.sendMessage("§cYou can't kick someone not on your team!");
						}
					}
				}
			} else {
				p.sendMessage("§8----- §a[Team Commands] §8-----");
				p.sendMessage("§e(Optional) <Required> §6Requires Team Owner");
				p.sendMessage("§b/team list §a- List all teams in a nice menu, click to join");
				p.sendMessage("§b/team leave §a- Leave your team (If nobody is on the team, it deletes it)");
				p.sendMessage("§8----- §a[Team Management] §8-----");
				p.sendMessage("§b/team create (password) §a- Create a team (Including password makes password-locked team)");
				p.sendMessage("§6/team setpass <password> §a- Set password of team (Use 'none' to remove password)");
				p.sendMessage("§6/team giveowner <name> §a- Transfer your owner to another player");
				p.sendMessage("§6/team kick <name> §a- Kick player from your team");
			}

		}

		return false;
	}
}
