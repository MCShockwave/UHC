package net.mcshockwave.UHC.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class Compensation implements Listener {

	public static final String	pre		= "§8[§5Compensation§8] §d";

	public static Compensation	comp	= new Compensation();

	public int getTeamSize(Team t) {
		int si = 0;

		for (OfflinePlayer op : t.getPlayers()) {
			if (op.isOnline()) {
				si++;
			}
		}

		return si;
	}

	public void healTeammates(Team t, Player d) {
		int players = getTeamSize(t) - 1;
		double health = d.getMaxHealth() / players;

		for (OfflinePlayer op : t.getPlayers()) {
			if (op.isOnline() && op != d) {
				Player p = op.getPlayer();

				p.setMaxHealth(p.getMaxHealth() + health);
				p.setHealth(p.getHealth() + health);

				p.sendMessage(pre + "You were given " + health + " HP for the death of " + d.getName());
			}
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player p = event.getEntity();
		Scoreboard sc = Bukkit.getScoreboardManager().getMainScoreboard();

		if (sc.getPlayerTeam(p) != null) {
			healTeammates(sc.getPlayerTeam(p), p);
		}
	}
}
