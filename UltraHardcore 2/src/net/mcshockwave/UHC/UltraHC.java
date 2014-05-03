package net.mcshockwave.UHC;

import net.mcshockwave.UHC.Commands.CommandOption;
import net.mcshockwave.UHC.Commands.CommandTeam;
import net.mcshockwave.UHC.Commands.CommandUHC;
import net.mcshockwave.UHC.Commands.MoleChatCommand;
import net.mcshockwave.UHC.Commands.RestrictCommand;
import net.mcshockwave.UHC.Commands.SilenceCommand;
import net.mcshockwave.UHC.Commands.VoteCommand;
import net.mcshockwave.UHC.Listeners.MoleListener;
import net.mcshockwave.UHC.Menu.ItemMenuListener;
import net.mcshockwave.UHC.Utils.CustomSignUtils.CustomSignListener;
import net.mcshockwave.UHC.Utils.ItemMetaUtils;
import net.mcshockwave.UHC.worlds.Multiworld;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.Hologram;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.schematic.SchematicFormat;

public class UltraHC extends JavaPlugin {

	public static UltraHC			ins;

	public static boolean			started			= false;

	public static Counter			count			= null;

	public static Scoreboard		score			= null;
	public static Objective			health			= null, healthList = null, stats = null;
	public static Score				playersLeft		= null, mutime = null;
	// borderSize = null;

	public static ArrayList<String>	specs			= new ArrayList<>();
	public static ArrayList<String>	players			= new ArrayList<>();

	public static TeamSystem		ts;

	public static ItemStack[]		startCon		= null, startACon;

	public static String			cruxSchemName	= "cruxSpawn";

	public static boolean			chatSilenced	= false;

