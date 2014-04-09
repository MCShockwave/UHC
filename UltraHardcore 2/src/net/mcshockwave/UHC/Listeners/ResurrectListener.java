package net.mcshockwave.UHC.Listeners;

import net.mcshockwave.UHC.UltraHC;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;

public class ResurrectListener implements Listener {

	public static HashMap<String, Location>	resu	= new HashMap<>();

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Block b = event.getBlock();
		Block u = b.getRelative(0, -1, 0);
		Player p = event.getPlayer();

		if (b.getType() == Material.SKULL) {
			Skull s = (Skull) b.getState();
			String name = s.getOwner();

			if (u.getType() == Material.GOLD_BLOCK) {

				if (resu.containsKey(name)) {
					p.sendMessage("§cThat player has already been resurrected!");
					event.setBuild(false);
					return;
				}

				u.setType(Material.AIR);
				b.setType(Material.AIR);
				resu.put(name, u.getLocation());

				Bukkit.broadcastMessage("§e§l" + name + " has been resurrected!");
				UltraHC.addPlayer(name);
				
				if (Bukkit.getPlayer(name) != null) {
					Bukkit.getPlayer(name).kickPlayer("Reconnect to be resurrected!");
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();

		if (resu.containsKey(p.getName()) && !UltraHC.specs.contains(p.getName())) {
			p.teleport(resu.get(p.getName()));
			p.setAllowFlight(false);
			p.setHealth(p.getMaxHealth());
			p.setGameMode(GameMode.SURVIVAL);
			for (Player p2 : Bukkit.getOnlinePlayers()) {
				p2.showPlayer(p);
			}
		}
	}

}
