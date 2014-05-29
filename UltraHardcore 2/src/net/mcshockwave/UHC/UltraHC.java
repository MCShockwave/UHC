package net.mcshockwave.UHC;

import net.mcshockwave.UHC.NumberedTeamSystem.NumberTeam;
import net.mcshockwave.UHC.Commands.BanningCommands;
import net.mcshockwave.UHC.Commands.CommandOption;
import net.mcshockwave.UHC.Commands.CommandTeam;
import net.mcshockwave.UHC.Commands.CommandUHC;
import net.mcshockwave.UHC.Commands.MoleChatCommand;
import net.mcshockwave.UHC.Commands.RestrictCommand;
import net.mcshockwave.UHC.Commands.ScenarioListCommand;
import net.mcshockwave.UHC.Commands.SilenceCommand;
import net.mcshockwave.UHC.Commands.VoteCommand;
import net.mcshockwave.UHC.Commands.WLCommand;
import net.mcshockwave.UHC.Listeners.DTMListener;
import net.mcshockwave.UHC.Listeners.MoleListener;
import net.mcshockwave.UHC.Menu.ItemMenuListener;
import net.mcshockwave.UHC.Utils.BarUtil;
import net.mcshockwave.UHC.Utils.CustomSignUtils.CustomSignListener;
import net.mcshockwave.UHC.Utils.ItemMetaUtils;
import net.mcshockwave.UHC.worlds.Multiworld;
import net.mcshockwave.scatter.ScatterManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
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
import org.bukkit.scoreboard.Scoreboard;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.schematic.SchematicFormat;

public class UltraHC extends JavaPlugin {

	public static UltraHC					ins;

	public static boolean					started			= false;

	public static Counter					count			= null;

	public static Scoreboard				scb				= null;
	public static Objective					health			= null, healthList = null, kills = null;
	// borderSize = null;

	public static ArrayList<String>			specs			= new ArrayList<>();
	public static ArrayList<String>			players			= new ArrayList<>();

	public static ItemStack[]				startCon		= null, startACon;

	public static String					cruxSchemName	= "cruxSpawn";

	public static boolean					chatSilenced	= false;

	public static HashMap<String, Location>	scatterLocs		= new HashMap<>();

	public static NumberedTeamSystem		nts;

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
		getCommand("gameban").setExecutor(new BanningCommands());
		getCommand("permban").setExecutor(new BanningCommands());
		getCommand("incrban").setExecutor(new BanningCommands());
		getCommand("uhcunban").setExecutor(new BanningCommands());
		getCommand("scenarios").setExecutor(new ScenarioListCommand());
		getCommand("wl").setExecutor(new WLCommand());
		getCommand("setoption").setExecutor(new CommandOption());

		scb = Bukkit.getScoreboardManager().getMainScoreboard();

		registerHealthScoreboard();

		BarUtil.enable();

		nts = new NumberedTeamSystem(scb);

		for (Player p : Bukkit.getOnlinePlayers()) {
			p.setScoreboard(scb);
		}

		Bukkit.addRecipe(new ShapedRecipe(ItemMetaUtils.setItemName(new ItemStack(Material.GOLDEN_APPLE),
				ChatColor.GOLD + "Golden Head")).shape("III", "IHI", "III").setIngredient('I', Material.GOLD_INGOT)
				.setIngredient('H', new ItemStack(Material.SKULL_ITEM, 1, (short) 3).getData()));

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

		rerandomize();

