package net.mcshockwave.UHC;

import net.mcshockwave.UHC.Menu.ItemMenu;
import net.mcshockwave.UHC.Menu.ItemMenu.Button;
import net.mcshockwave.UHC.Menu.ItemMenu.ButtonRunnable;
import net.mcshockwave.UHC.Utils.ItemMetaUtils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public enum Option {

	Scenario(
		Material.DIAMOND,
		0,
		"UHC",
		"UHC",
		"Mini UHC",
		"OP Enchants",
		"Crux",
		"Linked",
		"Triple Ores",
		"Hallucinations",
		"Barebones",
		"Mole",
		"Team DM",
		"Resurrect"),
	Spectating(
		Material.THIN_GLASS,
		0,
		"OP Only",
		"OP Only",
		"ALL",
		"Whitelisted Only",
		"Jr Mod",
		"Ender",
		"Nether",
		"Obsidian",
		"Emerald",
		"Diamond",
		"Gold",
		"Iron",
		"Coal"),
	Spread_Radius(
		Material.EYE_OF_ENDER,
		0,
		500,
		1000,
		500,
		250,
		150,
		100),
	No_Kill_Time(
		Material.STONE_SWORD,
		0,
		20,
		30,
		20,
		15,
		10,
		5,
		3,
		2,
		1,
		0),
	Mark_Time(
		Material.PAPER,
		0,
		20,
		30,
		20,
		10,
		5),
	Eternal_Daylight(
		Material.WATCH,
		0,
		true),
	Meet_Up_Time(
		Material.BEDROCK,
		0,
		60,
		60,
		45,
		30,
		20,
		15,
		5,
		0),
	// Border_Radius(
	// Material.BEDROCK,
	// 0,
	// 1000,
	// 1000,
	// 750,
	// 500,
	// 250,
	// 150,
	// 100),
	// Border_Time(
	// Material.ENDER_STONE,
	// 0,
	// 60,
	// 180,
	// 60,
	// 45,
	// 30,
	// 15,
	// 10,
	// 5,
	// 1),
	// Border_Rate(
	// Material.SKULL_ITEM,
	// 1,
	// 2,
	// 10,
	// 5,
	// 4,
	// 3,
	// 2,
	// 1),
	Better_Arrows(
		Material.ARROW,
		0,
		false),
	Hunger(
		Material.APPLE,
		0,
		false),
	Absorption(
		Material.GOLDEN_APPLE,
		0,
		false),
	Death_Distance(
		Material.SKULL_ITEM,
		0,
		true),
	// Hologram_on_Death(
	// Material.NAME_TAG,
	// 0,
	// false),
	Golden_Heads(
		Material.SKULL_ITEM,
		3,
		true),
	Ender_Pearl_Damage(
		Material.ENDER_PEARL,
		0,
		false),
	Head_on_Fence(
		Material.NETHER_FENCE,
		0,
		true),
	Enable_Nether(
		Material.NETHERRACK,
		0,
		true),
	// Damage_Indicators(
	// Material.REDSTONE,
	// 0,
	// false),
	UHC_Mode(
		Material.GOLD_INGOT,
		0,
		true),
	Team_Limit(
		Material.WOOL,
		0,
		0,
		8,
		6,
		5,
		4,
		3,
		2,
		1,
		0);

	public String		name;

	public int			defInt;
	public String		defString;
	public boolean		defBool;

	private int			intVal;
	private int[]		intVals		= null;

	private String		stringVal;
	private String[]	stringVals	= null;

	private boolean		boolVal;

	private ItemStack	icon;

	private Option(Material m, int d, int def, int... others) {
		this.defInt = def;
		this.intVal = def;
		this.intVals = others;

		this.name = name().replace('_', ' ');

		icon = new ItemStack(m, 1, (short) d);
	}

	private Option(Material m, int d, String def, String... others) {
		this.defString = def;
		this.stringVal = def;
		this.stringVals = others;

		this.name = name().replace('_', ' ');

		icon = new ItemStack(m, 1, (short) d);
	}

	private Option(Material m, int d, boolean def) {
		this.defBool = def;
		this.boolVal = def;

		this.name = name().replace('_', ' ');

		icon = new ItemStack(m, 1, (short) d);
	}

	public Class<?> getType() {
		if (intVals != null) {
			return Integer.class;
		} else if (stringVals != null) {
			return String.class;
		} else {
			return Boolean.class;
		}
	}

	public int getInt() {
		return intVal;
	}

	public String getString() {
		return stringVal;
	}

	public boolean getBoolean() {
		return boolVal;
	}

	public void setInt(int i) {
		intVal = i;
	}

	public void setString(String s) {
		stringVal = s;
	}

	public void setBoolean(boolean b) {
		boolVal = b;
	}

	public Object[] getVals() {
		if (getType() == Integer.class) {
			return Arrays.asList(intVals).toArray();
		} else if (getType() == String.class) {
			return stringVals;
		} else {
			return new Object[] { true, false };
		}
	}

	public ItemMenu getMenu() {
		ItemMenu m = new ItemMenu(name, getVals().length + 1);

		if (getType() == Integer.class) {
			for (int i = 0; i < intVals.length; i++) {
				int v = intVals[i];

				Button b = new Button(false, icon.getType(), 1, icon.getDurability(), "" + v, "Click to set");
				m.addButton(b, i);
				b.setOnClick(new ButtonRunnable() {
					public void run(Player p, InventoryClickEvent event) {
						intVal = Integer.parseInt(ItemMetaUtils.getItemName(event.getCurrentItem()).substring(2));
						p.sendMessage("§aSet " + name + " to " + intVal);

						p.closeInventory();
						getGlobalMenu().open(p);
					}
				});
			}
		}

		if (getType() == String.class) {
			final boolean sc = this == Scenario;

			for (int i = 0; i < stringVals.length; i++) {
				String v = stringVals[i];

				Button b = new Button(false, icon.getType(), 1, icon.getDurability(), "" + v, "Click to set");
				m.addButton(b, i);
				b.setOnClick(new ButtonRunnable() {
					public void run(Player p, InventoryClickEvent event) {
						stringVal = ItemMetaUtils.getItemName(event.getCurrentItem()).substring(2);
						p.sendMessage("§aSet " + name + " to " + stringVal);

						if (sc) {
							Bukkit.broadcastMessage("§aScenario changed to " + stringVal);

							Scenarios s = Scenarios.valueOf(stringVal.replace(' ', '_'));
							s.setOptions();
						}

						p.closeInventory();
						getGlobalMenu().open(p);
					}
				});
			}
		}

		if (getType() == Boolean.class) {
			int sl = 0;
			for (String s : new String[] { "True", "False" }) {
				Button b = new Button(false, icon.getType(), 1, icon.getDurability(), s, "Click to set");
				m.addButton(b, sl);
				b.setOnClick(new ButtonRunnable() {
					public void run(Player p, InventoryClickEvent event) {
						boolVal = Boolean.parseBoolean(ItemMetaUtils.getItemName(event.getCurrentItem()).substring(2));
						p.sendMessage("§aSet " + name + " to " + boolVal);

						p.closeInventory();
						getGlobalMenu().open(p);
					}
				});

				sl++;
			}
		}

		return m;
	}

	@Override
	public String toString() {
		if (getType() == Integer.class) {
			return intVal + "";
		}
		if (getType() == String.class) {
			return stringVal;
		}
		if (getType() == Boolean.class) {
			return boolVal + "";
		}
		return "";
	}

	public static ItemMenu getGlobalMenu() {
		return getGlobalMenu(true);
	}

	public static ItemMenu getGlobalMenu(boolean editable) {
		ItemMenu m = new ItemMenu("Options - " + (editable ? "Editable" : "Viewing"), values().length);

		int in = 0;
		for (Option o : values()) {
			Button b = new Button(false, o.icon.getType(), 1, o.icon.getDurability(), o.name, "Current Value: §o"
					+ o.toString());
			m.addButton(b, in);
			if (editable) {
				m.addSubMenu(o.getMenu(), b, true);
			}

			in++;
		}

		return m;
	}

	public static Scenarios getScenario() {
		String na = Option.Scenario.getString().replace(' ', '_');
		Scenarios s = Scenarios.valueOf(na);
		return s;
	}

}
