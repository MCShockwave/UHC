package net.mcshockwave.UHC;

import net.mcshockwave.UHC.Commands.ScenarioListCommand;
import net.mcshockwave.UHC.Menu.ItemMenu;
import net.mcshockwave.UHC.Menu.ItemMenu.Button;
import net.mcshockwave.UHC.Menu.ItemMenu.ButtonRunnable;
import net.mcshockwave.UHC.Utils.ItemMetaUtils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public enum Option {

	Scenario_List(
		Category.Scenarios,
		Material.DIAMOND,
		0,
		"UHC",
		"ERROR: Check Console"),
	Spectating(
		Category.Game_Settings,
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
		Category.Game_Settings,
		Material.EYE_OF_ENDER,
		0,
		500,
		1000,
		500,
		250,
		150,
		100),
	PVP_Time(
		Category.Game_Settings,
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
		Category.Game_Settings,
		Material.PAPER,
		0,
		20,
		30,
		20,
		10,
		5),
	Eternal_Daylight(
		Category.Game_Settings,
		Material.WATCH,
		0,
		true),
	Game_Length(
		Category.End_Game,
		Material.BEDROCK,
		0,
		60,
		120,
		90,
		60,
		45,
		30,
		20,
		15,
		5,
		2,
		0),
	End_Game(
		Category.End_Game,
		Material.COMPASS,
		0,
		"Meetup",
		"Meetup",
		"Sudden Death",
		"Compasses (Not Added)"),
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
		Category.Game_Settings,
		Material.COOKED_BEEF,
		0,
		false),
	Absorption(
		Category.Game_Settings,
		Material.GOLDEN_APPLE,
		0,
		false),
	Death_Distance(
		Category.Game_Settings,
		Material.SKULL_ITEM,
		0,
		true),
	// Hologram_on_Death(
	// Material.NAME_TAG,
	// 0,
	// false),
	Golden_Heads(
		Category.Game_Settings,
		Material.SKULL_ITEM,
		3,
		true),
	Ender_Pearl_Damage(
		Category.Game_Settings,
		Material.ENDER_PEARL,
		0,
		false),
	Increased_Apples(
		Category.Game_Settings,
		Material.APPLE,
		0,
		true),
	Increased_Flint(
		Category.Game_Settings,
		Material.FLINT,
		0,
		true),
	Head_on_Fence(
		Category.Game_Settings,
		Material.NETHER_FENCE,
		0,
		true),
	Dead_Bodies(
		Category.Game_Settings,
		Material.SKULL_ITEM,
		1,
		false),
	Show_Nametags(
		Category.Game_Settings,
		Material.NAME_TAG,
		0,
		true),
	Enable_Nether(
		Category.Game_Settings,
		Material.NETHERRACK,
		0,
		true),
	Strength_Potions(
		Category.Game_Settings,
		Material.BLAZE_POWDER,
		0,
		false),
	Lives(
		Category.End_Game,
		Material.BEACON,
		0,
		"NO RESPAWNS",
		"INFINITE",
		"50",
		"25",
		"20",
		"15",
		"10",
		"5",
		"1",
		"NO RESPAWNS"),
	// Damage_Indicators(
	// Material.REDSTONE,
	// 0,
	// false),
	UHC_Mode(
		Category.Game_Settings,
		Material.GOLD_INGOT,
		0,
		true),
	Team_Limit(
		Category.Teams,
		Material.WOOL,
		0,
		1,
		8,
		6,
		5,
		4,
		3,
		2,
		1,
		0),
	Max_Teams(
		Category.Teams,
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
		Category.Teams,
		Material.WOOL,
		0,
		false),
	Friendly_Fire(
		Category.Teams,
		Material.GOLD_SWORD,
		0,
		false),

	// Scenario-Based
	Base_Height(
		Scenarios.DTM,
		Material.ENDER_STONE,
		0,
		100,
		256,
		150,
		140,
		130,
		120,
		110,
		100,
		90,
		80,
		70,
		64,
		16),
	Wither_on_Break(
		Scenarios.DTM,
		Material.SKULL_ITEM,
		1,
		true),
	Ore_Multiplier(
		Scenarios.Triple_Ores,
		Material.IRON_INGOT,
		0,
		3,
		10,
		5,
		4,
		3,
		2),
	Extra_Drops(
		Scenarios.Barebones,
		Material.GOLDEN_APPLE,
		0,
		true),
	Chump_Charity_Interval(
		Scenarios.Chump_Charity,
		Material.POTION,
		0,
		10,
		15,
		10,
		8,
		5,
		4,
		3,
		2,
		1),
	Blood_Price_Interval(
		Scenarios.Blood_Price,
		Material.POTION,
		0,
		10,
		15,
		10,
		8,
		5,
		4,
		3,
		2,
		1),
	Weakest_Link_Interval(
		Scenarios.Weakest_Link,
		Material.POTION,
		0,
		10,
		15,
		10,
		8,
		5,
		4,
		3,
		2,
		1),
	Skyhigh_Enable(
		Scenarios.Skyhigh,
		Material.DIRT,
		0,
		45,
		120,
		90,
		60,
		45,
		30,
		15,
		10,
		5,
		0),
	Skyhigh_Damage_Interval(
		Scenarios.Skyhigh,
		Material.COBBLESTONE,
		0,
		30,
		60,
		45,
		30,
		15,
		5),
	HG_Grace_Period(
		Scenarios.Hunger_Games,
		Material.STONE_SWORD,
		0,
		20,
		120,
		60,
		45,
		30,
		20,
		10,
		5,
		0),
	HG_Regen_Off(
		Scenarios.Hunger_Games,
		Material.GOLDEN_APPLE,
		0,
		true),
	HG_Game_Handling(
		Scenarios.Hunger_Games,
		Material.GRASS,
		0,
		true);

	public String		name	= name().replace('_', ' ');

	public String		defVal;

	private String		val;
	private String[]	vals	= new String[0];

	private ItemStack	icon;

	public Category		cat		= null;
	public Scenarios	scen	= null;

	private Option(Category cat, Material m, int d, int def, Integer... others) {
		this.cat = cat;

		this.defVal = def + "";
		this.val = defVal;
		this.vals = strAry(others);

		icon = new ItemStack(m, 1, (short) d);
	}

	private Option(Category cat, Material m, int d, String def, String... others) {
		this.cat = cat;

		this.defVal = def;
		this.val = defVal;
		this.vals = others;

		icon = new ItemStack(m, 1, (short) d);
	}

	private Option(Category cat, Material m, int d, boolean def) {
		this.cat = cat;

		this.defVal = def + "";
		this.val = defVal;
		this.vals = new String[] { "true", "false" };

		icon = new ItemStack(m, 1, (short) d);
	}

	private Option(Scenarios scen, Material m, int d, int def, Integer... others) {
		this.scen = scen;

		this.defVal = def + "";
		this.val = defVal;
		this.vals = strAry(others);

		icon = new ItemStack(m, 1, (short) d);
	}

	private Option(Scenarios scen, Material m, int d, String def, String... others) {
		this.scen = scen;

		this.defVal = def;
		this.val = defVal;
		this.vals = others;

		icon = new ItemStack(m, 1, (short) d);
	}

	private Option(Scenarios scen, Material m, int d, boolean def) {
		this.scen = scen;

		this.defVal = def + "";
		this.val = defVal;
		this.vals = new String[] { "true", "false" };

		icon = new ItemStack(m, 1, (short) d);
	}

	public String[] strAry(Integer[] vs) {
		List<String> ret = new ArrayList<>();

		for (int v : vs) {
			ret.add(v + "");
		}

		return ret.toArray(new String[0]);
	}

	public boolean isInt() {
		try {
			for (String s : vals) {
				Integer.parseInt(s);
			}
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	public boolean isBoolean() {
		return val.equalsIgnoreCase("true") || val.equalsIgnoreCase("false");
	}

	public boolean isString() {
		return !isInt() && !isBoolean();
	}

	public Class<?> getType() {
		if (isInt()) {
			return Integer.class;
		} else if (isBoolean()) {
			return Boolean.class;
		}
		return String.class;
	}

	public int getInt() {
		return Integer.parseInt(val);
	}

	public String getString() {
		return val;
	}

	public boolean getBoolean() {
		return Boolean.parseBoolean(val);
	}

	public void set(int i) {
		val = "" + i;
	}

	public void set(String s) {
		val = s;
	}

	public void set(boolean b) {
		val = "" + b;
	}

	public int getValLength() {
		if (this == Scenario_List) {
			return Scenarios.values().length;
		}

		return vals.length;
	}

	public ItemMenu getMenu(final ItemMenu from) {
		ItemMenu m = new ItemMenu(name, getValLength() + 1);

		if (getType() == Integer.class) {
			for (int i = 0; i < vals.length; i++) {
				String v = vals[i];

				Button b = new Button(false, icon.getType(), 1, icon.getDurability(), v, "Click to set");
				m.addButton(b, i);
				b.setOnClick(new ButtonRunnable() {
					public void run(Player p, InventoryClickEvent event) {
						set(ItemMetaUtils.getItemName(event.getCurrentItem()).substring(2));
						p.sendMessage("§aSet " + name + " to " + val);

						p.closeInventory();
						getMenuFor(cat == null ? scen : cat, true).open(p);
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
							getMenu(getMenuFor(cat == null ? scen : cat, true)).open(p);
						}
					});
				}

				Button back = new Button(false, Material.STICK, 1, 0, "Back", "Click to go back");
				m.addButton(back, m.i.getSize() - 1);
				m.addSubMenu(from, back);
			} else {
				for (int i = 0; i < vals.length; i++) {
					String v = vals[i];

					Button b = new Button(false, icon.getType(), 1, icon.getDurability(), "" + v, "Click to set");
					m.addButton(b, i);
					b.setOnClick(new ButtonRunnable() {
						public void run(Player p, InventoryClickEvent event) {
							set(ItemMetaUtils.getItemName(event.getCurrentItem()).substring(2));
							p.sendMessage("§aSet " + name + " to " + val);

							p.closeInventory();
							getMenuFor(cat == null ? scen : cat, true).open(p);
						}
					});
				}
			}
		}

		if (getType() == Boolean.class) {
			int sl = 0;
			for (String s : vals) {
				Button b = new Button(false, icon.getType(), 1, icon.getDurability(), s, "Click to set");
				m.addButton(b, sl);
				b.setOnClick(new ButtonRunnable() {
					public void run(Player p, InventoryClickEvent event) {
						set(ItemMetaUtils.getItemName(event.getCurrentItem()).substring(2));
						p.sendMessage("§aSet " + name + " to " + val);

						p.closeInventory();
						getMenuFor(cat == null ? scen : cat, true).open(p);
					}
				});

				sl++;
			}
		}

		return m;
	}

	@Override
	public String toString() {
		return val;
	}

	public static ItemMenu getOptionsMenu(final boolean editable) {
		ItemMenu m = new ItemMenu("Options - " + (editable ? "Editable" : "Viewing"), Category.values().length);

		int in = 0;
		for (Category c : Category.values()) {

			Button b = new Button(false, c.ico, 1, c.icodata, c.name, "Click to open category");
			m.addButton(b, in);
			m.addSubMenu(getMenuFor(c, editable), b);

			in++;
		}

		return m;
	}

	public static ItemMenu getMenuFor(final Enum<?> category, final boolean editable) {
		ItemMenu m = new ItemMenu(category.name().replace('_', ' ') + " - " + (editable ? "Editable" : "Viewing"),
				getOptionsFor(category).size());

		int in = 0;
		for (Option o : getOptionsFor(category)) {
			Button b = new Button(false, o.icon.getType(), 1, o.icon.getDurability(), o.name,
					(o == Scenario_List ? (editable ? "Click to open menu" : "Click to view enabled scenarios")
							: "Current Value: §o" + o.toString()));
			m.addButton(b, in);
			if (editable) {
				m.addSubMenu(o.getMenu(m), b, !(o == Scenario_List));
			} else if (o == Scenario_List) {
				m.addSubMenu(ScenarioListCommand.getMenu(), b, true);
			}

			in++;
		}

		if (category == Category.Scenarios) {
			if (Scenarios.getEnabled().size() > 0) {

				int id = 0;
				for (Scenarios s : Scenarios.getEnabled()) {
					if (getOptionsFor(s).size() < 1) {
						continue;
					}
					id++;

					Button b = new Button(false, Category.Scenarios.ico, 1, Category.Scenarios.icodata, "Scenario: "
							+ s.name().replace('_', ' '), "Click to open menu");
					m.addButton(b, id);
					m.addSubMenu(getMenuFor(s, editable), b, true);
				}
			}
		}

		Button back = new Button(false, Material.STICK, 1, 0, "Back", "Click to go back");
		m.addButton(back, m.i.getSize() - 1);
		back.setOnClick(new ButtonRunnable() {
			public void run(Player p, InventoryClickEvent event) {
				p.closeInventory();

				getOptionsMenu(editable).open(p);
			}
		});

		return m;
	}

	public static List<Option> getOptionsFor(Enum<?> category) {
		List<Option> ret = new ArrayList<>();

		for (Option o : values()) {
			if (category.getClass() == Scenarios.class && o.scen != category || category.getClass() == Category.class
					&& o.cat != category) {
				continue;
			}

			ret.add(o);
		}

		return ret;
	}

	public static String serialize() {
		String ret = "";

		for (Option o : values()) {
			if (o == Scenario_List) {
				String add = "";
				for (int i = 0; i < Scenarios.values().length; i++) {
					Scenarios sc = Scenarios.values()[i];
					if (sc.isEnabled()) {
						add += i + ":";
					}
				}
				ret += add.substring(0, add.length() > 0 ? add.length() - 1 : 0);
			} else if (o.isInt()) {
				ret += o.val;
			} else if (o.isBoolean()) {
				ret += o.getBoolean() ? "1" : "0";
			} else {
				for (int i = 0; i < o.vals.length; i++) {
					String str = o.vals[i];
					if (str.equalsIgnoreCase(o.val)) {
						ret += i;
						break;
					}
				}
			}

			ret += ";";
		}

		return ret.substring(0, ret.length() - 1);
	}

	public static void loadFromString(String load) {
		String[] str = load.split(";");

		for (int i = 0; i < str.length; i++) {
			Option set = values()[i];
			String setTo = str[i];

			if (set == Scenario_List) {
				Scenarios.enabled.clear();
				String[] scens = setTo.split(":");
				for (String s : scens) {
					if (isInteger(s)) {
						Scenarios en = Scenarios.values()[Integer.parseInt(s)];
						en.setEnabled(true);
					}
				}
			} else {
				if (set.isInt()) {
					set.set(setTo);
				} else if (set.isBoolean()) {
					set.set(setTo.equalsIgnoreCase("1"));
				} else {
					set.set(set.vals[Integer.parseInt(setTo)]);
				}
			}
		}
	}

	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (Exception e) {
		}
		return false;
	}
}
