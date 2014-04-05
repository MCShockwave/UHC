package net.mcshockwave.UHC.Listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.Random;

public class BarebonesListener implements Listener {

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		int i = new Random().nextInt(3) + 1;
		for (int a = 0; a < i; a++) {
			e.getDrops().add(new ItemStack(Material.DIAMOND));
		}
		e.getDrops().add(new ItemStack(Material.GOLDEN_APPLE));
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Block b = e.getBlock();
		if (b.getType() == Material.DIAMOND_ORE) {
			b.breakNaturally(null);
			b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.GOLD_INGOT));
		}
	}

	@EventHandler
	public void onCraftItem(PrepareItemCraftEvent event) {
		Recipe r = event.getRecipe();
		CraftingInventory ci = event.getInventory();
		if (r.getResult().getType() == Material.ENCHANTMENT_TABLE || r.getResult().getType() == Material.ANVIL) {
			ci.setResult(new ItemStack(Material.AIR));
		}
	}

}
