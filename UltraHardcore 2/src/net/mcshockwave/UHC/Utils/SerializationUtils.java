package net.mcshockwave.UHC.Utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Map.Entry;

public class SerializationUtils {

	@SuppressWarnings("deprecation")
	public static String toString(ItemStack it) {
		int id = it.getTypeId();
		int am = it.getAmount();
		short dt = it.getDurability();

		String meta = "";
		if (it.hasItemMeta()) {
			ItemMeta im = it.getItemMeta();
			if (im.hasDisplayName()) {
				meta += ">dn/" + im.getDisplayName().replace('§', '&');
			}
			if (im.hasLore()) {
				String lor = "";
				for (String s : im.getLore()) {
					lor += s.replace('§', '&') + "||";
				}
				meta += ">l/" + lor.substring(0, lor.length() - 2);
			}
			if (im.hasEnchants()) {
				String ench = "";
				for (Entry<Enchantment, Integer> enc : im.getEnchants().entrySet()) {
					ench += enc.getKey().getId() + "-" + enc.getValue() + "|";
				}
				meta += ">e/" + ench.substring(0, ench.length() - 1);
			}
		}

		return id + ":" + am + ":" + dt + (meta.length() > 0 ? (":" + meta) : "");
	}

	@SuppressWarnings("deprecation")
	public static ItemStack itemFromString(String ser) {
		String[] ids = ser.split(":");
		Material m = Material.getMaterial(Integer.parseInt(ids[0]));
		int am = Integer.parseInt(ids[1]);
		short dt = Short.parseShort(ids[2]);

		ItemStack ret = new ItemStack(m, am, dt);

		if (ids.length > 3) {
			String meta = "";
			for (int i = 3; i < ids.length; i++) {
				meta += ids[i] + ":";
			}
			meta = meta.substring(0, meta.length() - 1);

			if (meta.contains(">e/")) {
				int ind = meta.indexOf(">e/");
				String ench = meta.substring(ind + 3, meta.length());
				meta = meta.substring(0, ind);

				String[] enchs = ench.split("\\|");
				for (String s : enchs) {
					String[] spl = s.split("-");

					ItemMetaUtils.addEnchantment(ret, Enchantment.getById(Integer.parseInt(spl[0])),
							Integer.parseInt(spl[1]));
				}
			}
			if (meta.contains(">l/")) {
				int ind = meta.indexOf(">l/");
				String lore = meta.substring(ind + 3, meta.length());
				meta = meta.substring(0, ind);

				ArrayList<String> loreList = new ArrayList<>();
				String[] lores = lore.split("\\|\\|");
				for (String lo : lores) {
					String l = ChatColor.translateAlternateColorCodes('&', lo);
					if (l.startsWith("§7")) {
						l = l.replaceFirst("§7", "");
					}
					loreList.add(l);
				}
				ItemMetaUtils.setLore(ret, loreList.toArray(new String[0]));
			}
			if (meta.contains(">dn/")) {
				int ind = meta.indexOf(">dn/");
				String name = meta.substring(ind + 4, meta.length());
				meta = meta.substring(0, ind);

				ItemMetaUtils.setItemName(ret, ChatColor.translateAlternateColorCodes('&', name));
			}
		}

		return ret;
	}

	public static String toString(ItemStack[] contents) {
		String ret = "";

		for (int i = 0; i < contents.length; i++) {
			ItemStack it = contents[i];
			if (it != null && it.getType() != Material.AIR) {
				ret += i + "," + toString(it) + "*";
			}
		}

		return ret.substring(0, ret.length() > 0 ? ret.length() - 1 : 0);
	}

	public static ItemStack[] itemsFromString(String ser, int size) {
		ItemStack[] ret = new ItemStack[size];

		if (ser.length() > 0) {
			String[] items = ser.split("\\*");

			for (String s : items) {
				int indx = s.indexOf(',');

				int slot = Integer.parseInt(s.substring(0, indx));
				ItemStack item = itemFromString(s.substring(indx + 1, s.length()));

				ret[slot] = item;
			}
		}

		return ret;
	}
}