		Multiworld.getUHC().setSpawnLocation(0, Multiworld.getUHC().getHighestBlockYAt(0, 0), 0);
	}

	public static void registerKillScoreboard() {
		if (scb.getObjective("Kills") != null) {
			scb.getObjective("Kills").unregister();
		}

		kills = scb.registerNewObjective("Kills", "playerKillCount");
		kills.setDisplaySlot(DisplaySlot.SIDEBAR);
		kills.setDisplayName("§e>> §6KILLS §e<<");
	}

	public static void registerHealthScoreboard() {
		if (scb.getObjective("Health") != null) {
			scb.getObjective("Health").unregister();
		}
		if (scb.getObjective("HealthList") != null) {
			scb.getObjective("HealthList").unregister();
		}

		health = scb.registerNewObjective("Health", "dummy");
		health.setDisplayName("% HP");
		health.setDisplaySlot(DisplaySlot.BELOW_NAME);

		healthList = scb.registerNewObjective("HealthList", "dummy");
		healthList.setDisplaySlot(DisplaySlot.PLAYER_LIST);

		updateHealth();
	}

	public static void updateHealthFor(final Player p) {
		Bukkit.getScheduler().runTaskLater(ins, new Runnable() {
			@SuppressWarnings("deprecation")
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

	@SuppressWarnings("deprecation")
	public static void start(long time) {
		boolean resuming = time > 0;

		if (started)
			return;
		started = true;

		if (!resuming) {
			if (Bukkit.getPluginManager().isPluginEnabled("InstantScatter")) {
				ScatterManager.spreadPlayers(Multiworld.getUHC(), Option.Spread_Radius.getInt());
			} else {
				spreadPlayers(Option.Spread_Radius.getInt());
			}
		}

		for (Player p : Bukkit.getOnlinePlayers()) {
			Chunk c = p.getLocation().getChunk();

			if (!c.isLoaded()) {
				c.load();
			}

			if (p.getGameMode() != GameMode.SURVIVAL) {
				p.setGameMode(GameMode.SURVIVAL);
			}

			if (!resuming) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 620, 10));
				p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 620, 10));
			}

			if (startCon != null) {
				UltraHC.setInventory(p, startCon, startACon);
			} else if (!resuming) {
				p.getInventory().clear();
				p.getInventory().setArmorContents(null);
			}

			players.add(p.getName());
		}

		// stats = scb.registerNewObjective("UHCStats", "dummy");
		// stats.setDisplayName("§c" + Option.getScenario().name().replace('_',
		// ' ') + " §60:00:00");
		// stats.setDisplaySlot(DisplaySlot.SIDEBAR);
		//
		// playersLeft =
		// stats.getScore(Bukkit.getOfflinePlayer("§aPlayers Left:"));
		// playersLeft.setScore(Bukkit.getOnlinePlayers().length);
		//
		// mutime = stats.getScore(Bukkit.getOfflinePlayer("§bTime Until MU:"));
		// mutime.setScore(Option.Meet_Up_Time.getInt() * 60);

		// borderSize =
		// stats.getScore(Bukkit.getOfflinePlayer("§bBorder Size:"));
		// borderSize.setScore(Option.Border_Radius.getInt());

		count = new Counter();
		count.setRunnable(new Runnable() {
			public void run() {
				// stats.setDisplayName("§c" +
				// Option.getScenario().name().replace('_', ' ') + " §6"
				// + count.getTimeString());
				BarUtil.displayTextBar(getBarText(), getBarHealth());

				if (Option.Eternal_Daylight.getBoolean()) {
					Multiworld.getUHC().setTime(5000);
				} else {
					Multiworld.getUHC().setTime(count.getTime() * 20);
				}

				long c = count.runCountMin + 1;
				boolean isM = count.runCount % 60 == 0;
				if (isM && c % Option.Mark_Time.getInt() == 0 && c != 0) {
					Bukkit.broadcastMessage("§c§lMARK " + c + " MINS IN!");
				}

				if (isM && c == Option.PVP_Time.getInt() && count.getTime() > 30) {
					Bukkit.broadcastMessage("§a§lKilling is now allowed!");
					for (Player p : Bukkit.getOnlinePlayers()) {
						p.playSound(p.getLocation(), Sound.ENDERDRAGON_GROWL, 1, 0.75f);
					}

					if (Scenarios.DTM.isEnabled()) {
						DTMListener.onPVP();
					}

					if (Scenarios.Mole.isEnabled()) {
						for (NumberTeam nt : nts.teams) {
							ArrayList<String> ps = nt.players;
							if (ps.size() == 0) {
								continue;
							}
							String mole = ps.get(rand.nextInt(ps.size()));

							MoleListener.setAsMole(Bukkit.getOfflinePlayer(mole));

							for (Player p : nt.getOnlinePlayers()) {
								if (!p.getName().equalsIgnoreCase(mole)) {
									p.sendMessage("§cYou are not the mole!");
								}
							}
						}
					}
				}

				// if (c < Option.Meet_Up_Time.getInt()) {
				// mutime.setScore((int) ((Option.Meet_Up_Time.getInt() * 60) -
				// count.runCount));
				// }

				if (isM && c == Option.Game_Length.getInt()) {
					for (Player p : Bukkit.getOnlinePlayers()) {
						p.playSound(p.getLocation(), Sound.WITHER_SPAWN, 1, 0.75f);
					}

					String end = Option.End_Game.getString();
					if (end.equalsIgnoreCase("Meetup")) {
						Bukkit.broadcastMessage("§a§lMeet up time! Everyone stop what you are doing and head to the center of the map! (x: 0, z:0)");

						int y = Multiworld.getUHC().getHighestBlockYAt(0, 0);
						Block b = Multiworld.getUHC().getBlockAt(0, y, 0);
						if (b.getRelative(0, -1, 0).getType() == Material.BEACON) {
							y--;
						} else {
							b.setType(Material.BEACON);
						}

						int[] xs = { -1, 0, 1 };
						int[] zs = { -1, 0, 1 };

						for (int x : xs) {
							for (int z : zs) {
								Multiworld.getUHC().getBlockAt(x, y - 1, z).setType(Material.IRON_BLOCK);
							}
						}
					} else if (end.equalsIgnoreCase("Sudden Death")) {
						Bukkit.broadcastMessage("§a§lSudden Death! Everyone will be teleported to 0, 0 for a final battle.");

						CommandUHC.genWalls(Multiworld.getUHC(), 100);

						Bukkit.broadcastMessage("§bSpreading players...");
						spreadPlayers(100);
					} else if (end.equalsIgnoreCase("Compasses")) {
						Bukkit.broadcastMessage("§a§lCompass Time! Everyone has received a compass. Right-click the compass to point to close players.");

						for (Player p : Bukkit.getOnlinePlayers()) {
							p.getInventory().addItem(
									ItemMetaUtils.setLore(new ItemStack(Material.COMPASS), "§eClick to point",
											"§eto closest player"));
						}
					}
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
		if (resuming) {
			count.startTime = time;
		}

		Multiworld.getUHC().setTime(0);

		registerKillScoreboard();

		for (Scenarios s : Scenarios.getEnabled()) {
			if (s.l != null) {
				Bukkit.getPluginManager().registerEvents(s.l, ins);
			}
			s.onStart();
		}

		Bukkit.broadcastMessage("§c§lGame " + (resuming ? "§c§lresuming" : "§c§lstarted") + "!");
	}

	public static String getBarText() {
		return "§eMCShockwave UHC §6" + count.getTimeString() + "§0 - §a" + getTimeUntil();
	}

	public static String	colors	= "c6ea2b3d5";
	public static int		id		= -1;

	public static String getTimeUntil() {
		if (!isPVP()) {
			return "PVP in " + getReadableTime((Option.PVP_Time.getInt() * 60) - count.getTime());
		} else if (Option.Game_Length.getInt() > count.getTotalMins()) {
			return Option.End_Game.getString() + " in "
					+ getReadableTime((Option.Game_Length.getInt() * 60) - count.getTime());
		}
		id++;
		if (id >= colors.length()) {
			id = 0;
		}
		return "§" + colors.charAt(id) + "§l" + Option.End_Game.getString().toUpperCase();
	}

	public static String getReadableTime(long time) {
		return String.format("%d:%02d:%02d", time / 3600, time % 3600 / 60, time % 60);
	}

	public static float getBarHealth() {
		if (!isPVP()) {
			return getPercentDone(count.getTime(), Option.PVP_Time.getInt() * 60);
		} else if (Option.Game_Length.getInt() > count.getTotalMins()) {
			return getPercentDone(count.getTime(), Option.Game_Length.getInt() * 60);
		}
		return 100;
	}

	public static float getPercentDone(float cur, float fin) {
		return 100 * ((fin - cur) / fin);
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

		for (Scenarios s : Scenarios.getEnabled()) {
			if (s.l != null) {
				HandlerList.unregisterAll(s.l);
			}
			s.onStop();
		}
		// for (Hologram h : HoloAPI.getManager().getAllHolograms().keySet()) {
		// h.clearAllPlayerViews();
		// HoloAPI.getManager().stopTracking(h);
		// HoloAPI.getManager().clearFromFile(h);
		// }

		players.clear();
		specs.clear();
		kills.unregister();

		BarUtil.destroyTimer();

		// playersLeft = null;
		// stats.unregister();
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

	public static int	deathKickSeconds	= 30;

	public static void onDeath(final Player p) {
		specs.add(p.getName());
		p.sendMessage("§c§lYou died! You have " + deathKickSeconds + " seconds before you get kicked!");
		Bukkit.getScheduler().runTaskLater(ins, new Runnable() {
			public void run() {
				p.kickPlayer("§c§lThanks for playing MCShockwave UHC!");
			}
		}, deathKickSeconds * 20);
		scb.resetScores(p);
		// playersLeft.setScore(getAlive().size());
	}

	public static Random	rand		= new Random();

	public static int		maxPlayers	= 30;

	public static void spreadPlayers(int spreadDistance) {
		scatterLocs.clear();

		Material[] nospawn = { Material.STATIONARY_WATER, Material.WATER, Material.STATIONARY_LAVA, Material.LAVA,
				Material.CACTUS };
		if (spreadDistance <= -1) {
			spreadDistance = 1000;
		}
		ArrayList<Player> spread = new ArrayList<Player>();
		for (Player p : Bukkit.getOnlinePlayers()) {
			boolean goodSpawn = false;
			for (Player p2 : spread) {
				NumberTeam t = nts.getTeam(p.getName());
				NumberTeam t2 = nts.getTeam(p2.getName());
				if (t != null && t2 != null && t == t2) {
					p.teleport(p2);
					goodSpawn = true;
				}
			}
			int tries = 0;
			while (!goodSpawn) {
				tries++;

				int x = rand.nextInt(spreadDistance) - rand.nextInt(spreadDistance);
				int z = rand.nextInt(spreadDistance) - rand.nextInt(spreadDistance);
				int y = Multiworld.getUHC().getHighestBlockYAt(x, z);
				Location l = new Location(Multiworld.getUHC(), x, y, z);
				Material m = l.add(0, -1, 0).getBlock().getType();
				boolean noHazard = true;
				int minRadPlayers = (spreadDistance / (UltraHC.nts.isTeamGame() ? UltraHC.nts.teams.size() : Bukkit
						.getOnlinePlayers().length)) - tries;
				for (Entity e : p.getNearbyEntities(minRadPlayers, 256, minRadPlayers)) {
					if (e instanceof Player) {
						Player n = (Player) e;
						if (nts.getTeam(p.getName()) != null && nts.getTeam(n.getName()) != null
								&& nts.getTeam(n.getName()) != nts.getTeam(p.getName())) {
							noHazard = false;
						}
					}
				}
				if (l.getBlockY() < 48) {
					noHazard = false;
				}
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

					if (nts.getTeam(p.getName()) != null) {
						scatterLocs.put(nts.getTeam(p.getName()).id + "", l);
					} else {
						scatterLocs.put(p.getName(), l);
					}
				}
			}
		}
		spread.clear();
	}

	public static Location getScatterLocation(Player p) {
		String s = p.getName();

		if (scatterLocs.containsKey(s)) {
			return scatterLocs.get(s);
		}
		NumberTeam nt = nts.getTeam(s);
		if (nt != null && scatterLocs.containsKey(nt.id + "")) {
			return scatterLocs.get(nt.id + "");
		}
		return Multiworld.getUHC().getSpawnLocation();
	}

	public static void setInventory(Player p, ItemStack[] con, ItemStack[] acon) {
		PlayerInventory inv = p.getInventory();

		for (int i = 0; i < 36; i++) {
			inv.setItem(i, con[i]);
		}

		inv.setArmorContents(acon);
	}

	public static void deleteWorld(String w) {
		if (Bukkit.unloadWorld(w, false)) {
			System.out.println("Unloaded world");
		} else {
			System.err.println("Couldn't unload world");
		}
		if (delete(new File(w))) {
			System.out.println("Deleted world!");
		} else {
			System.err.println("Couldn't delete world");
		}
	}

	public static void deleteWorld(World w) {
		deleteWorld(w.getName());
	}

	public static boolean delete(File file) {
		if (file.isDirectory())
			for (File subfile : file.listFiles())
				if (!delete(subfile))
					return false;
		if (!file.delete())
			return false;
		return true;
	}

	public static void resetPlayer(Player p) {
		p.setMaxHealth(20);
		p.setHealth(p.getMaxHealth());
		p.setFoodLevel(20);
		p.setSaturation(10f);
		p.setLevel(0);
		p.setExp(0);
		UltraHC.updateHealthFor(p);

		for (PotionEffect pe : p.getActivePotionEffects()) {
			p.removePotionEffect(pe.getType());
		}

		p.getInventory().clear();
		p.getInventory().setArmorContents(new ItemStack[4]);
	}

	public static ItemStack getHOF() {
		return ItemMetaUtils.setLore(ItemMetaUtils.setItemName(new ItemStack(Material.BOOK), "§e§lHall of Fame"),
				"§bClick to view");
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

	public static boolean canBeCounted(Player p) {
		if (p.getGameMode() == GameMode.CREATIVE || specs.contains(p.getName())) {
			return false;
		}
		return true;
	}

	public static ArrayList<String>	random	= new ArrayList<>();

	public static void rerandomize() {
		ArrayList<String> ret = new ArrayList<>();
		ArrayList<Player> ps = new ArrayList<>(Arrays.asList(Bukkit.getOnlinePlayers()));

		for (int i = 0; i < ps.size(); i++) {
			Player r = ps.get(rand.nextInt(ps.size()));
			ps.remove(r);

			ret.add(r.getName());
		}

		random = ret;
	}

	public static Player getLowestHealth() {
		Player ret = null;
		for (String s : random) {
			if (Bukkit.getPlayer(s) == null) {
				continue;
			}
			Player p = Bukkit.getPlayer(s);
			if (!canBeCounted(p)) {
				continue;
			}
			if (ret == null) {
				ret = p;
				continue;
			}
			if (p.getHealth() < ret.getHealth()) {
				ret = p;
			}
		}
		return ret;
	}

	public static Player getMaximumHealth() {
		Player ret = null;
		for (String s : random) {
			if (Bukkit.getPlayer(s) == null) {
				continue;
			}
			Player p = Bukkit.getPlayer(s);
			if (!canBeCounted(p)) {
				continue;
			}
			if (ret == null) {
				ret = p;
				continue;
			}
			if (p.getHealth() > ret.getHealth()) {
				ret = p;
			}
		}
		return ret;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("max")) {
			Player max = getMaximumHealth();
			if (max == null) {
				sender.sendMessage("§cCould not find player with most health");
				return true;
			}
			sender.sendMessage("§cPerson with most health: " + max.getName());
		}
		if (label.equalsIgnoreCase("min")) {
			Player min = getLowestHealth();
			if (min == null) {
				sender.sendMessage("§cCould not find player with lowest health");
				return true;
			}
			sender.sendMessage("§cPerson with least health: " + min.getName());
		}
		return false;
	}

	public static boolean isPVP() {
		if (!started) {
			return false;
		}
		return Option.PVP_Time.getInt() <= UltraHC.count.getTotalMins();
	}

}
