package net.mcshockwave.UHC.Listeners;

import net.mcshockwave.UHC.Option;
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

		int mult = Option.Ore_Multiplier.getInt();

		if (b.getType() == Material.IRON_ORE) {
			b.breakNaturally(null);
			event.setExpToDrop(4);
			for (int i = 0; i < mult; i++) {
				b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.IRON_INGOT));
			}
		}

		if (b.getType() == Material.GOLD_ORE) {
			b.breakNaturally(null);
			event.setExpToDrop(8);
			for (int i = 0; i < mult; i++) {
				b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.GOLD_INGOT));
			}
		}

		if (b.getType() == Material.DIAMOND_ORE) {
			for (int i = 0; i < mult - 1; i++) {
				b.getWorld().dropItemNaturally(b.getLocation(),
						new ItemStack(Scenarios.Barebones.isEnabled() ? Material.GOLD_INGOT : Material.DIAMOND));
			}
		}

		if (b.getType() == Material.COAL_ORE) {
			for (int i = 0; i < mult - 1; i++) {
				b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.COAL));
			}
		}

		if (b.getType() == Material.EMERALD_ORE) {
			for (int i = 0; i < mult - 1; i++) {
				b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.EMERALD));
			}
		}

		if (b.getType() == Material.REDSTONE) {
			for (int i = 0; i < (mult - 1) * 5; i++) {
				b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.REDSTONE));
			}
		}

		if (b.getType() == Material.LAPIS_ORE) {
			for (int i = 0; i < (mult - 1) * 7; i++) {
				b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.INK_SACK, 1, (short) 4));
			}
		}
	}

}
