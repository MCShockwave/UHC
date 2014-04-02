package net.mcshockwave.UHC.Listeners;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class BarebonesListener implements Listener {
	
	Random r = new Random();
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		int i = r.nextInt(4);
		for (int a = 0; a < i; i++) {
			p.getWorld().dropItemNaturally(p.getLocation(), new ItemStack(Material.DIAMOND));
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Block b = e.getBlock();
		if (b.getType() == Material.DIAMOND_ORE) {
			b.breakNaturally(null);
			for (int i = 0; i < 3; i++) {
				b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.GOLD_INGOT));
			}
		}
	}
	
}
