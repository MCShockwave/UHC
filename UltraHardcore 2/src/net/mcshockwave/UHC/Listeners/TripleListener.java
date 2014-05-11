package net.mcshockwave.UHC.Listeners;

import net.mcshockwave.UHC.Scenarios;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class TripleListener implements Listener {

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Block b = event.getBlock();
		if (b.getType() == Material.IRON_ORE) {
			b.breakNaturally(null);
			event.setExpToDrop(4);
			for (int i = 0; i < 3; i++) {
				b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.IRON_INGOT));
			}
		}

		if (b.getType() == Material.GOLD_ORE) {
			b.breakNaturally(null);
			event.setExpToDrop(8);
			for (int i = 0; i < 3; i++) {
				b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.GOLD_INGOT));
			}
		}

		if (b.getType() == Material.DIAMOND_ORE) {
			for (int i = 0; i < 2; i++) {
				b.getWorld().dropItemNaturally(b.getLocation(),
						new ItemStack(Scenarios.Barebones.isEnabled() ? Material.GOLD_INGOT : Material.DIAMOND));
			}
		}

		if (b.getType() == Material.COAL_ORE) {
			for (int i = 0; i < 2; i++) {
				b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.COAL));
			}
		}
	}

}
