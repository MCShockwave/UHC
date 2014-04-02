package net.mcshockwave.UHC.Listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class BarebonesListener implements Listener {

	Random	r	= new Random();

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		int i = r.nextInt(3) + 1;
		for (int a = 0; a < i; i++) {
			e.getDrops().add(new ItemStack(Material.DIAMOND));
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Block b = e.getBlock();
		if (b.getType() == Material.DIAMOND_ORE) {
			b.breakNaturally(null);
			b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.GOLD_INGOT));
		}
	}

}
