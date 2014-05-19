package net.mcshockwave.UHC.Listeners;

import net.mcshockwave.UHC.UltraHC;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class Compensation implements Listener {

	public static final String	pre	= "§8[§5Compensation§8] §d";

	public int getTeamSize(Team t) {
		int si = 0;

		for (OfflinePlayer op : t.getPlayers()) {
			if (op.isOnline() && UltraHC.getAlive().contains(op.getPlayer())) {
				si++;
			}
		}

		return si;
	}

	public void healTeammates(Team t, Player d) {
		int players = getTeamSize(t) - 1;
		double health = d.getMaxHealth() / players;

		for (OfflinePlayer op : t.getPlayers()) {
			if (op.isOnline() && !op.getName().equalsIgnoreCase(d.getName())) {
				Player p = op.getPlayer();

				double toDamage = p.getMaxHealth() - p.getHealth();

				p.setMaxHealth(p.getMaxHealth() + health);
				p.setHealth(p.getMaxHealth() - toDamage);

				p.sendMessage(pre + "You were given " + getRoundedNumber(health, 2) + " HP for the death of "
						+ d.getName());
			}
		}
	}

	public static double getRoundedNumber(double num, int places) {
		double pla = Math.pow(10, places);

		num = Math.round(num * pla);
		num /= pla;

		return num;
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
