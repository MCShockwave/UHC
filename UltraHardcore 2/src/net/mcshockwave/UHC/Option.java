package net.mcshockwave.UHC;

import net.mcshockwave.UHC.Menu.ItemMenu;
import net.mcshockwave.UHC.Menu.ItemMenu.Button;
import net.mcshockwave.UHC.Menu.ItemMenu.ButtonRunnable;
import net.mcshockwave.UHC.Utils.ItemMetaUtils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public enum Option {

	Scenario_List(
		Material.DIAMOND,
		0,
		"UHC"),
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
	PVP_Time(
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
		120,
		90,
		60,
		60,
		45,
		30,
		20,
		15,
		5,
		2,
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
	Hunger(
		Material.COOKED_BEEF,
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
	Increased_Apples(
		Material.APPLE,
		0,
		true),
	Increased_Flint(
		Material.FLINT,
		0,
		true),
	Head_on_Fence(
		Material.NETHER_FENCE,
		0,
		true),
	Enable_Nether(
		Material.NETHERRACK,
		0,
		true),
	Strength_Potions(
		Material.BLAZE_POWDER,
		0,
		false),
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
		4),
	Max_Teams(
		Material.WOOL,
		0,
		64,
		64,
		32,
		16,
		15,
		14,
		13,
		12,
		11,
		10,
		9,
		8,
		7,
		6,
		5,
		4,
		3,
		2,
		1,
		0),
	Team_Commands(
		Material.WOOL,
		0,
		false);

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

	public int getValLength() {
		if (this == Scenario_List) {
			return Scenarios.values().length;
		}

		if (getType() == Integer.class) {
			return intVals.length;
		} else if (getType() == String.class) {
			return stringVals.length;
		} else {
			return 2;
		}
	}

	public Object[] getVals() {
		if (getType() == Integer.class) {
			return Arrays.asList(intVals).toArray(new Object[0]);
		} else if (getType() == String.class) {
			return stringVals;
		} else {
			return new Object[] { true, false };
		}
	}

	public ItemMenu getMenu(final ItemMenu from) {
		ItemMenu m = new ItemMenu(name, getValLength() + 1);

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
						getGlobalMenu(true).open(p);
					}
				});
			}
		}

		if (getType() == String.class) {
			if (this == Scenario_List) {
				for (int sid = 0; sid < Scenarios.values().length; sid++) {
					final Scenarios s = Scenarios.values()[sid];

					Button b = new Button(false, icon.getType(), 1, icon.getDurability(), s.name().replace('_', ' '),
							"§eClick to " + (s.isEnabled() ? "§cdisable" : "§aenable"));
					if (s.isEnabled()) {
						ItemMetaUtils.addEnchantment(b.button, Enchantment.WATER_WORKER, 1);
					}
					m.addButton(b, sid);
					b.setOnClick(new ButtonRunnable() {
						public void run(Player p, InventoryClickEvent event) {
							s.setEnabled(!s.isEnabled());

							p.closeInventory();
							getMenu(getGlobalMenu(true)).open(p);
						}
					});
				}

				Button back = new Button(false, Material.STICK, 1, 0, "Back", "Click to go back");
				m.addButton(back, m.i.getSize() - 1);
				m.addSubMenu(from, back);
			} else {
				for (int i = 0; i < stringVals.length; i++) {
					String v = stringVals[i];

					Button b = new Button(false, icon.getType(), 1, icon.getDurability(), "" + v, "Click to set");
					m.addButton(b, i);
					b.setOnClick(new ButtonRunnable() {
						public void run(Player p, InventoryClickEvent event) {
							stringVal = ItemMetaUtils.getItemName(event.getCurrentItem()).substring(2);
							p.sendMessage("§aSet " + name + " to " + stringVal);

							p.closeInventory();
							getGlobalMenu(true).open(p);
						}
					});
				}
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
						getGlobalMenu(true).open(p);
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

	public static ItemMenu getGlobalMenu(boolean editable) {
		ItemMenu m = new ItemMenu("Options - " + (editable ? "Editable" : "Viewing"), values().length);

		int in = 0;
		for (Option o : values()) {
			Button b = new Button(false, o.icon.getType(), 1, o.icon.getDurability(), o.name,
					(o == Scenario_List ? (editable ? "Click to open menu" : "Type /scenarios to view")
							: "Current Value: §o" + o.toString()));
			m.addButton(b, in);
			if (editable) {
				m.addSubMenu(o.getMenu(m), b, !(o == Scenario_List));
			}

			in++;
		}

		return m;
	}

}
