package net.mcshockwave.UHC;

import net.mcshockwave.UHC.NumberedTeamSystem.NumberTeam;
import net.mcshockwave.UHC.Commands.BanningCommands;
import net.mcshockwave.UHC.Commands.CommandOption;
import net.mcshockwave.UHC.Commands.CommandTeam;
import net.mcshockwave.UHC.Commands.CommandUHC;
import net.mcshockwave.UHC.Commands.HelpOpCommand;
import net.mcshockwave.UHC.Commands.MoleChatCommand;
import net.mcshockwave.UHC.Commands.RestrictCommand;
import net.mcshockwave.UHC.Commands.ScenarioListCommand;
import net.mcshockwave.UHC.Commands.SilenceCommand;
import net.mcshockwave.UHC.Commands.VoteCommand;
import net.mcshockwave.UHC.Commands.WLCommand;
import net.mcshockwave.UHC.Listeners.DTMListener;
import net.mcshockwave.UHC.Listeners.HungerGamesHandler;
import net.mcshockwave.UHC.Listeners.MoleListener;
import net.mcshockwave.UHC.Menu.ItemMenuListener;
import net.mcshockwave.UHC.Utils.BarUtil;
import net.mcshockwave.UHC.Utils.CustomSignUtils.CustomSignListener;
import net.mcshockwave.UHC.Utils.FakePlayer;
import net.mcshockwave.UHC.Utils.ItemMetaUtils;
import net.mcshockwave.UHC.Utils.SchedulerUtils;
import net.mcshockwave.UHC.worlds.Multiworld;

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
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
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
	public static Objective					health			= null, healthTab = null, kills = null;

	public static HashMap<String, Integer>	totKills		= new HashMap<>();
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
		getCommand("helpop").setExecutor(new HelpOpCommand());

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
		pm.addPacketListener(new PacketAdapter(ins, PacketType.Play.Client.USE_ENTITY) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
				PacketContainer con = event.getPacket();
				Player p = event.getPlayer();

				short id = con.getIntegers().read(0).shortValue();

				if (FakePlayer.fakePlayers.containsKey(id)) {
					final FakePlayer fp = FakePlayer.fakePlayers.get(id);

					if (fp.getInventory() != null) {
						p.openInventory(fp.getInventory());
					}
				}
			}
		});

		rerandomize();

		Multiworld.getUHC().setSpawnLocation(0, Multiworld.getUHC().getHighestBlockYAt(0, 0), 0);

		FakePlayer.init();
	}

	public static void registerKillScoreboard() {
		if (scb.getObjective("Kills") != null) {
			scb.getObjective("Kills").unregister();
		}

		kills = scb.registerNewObjective("Kills", "playerKillCount");
		if (!Scenarios.Hunger_Games.isEnabled() || !Option.HG_Game_Handling.getBoolean()) {
			kills.setDisplaySlot(DisplaySlot.SIDEBAR);
		}
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

		healthTab = scb.registerNewObjective("HealthList", "dummy");
		healthTab.setDisplaySlot(DisplaySlot.PLAYER_LIST);

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
					if (!Scenarios.Hunger_Games.isEnabled()) {
						healthTab.getScore(op).setScore(getRoundedHealth(p.getHealth()));
					}
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

	public static void startSpread(long time) {
		boolean resuming = time > 0;

		boolean hg = Scenarios.Hunger_Games.isEnabled();
		boolean hgh = Option.HG_Game_Handling.getBoolean();

		if (started)
			return;

		if (!resuming) {
			if (hg && hgh) {
				Bukkit.broadcastMessage("§eGenerating center...");
				HungerGamesHandler.createCenter();
			}
			if (hg && hgh) {
				HungerGamesHandler.spreadAll(30, Multiworld.getUHC().getHighestBlockYAt(0, 0) + 1);
				HungerGamesHandler.preparePlayers();
				start(time, resuming);
			} else {
				spreadPlayers(Option.Spread_Radius.getInt(), true, false, time, resuming);
			}
		} else {
			start(time, resuming);
		}

	}

	public static void start(long time, boolean resuming) {
		started = true;

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
			@SuppressWarnings("deprecation")
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

				if (isM && (c - 10) % 20 == 0 && Scenarios.Hunger_Games.isEnabled()) {
					HungerGamesHandler.displayDeaths();
				}

				if (isM && c == Option.PVP_Time.getInt() && count.getTime() > 30 && !Scenarios.Hunger_Games.isEnabled()) {
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
						Bukkit.broadcastMessage("§a§lMeet up time! Everyone stop what you are doing and head to the center of the map! (x: 0, z: 0)");

						int y = Multiworld.getUHC().getHighestBlockYAt(0, 0);
						Block b = Multiworld.getUHC().getBlockAt(0, y, 0);
						if (b.getType() == Material.CHEST) {
							b = b.getRelative(0, -1, 0);
						}
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
						spreadPlayers(100, true, true);
					} else if (end.equalsIgnoreCase("Compasses")) {
						Bukkit.broadcastMessage("§a§lCompass Time! Everyone has received a compass. Right-click the compass to point to close players.");

						for (Player p : Bukkit.getOnlinePlayers()) {
							p.getInventory().addItem(
									ItemMetaUtils.setLore(new ItemStack(Material.COMPASS), "§eClick to point",
											"§eto closest player"));
						}
					} else if (end.equalsIgnoreCase("Feast")) {
						HungerGamesHandler.onFeast();
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
		return "§e" + Option.Display_Name.getString() + " §6" + count.getTimeString() + "§0 - §a" + getTimeUntil();
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
		if (kills != null) {
			kills.unregister();
		}

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
		scb.resetScores(p.getName());
		// for (NumberTeam nt : nts.teams) {
		// nt.sc.resetScores(p);
		// }
		// playersLeft.setScore(getAlive().size());
	}

	public static Random	rand		= new Random();

	public static int		maxPlayers	= 30;

	public static int getScatterAmount() {
		int ret = 0;
		if (nts.isTeamGame()) {
			ret += nts.teams.size();
		}
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (UltraHC.specs.contains(p.getName())) {
				continue;
			}
			if (nts.isTeamGame()) {
				if (nts.getTeam(p.getName()) != null) {
					continue;
				}
				ret++;
			} else {
				ret++;
			}
		}

		return ret;
	}

	public static void spreadPlayers(int spreadDistance, final boolean delay, final boolean instant) {
		spreadPlayers(spreadDistance, delay, instant, -2, false);
	}

	public static void spreadPlayers(int spreadDistance, final boolean delay, final boolean instant, final long time,
			final boolean resuming) {
		SchedulerUtils util = SchedulerUtils.getNew();
		Bukkit.broadcastMessage("§cGetting scatter locations...");
		final Location[] locs = ScatterManager.getScatterLocations(Multiworld.getUHC(), spreadDistance,
				getScatterAmount());
		util.add(delay ? 10 : 0);
		util.add("§aLoading chunks... (this may take a while)");
		for (final Location l : locs) {
			util.add(new Runnable() {
				public void run() {
					l.getChunk().load(true);
				}
			});
			util.add(delay ? 10 : 0);
		}
		util.add(delay ? 9 : 0);
		util.add("§eLoading locations...");
		if (nts.isTeamGame()) {
			util.add(new Runnable() {
				public void run() {
					int index = 0;
					for (NumberTeam nt : nts.teams) {
						if (index >= locs.length) {
							continue;
						}

						final Location l = locs[index];
						for (String s : nt.getPlayersArray()) {
							ScatterManager.scatterLocs.put(s, l);
						}
						index++;
					}
					for (Player p : Bukkit.getOnlinePlayers()) {
						if (nts.getTeam(p.getName()) == null) {
							if (index >= locs.length) {
								continue;
							}

							if (UltraHC.specs.contains(p.getName())) {
								continue;
							}

							ScatterManager.scatterLocs.put(p.getName(), locs[index]);
							index++;
						}
					}
				}
			});
		} else {
			util.add(new Runnable() {
				public void run() {
					int index = 0;
					for (Player p : Bukkit.getOnlinePlayers()) {
						if (index >= locs.length) {
							continue;
						}

						if (UltraHC.specs.contains(p.getName())) {
							continue;
						}

						ScatterManager.scatterLocs.put(p.getName(), locs[index]);
						index++;
					}
				}
			});
		}
		util.add(delay ? 10 : 0);
		util.add("§dStarting spread!");
		util.add(new Runnable() {
			public void run() {
				SchedulerUtils util = SchedulerUtils.getNew();

				for (final Entry<String, Location> ent : ScatterManager.scatterLocs.entrySet()) {
					util.add(new Runnable() {
						public void run() {
							if (Bukkit.getPlayer(ent.getKey()) != null) {
								Player p = Bukkit.getPlayer(ent.getKey());
								p.teleport(ent.getValue());
								Bukkit.broadcastMessage("§aScattering: §6"
										+ p.getName()
										+ " §8[§7"
										+ (nts.getTeam(p.getName()) != null ? "Team " + nts.getTeam(p.getName()).id
												: "Solo") + "§8]");
								for (Player pl : Bukkit.getOnlinePlayers()) {
									pl.playSound(pl.getLocation(), Sound.NOTE_PLING, 10, 2);
								}
							}
						}
					});
					util.add(instant ? 0 : 2);
				}

				util.add("§e§lDone scattering!");
				if (time != -2) {
					util.add(delay ? 10 : 0);
					util.add(new Runnable() {
						public void run() {
							start(time, resuming);
						}
					});
				}

				util.execute();
			}
		});

		util.execute();
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
		ArrayList<Player> ps = new ArrayList<>(Bukkit.getOnlinePlayers());

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
