package net.mcshockwave.UHC;

import net.mcshockwave.UHC.Utils.ItemMetaUtils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Random;

public class BloodBagListener implements Listener {

	Random	rand	= new Random();

	public ItemStack getBlockedSlotStack() {
		return ItemMetaUtils.setItemName(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14), "§cBlocked Slot");
	}

	public int getBlockedSlots(Player p) {
		int c = 0;

		for (ItemStack it : p.getInventory().getContents()) {
			if (it != null && it.isSimilar(getBlockedSlotStack())) {
				c++;
			}
		}

		return c;
	}

	@SuppressWarnings("deprecation")
	public void updatePlayer(Player p) {
		int curBlocked = getBlockedSlots(p);
		int neededBlocked = (int) (p.getMaxHealth() - p.getHealth());

		if (neededBlocked >= curBlocked) {
			addBlockedSlot(p, neededBlocked - curBlocked);
			p.updateInventory();
		}
		if (curBlocked >= neededBlocked) {
			removeBlockedSlots(p, curBlocked - neededBlocked);
			p.updateInventory();
		}
	}

	public void removeBlockedSlots(Player p, int slots) {
		int after = getBlockedSlots(p) - slots;
		if (after < 0) {
			after = 0;
		}
		PlayerInventory pi = p.getInventory();

		while (getBlockedSlots(p) > after) {
			int id = rand.nextInt(27) + 9;
			if (pi.getItem(id) != null && pi.getItem(id).isSimilar(getBlockedSlotStack())) {
				pi.setItem(id, new ItemStack(Material.AIR));
			}
		}
	}

	public void addBlockedSlot(Player p, int slots) {
		int after = getBlockedSlots(p) + slots;
		if (after > p.getMaxHealth()) {
			after = (int) p.getMaxHealth();
		}
		PlayerInventory pi = p.getInventory();

		while (getBlockedSlots(p) < after) {
			int id = rand.nextInt(27) + 9;
			if (pi.getItem(id) != null && !pi.getItem(id).isSimilar(getBlockedSlotStack()) || pi.getItem(id) == null) {
				ItemStack item = pi.getItem(id);
				if (item != null && item.getType() != Material.AIR) {
					p.getWorld().dropItemNaturally(p.getEyeLocation(), item);
				}
				pi.setItem(id, getBlockedSlotStack());
			}
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		ItemStack it = event.getCurrentItem();

		if (it != null && it.isSimilar(getBlockedSlotStack())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityDamage(final EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Bukkit.getScheduler().runTaskLater(UltraHC.ins, new Runnable() {
				public void run() {
					updatePlayer((Player) event.getEntity());
				}
			}, 1);
		}
	}

	@EventHandler
	public void onEntityRegainHealth(final EntityRegainHealthEvent event) {
		if (event.getEntity() instanceof Player) {
			Bukkit.getScheduler().runTaskLater(UltraHC.ins, new Runnable() {
				public void run() {
					updatePlayer((Player) event.getEntity());
				}
			}, 1);
		}
	}

}
