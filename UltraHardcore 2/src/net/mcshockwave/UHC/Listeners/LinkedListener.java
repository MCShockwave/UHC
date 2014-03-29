package net.mcshockwave.UHC.Listeners;

import net.mcshockwave.UHC.Option;
import net.mcshockwave.UHC.UltraHC;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class LinkedListener implements Listener {

	public static HashMap<Team, Integer>	startSize	= new HashMap<>();

	public static void onStart() {
		UltraHC.ts.setScores();

		Scoreboard s = Bukkit.getScoreboardManager().getMainScoreboard();

		for (Team t : s.getTeams()) {
			startSize.put(t, t.getSize());
		}
		
		Bukkit.broadcastMessage("§aAll teams linked");
	}

	public static List<Player> getLinkedTo(Player p) {
		List<Player> ret = new ArrayList<>();
		Scoreboard s = Bukkit.getScoreboardManager().getMainScoreboard();

		if (s.getPlayerTeam(p) != null) {
			for (OfflinePlayer op : s.getPlayerTeam(p).getPlayers()) {
				if (op.isOnline()) {
					ret.add((Player) op);
				}
			}
		}

		return ret;
	}

	public static Entry<Team, Integer> getTeamEntry(Player p) {
		Scoreboard s = Bukkit.getScoreboardManager().getMainScoreboard();

		for (Entry<Team, Integer> e : startSize.entrySet()) {
			if (s.getPlayerTeam(p) == e.getKey()) {
				return e;
			}
		}
		return null;
	}

	@EventHandler
	public void onPlayerDamage(EntityDamageEvent event) {
		Entity ee = event.getEntity();

		if (ee instanceof Player) {
			final Player p = (Player) ee;

			if (UltraHC.getAlive().contains(p)) {
				int div = getTeamEntry(p).getValue();
				double dam = event.getDamage() / div;
				event.setDamage(dam);

				Bukkit.getScheduler().runTaskLater(UltraHC.ins, new Runnable() {
					public void run() {
						for (Player p2 : getLinkedTo(p)) {
							if (p2.getHealth() != p.getHealth()) {
								p2.setHealth(p.getHealth());
								p2.damage(0);
							}
						}
					}
				}, 1l);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRegainHealth(EntityRegainHealthEvent event) {
		Entity e = event.getEntity();
		if (e instanceof Player && Option.UHC_Mode.getBoolean()) {
			final Player p = (Player) e;

			int div = getTeamEntry(p).getValue();
			double he = event.getAmount() / div;
			event.setAmount(he);

			Bukkit.getScheduler().runTaskLater(UltraHC.ins, new Runnable() {
				public void run() {
					for (Player p2 : getLinkedTo(p)) {
						if (p2.getHealth() != p.getHealth()) {
							p2.setHealth(p.getHealth());
						}
					}
				}
			}, 1l);
		}
	}
}