	public void onEnable() {
		ins = this;

		Multiworld.loadAll();

		Bukkit.getPluginManager().registerEvents(new DefaultListener(), this);
		Bukkit.getPluginManager().registerEvents(new ItemMenuListener(), this);
		Bukkit.getPluginManager().registerEvents(new CustomSignListener(), this);

		getCommand("uhc").setExecutor(new CommandUHC());
		getCommand("team").setExecutor(new CommandTeam());
		getCommand("vote").setExecutor(new VoteCommand());
		getCommand("silence").setExecutor(new SilenceCommand());
		getCommand("restr").setExecutor(new RestrictCommand());
		getCommand("options").setExecutor(new CommandOption());
		getCommand("mole").setExecutor(new MoleListener());
		getCommand("mc").setExecutor(new MoleChatCommand());

		score = Bukkit.getScoreboardManager().getMainScoreboard();

		registerHealthScoreboard();

		ts = new TeamSystem(score);

		for (Team t : score.getTeams()) {
			ts.teams.put(ChatColor.getByChar(t.getPrefix().charAt(1)), t);
		}

		Bukkit.addRecipe(new ShapedRecipe(ItemMetaUtils.setItemName(new ItemStack(Material.GOLDEN_APPLE),
				ChatColor.GOLD + "Golden Head")).shape("III", "IHI", "III").setIngredient('I', Material.GOLD_INGOT)
				.setIngredient('H', new ItemStack(Material.SKULL_ITEM, 1, (short) 3).getData()));

		saveDefaultConfig();

		SQLTable.enable();

		if (Bukkit.getPluginManager().getPlugin("ProtocolLib") != null) {
			ProtocolManager pm = ProtocolLibrary.getProtocolManager();
			PacketAdapter pa = null;
			if (pa == null) {
				pa = new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Server.LOGIN) {
					@Override
					public void onPacketSending(PacketEvent event) {
						if (event.getPacketType() == PacketType.Play.Server.LOGIN) {
							event.getPacket().getBooleans().write(0, true);
						}
					}
				};
			}
			pm.addPacketListener(pa);
		}
	}

	public static void registerHealthScoreboard() {
		if (score.getObjective("Health") != null) {
			score.getObjective("Health").unregister();
		}
		if (score.getObjective("HealthList") != null) {
			score.getObjective("HealthList").unregister();
		}

		health = score.registerNewObjective("Health", "dummy");
		health.setDisplayName(" / 100");
		health.setDisplaySlot(DisplaySlot.BELOW_NAME);
		for (Player p : Bukkit.getOnlinePlayers()) {
			health.getScore(p).setScore(getRoundedHealth(p.getHealth()));
		}

		healthList = score.registerNewObjective("HealthList", "dummy");
		healthList.setDisplaySlot(DisplaySlot.PLAYER_LIST);
		for (Player p : Bukkit.getOnlinePlayers()) {
			healthList.getScore(p).setScore(getRoundedHealth(p.getHealth()));
		}
	}

	public static void updateHealthFor(final Player p) {
		Bukkit.getScheduler().runTaskLater(ins, new Runnable() {
			public void run() {
				OfflinePlayer op = p;
				if (p.getName().length() > DefaultListener.maxLength) {
					op = Bukkit.getOfflinePlayer(p.getPlayerListName());
				}

				try {
					healthList.getScore(op).setScore(getRoundedHealth(p.getHealth()));
					health.getScore(p).setScore(getRoundedHealth(p.getHealth()));
				} catch (Exception e) {
					registerHealthScoreboard();
				}
			}
		}, 1);
	}

	public static void updateHealth() {
		for (Player p : getAlive()) {
			updateHealthFor(p);
		}
	}

	public void onDisable() {
		stop();
	}

	public static void start() {
		if (started)
			return;
		started = true;
		Bukkit.broadcastMessage("§c§lGame started!");

		spreadPlayers(Option.Spread_Radius.getInt());

		for (Player p : Bukkit.getOnlinePlayers()) {
			Chunk c = p.getLocation().getChunk();

			if (!c.isLoaded()) {
				c.load();
			}

			if (p.getGameMode() != GameMode.SURVIVAL) {
				p.setGameMode(GameMode.SURVIVAL);
			}

			if (!Option.Hunger.getBoolean()) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 10));
			}

			p.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 620, 10));
			p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 620, 10));

			if (startCon != null) {
				UltraHC.setInventory(p, startCon, startACon);
			} else {
				p.getInventory().clear();
				p.getInventory().setArmorContents(null);
			}

			players.add(p.getName());
		}

		stats = score.registerNewObjective("UHCStats", "dummy");
		stats.setDisplayName("§c" + Option.getScenario().name().replace('_', ' ') + " §60:00:00");
		stats.setDisplaySlot(DisplaySlot.SIDEBAR);

		playersLeft = stats.getScore(Bukkit.getOfflinePlayer("§aPlayers Left:"));
		playersLeft.setScore(Bukkit.getOnlinePlayers().length);

		mutime = stats.getScore(Bukkit.getOfflinePlayer("§bTime Until MU:"));
		mutime.setScore(Option.Meet_Up_Time.getInt() * 60);

		// borderSize =
		// stats.getScore(Bukkit.getOfflinePlayer("§bBorder Size:"));
		// borderSize.setScore(Option.Border_Radius.getInt());

		count = new Counter();
		count.setRunnable(new Runnable() {
			public void run() {
				stats.setDisplayName("§c" + Option.getScenario().name().replace('_', ' ') + " §6"
						+ count.getTimeString());

				if (Option.Eternal_Daylight.getBoolean()) {
					Multiworld.getUHC().setTime(5000);
				} else {
					Multiworld.getUHC().setTime(count.runCount * 20);
				}

				long c = count.runCountMin + 1;
				boolean isM = count.runCount % 60 == 0;
				if (isM && c % Option.Mark_Time.getInt() == 0 && c != 0) {
					Bukkit.broadcastMessage("§c§lMARK " + c + " MINS IN!");
				}

				if (isM && c == Option.No_Kill_Time.getInt()) {
					Bukkit.broadcastMessage("§aKilling is now allowed!");

					if (Option.Scenario.getString().equalsIgnoreCase("Mole")) {
						for (Team t : ts.teams.values()) {
							OfflinePlayer[] ps = t.getPlayers().toArray(new OfflinePlayer[0]);
							if (ps.length == 0) {
								continue;
							}
							OfflinePlayer mole = ps[rand.nextInt(ps.length)];

							MoleListener.setAsMole(mole);
						}
					}
				}

				if (c < Option.Meet_Up_Time.getInt()) {
					mutime.setScore((int) ((Option.Meet_Up_Time.getInt() * 60) - count.runCount));
				}

				if (isM && c == Option.Meet_Up_Time.getInt()) {
					Bukkit.broadcastMessage("§a§lMeet up time! Everyone stop what you are doing and head to the center of the map! (x: 0, z:0)");
					mutime.setScore(0);
				}

				// if (isM && c == Option.Border_Time.getInt()) {
				// Bukkit.broadcastMessage("§aThe border is now shrinking!");
				// }
				//
				// if (count.getTotalMins() >= Option.Border_Time.getInt()) {
				// if (count.runCount % Option.Border_Rate.getInt() == 0) {
				// if (borderSize.getScore() > 75) {
				// borderSize.setScore(borderSize.getScore() - 1);
				// }
				// }
				// }
			}
		});
		count.start();

		Multiworld.getUHC().setGameRuleValue("doDaylightCycle", !Option.Eternal_Daylight.getBoolean() + "");
		Multiworld.getUHC().setTime(0);

		if (Option.getScenario().l != null) {
			Bukkit.getPluginManager().registerEvents(Option.getScenario().l, ins);
		}
		Option.getScenario().onStart();

		ts.setScores();
	}

	public static int getRoundedHealth(double h) {
		h = Math.round(h * 10);
		h /= 2;

		return (int) h;
	}

	public static void stop() {
		if (!started)
			return;
		started = false;
		Bukkit.broadcastMessage("§a§lGame has been stopped!");
		count.stop();

		if (Option.getScenario().l != null) {
			HandlerList.unregisterAll(Option.getScenario().l);
		}
		Option.getScenario().onStop();
		for (Hologram h : HoloAPI.getManager().getAllHolograms().keySet()) {
			h.clearAllPlayerViews();
			HoloAPI.getManager().stopTracking(h);
			HoloAPI.getManager().clearFromFile(h);
		}
		
		players.clear();
		specs.clear();

		try {
			health.unregister();
			healthList.unregister();
			stats.unregister();
		} catch (Exception e) {
		}
		playersLeft = null;
		ts.scores.clear();
	}

	public static ArrayList<Player> getAlive() {
		ArrayList<Player> ret = new ArrayList<>();
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (!specs.contains(p.getName())) {
				ret.add(p);
			}
		}
		return ret;
	}

	public static void onDeath(final Player p) {
		specs.add(p.getName());
		p.sendMessage("§c You died! You have 30 seconds before you get kicked!");
		Bukkit.getScheduler().runTaskLater(ins, new Runnable() {
			public void run() {
				p.kickPlayer("§cYou are out!");
			}
		}, 600l);

		playersLeft.setScore(getAlive().size());

		if (UltraHC.score.getPlayerTeam(p) != null) {
			UltraHC.score.getPlayerTeam(p).removePlayer(p);
		}

		ts.setScores();
	}

	public static Random	rand		= new Random();

	public static int		maxPlayers	= 30;

	public static void spreadPlayers(int spreadDistance) {
		Material[] nospawn = { Material.STATIONARY_WATER, Material.WATER, Material.STATIONARY_LAVA, Material.LAVA,
				Material.CACTUS };
		if (spreadDistance == -1) {
			spreadDistance = 1000;
		}
		ArrayList<Player> spread = new ArrayList<Player>();
		for (Player p : Bukkit.getOnlinePlayers()) {
			boolean goodSpawn = false;
			for (Player p2 : spread) {
				Team t = score.getPlayerTeam(p);
				Team t2 = score.getPlayerTeam(p2);
				if (t != null && t2 != null && t == t2) {
					p.teleport(p2);
					goodSpawn = true;
				}
			}
			while (!goodSpawn) {
				int x = rand.nextInt(spreadDistance) - rand.nextInt(spreadDistance);
				int z = rand.nextInt(spreadDistance) - rand.nextInt(spreadDistance);
				int y = Multiworld.getUHC().getHighestBlockYAt(x, z);
				Location l = new Location(Multiworld.getUHC(), x, y, z);
				Material m = l.add(0, -1, 0).getBlock().getType();
				boolean noHazard = true;
				for (Material no : nospawn) {
					if (m == no) {
						noHazard = false;
					}
				}
				if (noHazard) {
					goodSpawn = true;
					l.getChunk().load();
					p.teleport(l.add(0, 2, 0));
					spread.add(p);
				}
			}
		}
		spread.clear();
	}

	public static void setInventory(Player p, ItemStack[] con, ItemStack[] acon) {
		PlayerInventory inv = p.getInventory();

		for (int i = 0; i < 36; i++) {
			inv.setItem(i, con[i]);
		}

		inv.setArmorContents(acon);
	}

	public static void loadSchematic(String name, Location l) {
		File f = new File(ins.getDataFolder(), name + ".schematic");

		if (!f.exists()) {
			Bukkit.broadcastMessage("§cSchematic not found: " + name + ".schematic");
			return;
		}

		SchematicFormat schematic = SchematicFormat.getFormat(f);

		EditSession session = new EditSession(new BukkitWorld(l.getWorld()), 1000);
		try {
			CuboidClipboard clipboard = schematic.load(f);
			clipboard.paste(session, BukkitUtil.toVector(l), false);
			session.flushQueue();
		} catch (Exception e) {
			Bukkit.broadcastMessage("§cError while loading schem: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static boolean isMCShockwaveEnabled() {
		return Bukkit.getPluginManager().isPluginEnabled("MCShockwave");
	}

	public static void addPlayer(String name) {
		while (UltraHC.specs.contains(name)) {
			UltraHC.specs.remove(name);
		}
		while (UltraHC.players.contains(name)) {
			UltraHC.players.remove(name);
		}
		UltraHC.players.add(name);
	}

}
