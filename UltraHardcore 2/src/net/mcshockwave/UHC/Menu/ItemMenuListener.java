package net.mcshockwave.UHC.Menu;

import java.util.Map.Entry;

import net.mcshockwave.UHC.Menu.ItemMenu.Button;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class ItemMenuListener implements Listener {

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Inventory i = event.getInventory();
		HumanEntity he = event.getWhoClicked();

		if (he instanceof Player) {
			Player p = (Player) he;

			for (ItemMenu im : ItemMenu.registeredMenus) {
				if (i.getName().equalsIgnoreCase(im.i.getName())) {
					event.setCancelled(true);

					for (Entry<Button, Integer> e : im.buttons.entrySet()) {
						if (e.getValue() == event.getSlot()) {
							e.getKey().onClick.run(p, event);
							
							if (e.getKey().closeInv) {
								p.closeInventory();
							}
						}
					}
					break;
				}
			}
		}
	}

}
