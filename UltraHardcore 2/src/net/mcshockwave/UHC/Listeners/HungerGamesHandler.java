package net.mcshockwave.UHC.Listeners;

import net.mcshockwave.UHC.Option;
import net.mcshockwave.UHC.UltraHC;
import net.mcshockwave.UHC.Utils.SchedulerUtils;
import net.mcshockwave.UHC.worlds.Multiworld;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.Vector2D;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.regions.CylinderRegion;
import com.sk89q.worldedit.regions.Region;

public class HungerGamesHandler implements Listener {

	private static final int				DISTANCE_BEGIN	= 5;

	public static Random					rand			= new Random();

	public static ArrayList<FallingBlock>	chests			= new ArrayList<>();

	public static boolean					freeze			= false;

	public static boolean					grace			= false;

	public static int						high			= 64;

	public static String[]					welcome			= { "Welcome to the %num% weekly Hunger Games.",
			"I am your gamemaker.", "We have a total of %count% tributes competing today.",
			"Here's a reminder of how things work.", "1. Friendly Fire is on and encouraged.",
			"2. There will be a %grace% second grace period at the start.",
			"3. Death messages and the like have been removed, but will be shown at sundown.",
			"4. Regen will be %regen%.", "Now that I've finished explaining the rules, let's get going!",
			"And remember, may the odds be ever in your favor!" };

	public static void start() {
		UltraHC.healthTab.unregister();
		if (Option.HG_Game_Handling.getBoolean()) {
			freeze = true;
			final String num = getNum();
			Option.PVP_Time.set(1);
			SchedulerUtils util = SchedulerUtils.getNew();

			util.add(60);

			for (int i = 0; i < DISTANCE_BEGIN + 1; i++) {
				final int y = (high - DISTANCE_BEGIN) + i;
				util.add(new Runnable() {
					public void run() {
						for (Player p : UltraHC.getAlive()) {
							Location tp = p.getLocation();
							tp.setY(y);
							tp.getBlock().setType(Material.BEDROCK);
							p.teleport(tp.add(0, 1, 0));
							p.playSound(p.getLocation(), Sound.PISTON_EXTEND, 10, 0);
						}
					}
				});
				util.add(10);
			}
			util.add(60);

			for (int i = 0; i < welcome.length; i++) {
				final int id = i;
				util.add(new Runnable() {
					public void run() {
						String[] repl = {
								"%num%:" + num,
								"%count%:" + UltraHC.getAlive().size(),
								"%grace%:" + Option.HG_Grace_Period.getInt(),
								"%regen%:"
										+ (Option.HG_Regen_Off.getBoolean() ? "turned off at sundown" : "always "
												+ (Option.UHC_Mode.getBoolean() ? "off" : "on")) };

						String msg = welcome[id];
						for (String s : repl) {
							String[] ss = s.split(":");
							msg = msg.replace(ss[0], ss[1]);
						}
						Bukkit.broadcastMessage("§8[§6Gamemaker§8] §e" + msg);
					}
				});
				util.add(90);
			}

			util.add(20);
			util.add(new Runnable() {
				public void run() {
					dropChests();
					Bukkit.broadcastMessage("§b§n§lThe " + num + " Hunger Games will begin in:");
					Bukkit.broadcastMessage("§f");
					for (Player p : Bukkit.getOnlinePlayers()) {
						p.playSound(p.getLocation(), Sound.EXPLODE, 10, 2);
					}
				}
			});
			util.add(20);
			final int[] isec = { 5, 4, 3, 2, 1 };
			for (final int i : isec) {
				util.add(new Runnable() {
					public void run() {
						Bukkit.broadcastMessage("§e§l" + i);
						for (Player p : Bukkit.getOnlinePlayers()) {
							p.playSound(p.getLocation(), Sound.NOTE_PLING, 10, (float) 2 * (float) i
									/ (float) isec.length);
						}
					}
				});
				util.add(20);
			}
			util.add(new Runnable() {
				public void run() {
					freeze = false;
					grace = true;
					Bukkit.broadcastMessage("§a§lBEGIN!");
					UltraHC.count.startTime = System.currentTimeMillis();
					Option.PVP_Time.set(0);
					Option.UHC_Mode.set(false);
					Option.Friendly_Fire.set(true);
					Option.End_Game.set("Feast");
					for (Team t : UltraHC.scb.getTeams()) {
						t.setAllowFriendlyFire(true);
					}
					for (Player p : Bukkit.getOnlinePlayers()) {
						p.playSound(p.getLocation(), Sound.EXPLODE, 10, 0);
						p.playSound(p.getLocation(), Sound.WOLF_HOWL, 10, 0);
					}
					Multiworld.getUHC().setTime(0);
				}
			});
			util.add(Option.HG_Grace_Period.getInt() * 20);
			util.add(new Runnable() {
				public void run() {
					Bukkit.broadcastMessage("§d§lGRACE PERIOD OVER!");
					for (Player p : Bukkit.getOnlinePlayers()) {
						p.playSound(p.getLocation(), Sound.EXPLODE, 10, 2);
					}
					grace = false;
				}
			});

			try {
				util.execute();
			} catch (Exception e) {
				e.printStackTrace();
				Bukkit.broadcastMessage("§cERROR: " + e.getMessage());
			}
		}
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		Entity ee = event.getEntity();
		Entity de = event.getDamager();

		if (de instanceof Player) {
			Player d = (Player) de;

			if (ee instanceof Player && UltraHC.started && grace) {
				event.setCancelled(true);
				d.sendMessage("§cGrace period is still on!");
			}
		}
	}

