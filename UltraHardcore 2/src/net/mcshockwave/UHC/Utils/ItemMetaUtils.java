package net.mcshockwave.UHC.Utils;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemMetaUtils {

	public static ItemStack renameItem(ItemStack it, String name) {
		return renameItemNoReset(it, ChatColor.RESET + name);
	}

	public static ItemStack renameItemNoReset(ItemStack it, String name) {
		ItemMeta m = it.getItemMeta();
		m.setDisplayName(name);
		it.setItemMeta(m);
		return it;
	}

	public static String getItemName(ItemStack it) {
		ItemMeta m = it.getItemMeta();
		return m.getDisplayName();
	}

	public static String getItemNameNoReset(ItemStack it) {
		if (getItemName(it) != null) {
			return getItemName(it).replaceFirst(ChatColor.RESET + "", "");
		}
		return null;
	}

	public static boolean hasCustomName(ItemStack it) {
		try {
			getItemName(it).replace("", "");
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static ItemStack setHeadName(ItemStack it, String name) {
		SkullMeta m = (SkullMeta) it.getItemMeta();
		m.setOwner(name);
		it.setItemMeta(m);
		return it;
	}
	
	public static ItemStack setItemName(ItemStack it, String name) {
		ItemMeta m = it.getItemMeta();
		m.setDisplayName(name);
		it.setItemMeta(m);
		return it;
	}
	
	public static boolean hasLore(ItemStack it) {
		try {
			getLoreArray(it)[0].replace("", "");
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static ItemStack addEnchantment(ItemStack it, Enchantment e, int level) {
		ItemMeta m = it.getItemMeta();
		m.addEnchant(e, level, false);
		it.setItemMeta(m);
		return it;
	}

	public static ItemStack setLeatherColor(ItemStack it, Color c) {
		LeatherArmorMeta m = (LeatherArmorMeta) it.getItemMeta();
		m.setColor(c);
		it.setItemMeta(m);
		return it;
	}

	public static ItemStack setLore(ItemStack it, String... lore) {
		ItemMeta m = it.getItemMeta();
		if (lore.length != 1 || !lore[0].equalsIgnoreCase("")) {
			ArrayList<String> lo = new ArrayList<String>();
			for (String s : lore) {
				lo.add(s);
			}
			m.setLore(lo);
		} else {
			m.setLore(null);
		}
		it.setItemMeta(m);
		return it;
	}

	public static String[] getLoreArray(ItemStack it) {
		ItemMeta m = it.getItemMeta();
		List<String> lore = m.getLore();
		if (lore != null && lore.size() > 0) {
			String[] lo = new String[lore.size()];
			for (int i = 0; i < lo.length; i++) {
				lo[i] = lore.get(i);
			}
			if (lo.length > 0) {
				return lo;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public static List<String> getLoreList(ItemStack it) {
		ItemMeta m = it.getItemMeta();
		if (m.getLore() != null && m.getLore().size() > 0) {
			return m.getLore();
		} else {
			return null;
		}
	}
	
	public static Map<Enchantment, Integer> getBookEnchant(ItemStack it) {
		EnchantmentStorageMeta m = (EnchantmentStorageMeta) it.getItemMeta();
		if (m.getStoredEnchants().size() > 0) {
			return m.getStoredEnchants();
		} else {
			return null;
		}
	}
	
	public static ItemStack setBookEnchant(ItemStack it, Object... es) {
		EnchantmentStorageMeta m = (EnchantmentStorageMeta) it.getItemMeta();
		for (int i = 0; i < es.length - 1; i++) {
			Object o = es[i];
			Object n = es[i + 1];
			if (o instanceof Enchantment && n instanceof Integer) {
				Enchantment e = (Enchantment) o;
				int l = (Integer) n;
				m.addStoredEnchant(e, l, true);
			}
		}
		it.setItemMeta(m);
		return it;
	}

}
