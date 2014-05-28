package net.mcshockwave.UHC.Listeners;

import net.mcshockwave.UHC.UltraHC;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;

public class Compensation implements Listener {

	public static final String				pre			= "§8[§5Compensation§8] §d";

	public static HashMap<String, Double>	deathHeal	= new HashMap<>();

	public int getTeamSize(Team t) {
		int si = 0;

		for (OfflinePlayer op : t.getPlayers()) {
			if (canBeHealed(op)) {
				si++;
			}
		}

		return si;
	}

	public boolean canBeHealed(OfflinePlayer op) {
		return !UltraHC.specs.contains(op.getName());
	}

	public void healTeammates(Team t, Player d) {
		int players = getTeamSize(t) - 1;
		double health = d.getMaxHealth() / players;

		for (OfflinePlayer op : t.getPlayers()) {
			if (!op.getName().equalsIgnoreCase(d.getName()) && canBeHealed(op)) {
				if (op.isOnline()) {
					Player p = op.getPlayer();

					healPlayer(p, health, d.getName());
				} else {
					double add = 0;
					if (deathHeal.containsKey(op.getName())) {
						add = deathHeal.get(op.getName());
						deathHeal.remove(op.getName());
					}
					deathHeal.put(op.getName(), health + add);
				}
			}
		}
	}

	public void healPlayer(Player p, double health, String name) {
		double toDamage = p.getMaxHealth() - p.getHealth();

		p.setMaxHealth(p.getMaxHealth() + health);
		p.setHealth(p.getMaxHealth() - toDamage);

		p.sendMessage(pre + "You were given " + getRoundedNumber(health, 2) + " HP for the death of " + name);
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

	@EventHandler
	public void onCraftItem(PrepareItemCraftEvent event) {
		Recipe r = event.getRecipe();
		CraftingInventory ci = event.getInventory();
		if (r.getResult().getType() == Material.ARROW) {
			ci.setResult(new ItemStack(Material.ARROW, 16));
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		final Player p = event.getPlayer();

		if (deathHeal.containsKey(p.getName())) {
			Bukkit.getScheduler().runTaskLater(UltraHC.ins, new Runnable() {
				public void run() {
					healPlayer(p, deathHeal.get(p.getName()), "a teammate");
				}
			}, 10l);
		}
	}

	@EventHandler
	public void onPlayerRegainHealth(EntityRegainHealthEvent event) {
		if (event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();

			event.setAmount(p.getMaxHealth() * 0.05);
		}
	}
}