	public static void preparePlayers() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			Location pl = p.getLocation();
			Location l = new Location(p.getWorld(), pl.getBlockX() + 0.5, high - DISTANCE_BEGIN, pl.getBlockZ() + 0.5);
			for (int i = 0; i < DISTANCE_BEGIN; i++) {
				l.clone().add(0, i, 0).getBlock().setType(Material.AIR);
			}
			p.teleport(l);
		}
	}

	public static void stop() {

	}

	public static HashMap<String, Inventory>	feast	= new HashMap<>();
	public static boolean						isFeast	= false;
	public static int							feastY	= 0;

	public static void onFeast() {
		Bukkit.broadcastMessage("§b§lThe feast has begun!");
		Bukkit.broadcastMessage("§eRegenerating center...");
		createCenter();

		for (int slot : new int[] { 3, 5 }) {
			for (String s : UltraHC.players) {
				Inventory inv = Bukkit.createInventory(null, 9);
				inv.setItem(slot, getRandomItem(true));
				feast.put(s, inv);
			}
		}

		Bukkit.getScheduler().runTaskLater(UltraHC.ins, new Runnable() {
			public void run() {
				isFeast = true;
				feastY = Multiworld.getUHC().getHighestBlockYAt(0, 0);
				Block ch = Multiworld.getUHC().getBlockAt(0, feastY, 0);
				ch.setType(Material.CHEST);
			}
		}, 10l);
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		Block b = event.getClickedBlock();

		if (freeze) {
			event.setCancelled(true);
		}

		if (b != null && b.getType() == Material.CHEST) {
			Location bl = b.getLocation();
			if (bl.getBlockX() == 0 && bl.getBlockY() == feastY && bl.getBlockZ() == 0) {
				if (isFeast && feast.containsKey(p.getName())) {
					p.openInventory(feast.get(p.getName()));
					event.setCancelled(true);
				}
			}
		}
	}

	public static ArrayList<String>	deaths	= new ArrayList<>();

	static int						count	= 0;

	public static void displayDeaths() {
		SchedulerUtils util = SchedulerUtils.getNew();

		bc("§b§lThe fallen players of the last day:", util);
		util.add(new Runnable() {
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers()) {
					p.playSound(p.getLocation(), Sound.FIREWORK_LAUNCH, 10, 0);
				}
			}
		});
		util.add(40);
		for (String s : deaths) {
			bc("§c[Death] §f" + s, util);
			util.add(new Runnable() {
				public void run() {
					for (Player p : Bukkit.getOnlinePlayers()) {
						p.playSound(p.getLocation(), Sound.FIREWORK_BLAST, 10, 0);
					}
				}
			});
			util.add(40);
		}
		util.add(new Runnable() {
			public void run() {
				if (count <= 0 && Option.HG_Regen_Off.getBoolean()) {
					Bukkit.broadcastMessage("§a§l - REGEN IS NOW OFF! -");
					Option.UHC_Mode.set(true);
					for (Player p : Bukkit.getOnlinePlayers()) {
						p.playSound(p.getLocation(), Sound.EXPLODE, 10, 1);
					}
				}
				count++;
				deaths.clear();

				UltraHC.kills.setDisplaySlot(DisplaySlot.SIDEBAR);
			}
		});
		util.add(300);
		util.add(new Runnable() {
			public void run() {
				UltraHC.kills.setDisplaySlot(null);
			}
		});

		util.execute();
	}

	public static void bc(final String s, SchedulerUtils util) {
		util.add(new Runnable() {
			public void run() {
				Bukkit.broadcastMessage(s);
			}
		});
	}

	public static String getNum() {
		String num = (rand.nextInt(900) + 100) + "";
		if (num.endsWith("1")) {
			num += "st";
		} else if (num.endsWith("2")) {
			num += "nd";
		} else if (num.endsWith("3")) {
			num += "rd";
		} else {
			num += "th";
		}
		return num;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void join(PlayerJoinEvent event) {
		event.setJoinMessage("");
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void quit(PlayerQuitEvent event) {
		event.setQuitMessage("");
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void kick(PlayerKickEvent event) {
		event.setLeaveMessage("");
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void death(PlayerDeathEvent event) {
		for (String s : UltraHC.specs) {
			Player p = Bukkit.getPlayer(s);
			if (p != null) {
				p.sendMessage("§c[Death] §f" + event.getDeathMessage());
			}
		}
		event.getEntity().sendMessage(event.getDeathMessage());
		deaths.add(event.getDeathMessage());
		event.setDeathMessage("");
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Location f = event.getFrom();
		Location t = event.getTo();
		if ((freeze) && ((f.getBlockX() != t.getBlockX()) || (f.getBlockZ() != t.getBlockZ()))) {
			while (f.getBlock().getType() != Material.AIR && f.getY() > 0) {
				f.setY(f.getY() - 0.5);
			}
			event.setTo(f);
		}
	}

	@SuppressWarnings("deprecation")
	public static void dropChests() {
		int[][] gen = { { 2, 5 }, { 5, 12 }, { 10, 16 }, { 15, 20 } };
		int y = Multiworld.getUHC().getHighestBlockYAt(0, 0) + 50;

		for (int[] ia : gen) {
			Location[] locs = getCircleLocations(Multiworld.getUHC(), ia[0], ia[1], y);

			for (Location l : locs) {
				FallingBlock fb = l.getWorld().spawnFallingBlock(l, Material.CHEST, (byte) rand.nextInt(16));
				chests.add(fb);
				fb.setDropItem(false);
				fb.setVelocity(new org.bukkit.util.Vector(0, -1, 0));
			}
		}
	}

	public static void createCenter() {
		World w = Multiworld.getUHC();
		BukkitWorld bw = new BukkitWorld(w);

		high = w.getHighestBlockYAt(0, 0) - 1;

		EditSession es = new EditSession(bw, Integer.MAX_VALUE);

		Region air = new CylinderRegion(bw, new Vector(0, high, 0), new Vector2D(60, 60), high, high + 50);
		Region grass = new CylinderRegion(bw, new Vector(0, high, 0), new Vector2D(60, 60), 64, high);

		try {
			es.setBlocks(air, new BaseBlock(0));
			es.setBlocks(grass, new BaseBlock(2));
		} catch (Exception e) {
			Bukkit.broadcastMessage("§cERROR: " + e.getMessage());
		}

		Bukkit.broadcastMessage("§eDone generating center!");
	}

	public static void spreadAll(double radius, int y) {
		Location[] locs = getCircleLocations(Multiworld.getUHC(), radius, Bukkit.getOnlinePlayers().length, y);

		Player[] pls = Bukkit.getOnlinePlayers();

		List<Player> spread = new ArrayList<>();

		for (int i = 0; i < locs.length; i += 0) {
			Player r = pls[rand.nextInt(pls.length)];
			if (!spread.contains(r)) {
				r.teleport(locs[i]);
				spread.add(r);
				i++;
			}
		}
	}

	// credit to ghowden
	public static Location[] getCircleLocations(World w, double radius, double amount, double y) {

		double increment = Math.PI * 2 / amount;

		ArrayList<Location> locations = new ArrayList<>();
		for (int i = 0; i < amount; i++) {
			double angle = i * increment;

			double x = getXFromRadians(radius, angle);
			double z = getZFromRadians(radius, angle);

			Location add = new Location(w, x, y, z);

			locations.add(add);
		}
		return locations.toArray(new Location[0]);
	}

	public static double getZFromRadians(double radius, double angle) {
		return radius * StrictMath.sin(angle);
	}

	public static double getXFromRadians(double radius, double angle) {
		return radius * StrictMath.cos(angle);
	}

	static Material[]	startItems	= { Material.WOOD, Material.LOG, Material.STICK, Material.WOOD_SWORD,
			Material.STONE_SWORD, Material.SNOW_BALL, Material.COBBLESTONE, Material.GRAVEL, Material.SAPLING,
			Material.TNT, Material.FLINT_AND_STEEL, Material.GLASS_BOTTLE, Material.BOAT, Material.BUCKET,
			Material.WATER_BUCKET, Material.LAVA_BUCKET, Material.MILK_BUCKET, Material.ENDER_PEARL,
			Material.EXP_BOTTLE, Material.STRING, Material.FEATHER, Material.FLINT, Material.COAL, Material.SUGAR_CANE,
			Material.MELON_SEEDS, Material.GOLD_NUGGET, Material.ARROW, Material.LEATHER_BOOTS,
			Material.LEATHER_LEGGINGS, Material.LEATHER_CHESTPLATE, Material.LEATHER_HELMET, Material.STONE_PICKAXE,
			Material.WOOD_PICKAXE, Material.SHEARS, Material.APPLE, Material.RAW_BEEF, Material.RAW_FISH,
			Material.RAW_CHICKEN, Material.PORK, Material.COOKIE, Material.CAKE, Material.BAKED_POTATO,
			Material.PUMPKIN_PIE	};

	static Material[]	feastItems	= { Material.DIAMOND_SWORD, Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE,
			Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS, Material.ENCHANTMENT_TABLE, Material.ANVIL };

	public static ItemStack getRandomItem(boolean feast) {
		Material[] mats = feast ? feastItems : startItems;
		Material m = mats[rand.nextInt(mats.length)];
		int am = m.getMaxStackSize() > 1 ? rand.nextInt(m.getMaxStackSize() / (feast ? 10 : 2)) : 1;
		short dura = m.getMaxDurability() > 0 ? (short) rand.nextInt(m.getMaxDurability()) : 0;

		ItemStack ret = new ItemStack(m, am, dura);
		return ret;
	}
}
