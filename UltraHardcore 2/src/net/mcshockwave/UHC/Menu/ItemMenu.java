package net.mcshockwave.UHC.Menu;

import net.mcshockwave.UHC.UltraHC;
import net.mcshockwave.UHC.Utils.ItemMetaUtils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class ItemMenu {

	public Inventory					i;
	public HashMap<Button, Integer>		buttons			= new HashMap<>();

	public static ArrayList<ItemMenu>	registeredMenus	= new ArrayList<>();

	public ItemMenu(String name, int slots) {
		i = Bukkit.createInventory(null, ((slots + 8) / 9) * 9, name);

		for (ItemMenu im : registeredMenus.toArray(new ItemMenu[0])) {
			if (im.i.getName().equalsIgnoreCase(name)) {
				registeredMenus.remove(im);
			}
		}

		registeredMenus.add(this);
	}

	public ItemMenu addSubMenu(final ItemMenu m, Button tri) {
		return addSubMenu(m, tri, false);
	}

	public ItemMenu addSubMenu(final ItemMenu m, Button tri, boolean backButton) {
		tri.setOnClick(new ButtonRunnable() {
			public void run(final Player p, InventoryClickEvent event) {
				p.closeInventory();

				Bukkit.getScheduler().runTaskLater(UltraHC.ins, new Runnable() {
					public void run() {
						m.open(p);
					}
				}, 1l);
			}
		});

		if (backButton) {
			Button b = new Button(false, Material.STICK, 1, 0, "Back", "Click to go back");
			m.addButton(b, m.i.getSize() - 1);
			m.addSubMenu(this, b);
		}

		return this;
	}

	public Button addButton(Button b, int slot) {
		i.setItem(slot, b.button);
		buttons.put(b, slot);
		return b;
	}

	public ItemMenu open(Player p) {
		p.openInventory(i);
		return this;
	}

	public static class Button {
		public boolean			closeInv;
		public ItemStack		button;
		public ButtonRunnable	onClick;

		public Button(boolean closeInv, Material m, int amount, int data, String name, String... lore) {
			button = ItemMetaUtils.setLore(
					ItemMetaUtils.setItemName(new ItemStack(m, amount, (short) data), "§r" + name), lore);
			onClick = new ButtonRunnable() {
				public void run(Player p, InventoryClickEvent event) {
				}
			};
			this.closeInv = closeInv;
		}

		public Button setOnClick(ButtonRunnable r) {
			onClick = r;
			return this;
		}

		public ItemMenu getItemMenu() {
			for (ItemMenu m : ItemMenu.registeredMenus) {
				if (m.buttons.containsKey(this)) {
					return m;
				}
			}
			throw new IllegalArgumentException("No Item Menu present!");
		}
	}

	public static interface ButtonRunnable {
		public void run(Player p, InventoryClickEvent event);
	}

}
