package net.mcshockwave.UHC;

import net.mcshockwave.MCS.SQLTable;
import net.mcshockwave.MCS.SQLTable.Rank;
import net.mcshockwave.UHC.NumberedTeamSystem.NumberTeam;
import net.mcshockwave.UHC.Commands.CommandUHC;
import net.mcshockwave.UHC.Commands.VoteCommand;
import net.mcshockwave.UHC.HoF.HallOfFame;
import net.mcshockwave.UHC.Listeners.MoleListener;
import net.mcshockwave.UHC.Menu.ItemMenu;
import net.mcshockwave.UHC.Menu.ItemMenu.Button;
import net.mcshockwave.UHC.Menu.ItemMenu.ButtonRunnable;
import net.mcshockwave.UHC.Utils.BlockFace2DVector;
import net.mcshockwave.UHC.Utils.ItemMetaUtils;
import net.mcshockwave.UHC.worlds.Multiworld;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.Sound;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.WordUtils;

public class DefaultListener implements Listener {

	Random									rand			= new Random();

	public static int						maxLength		= 8;

	public static HashMap<String, Integer>	livesRemaining	= new HashMap<>();

	@EventHandler
	public void join(PlayerJoinEvent event) {
		UltraHC.rerandomize();
	}

	@EventHandler
	public void quit(PlayerQuitEvent event) {
		UltraHC.rerandomize();
	}

	@EventHandler
	public void kick(PlayerKickEvent event) {
		UltraHC.rerandomize();
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();

		if (!UltraHC.started) {
			Location spawn = Multiworld.getLobby().getSpawnLocation();
			p.teleport(spawn);

			UltraHC.resetPlayer(p);

			if (!UltraHC.isMCShockwaveEnabled()) {
				p.getInventory().addItem(UltraHC.getHOF());
			}

			// List<Material> mats = Arrays.asList(new Material[] {
			// Material.WATER, Material.STATIONARY_WATER,
			// Material.LAVA, Material.STATIONARY_LAVA, Material.CACTUS });
			//
			// if (mats.contains(b.getType())) {
			// int s = 4;
			// for (int x = -s; x < s; x++) {
			// for (int z = -s; z < s; z++) {
			// b.getWorld().getBlockAt(x, b.getLocation().getBlockY(),
			// z).setType(Material.GRASS);
			// }
			// }
			// }
		} else if (UltraHC.specs.contains(p.getName())) {
			p.setAllowFlight(true);

			for (Player p2 : Bukkit.getOnlinePlayers()) {
				if (!UltraHC.specs.contains(p2.getName())) {
					p2.hidePlayer(p);
				} else {
					p2.showPlayer(p);
				}

				p.showPlayer(p2);
			}

			event.setJoinMessage("§3§o" + p.getName() + " is now spectating");

			if (UltraHC.nts.getTeam(p.getName()) != null) {
				UltraHC.nts.getTeam(p.getName()).removePlayer(p.getName());
			}

			p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));
		} else {
			for (Player p2 : Bukkit.getOnlinePlayers()) {
				if (UltraHC.specs.contains(p2.getName())) {
					p.hidePlayer(p2);
				} else {
					p.showPlayer(p2);
				}

				p2.showPlayer(p);
			}
		}

		if (UltraHC.nts.getTeam(p.getName()) != null) {
			p.setScoreboard(UltraHC.nts.getTeam(p.getName()).sc);
		}

		if (UltraHC.specs.contains(p.getName())) {
			int len = p.getName().length();
			p.setPlayerListName("\u2718" + (len >= 16 ? p.getName().substring(0, 15) : p.getName()));
		} else if (p.getName().length() > maxLength + 1) {
			p.setPlayerListName(getShortName(p));
		}
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if (!UltraHC.started) {
			event.setCancelled(true);
		}
	}

	public static String getShortName(Player p) {
		String name = p.getName();
		name = name.substring(0, maxLength) + "..";
		return name;
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if (event.getEntityType() == EntityType.GHAST) {
			event.getDrops().clear();
			event.getDrops().add(new ItemStack(Material.GOLD_INGOT, rand.nextInt(8) + 1));
			event.getDrops().add(new ItemStack(Material.BLAZE_ROD, rand.nextInt(2) + 1));
		}
	}

	@EventHandler
	public void onServerListPing(ServerListPingEvent event) {
		event.setMaxPlayers(UltraHC.maxPlayers);
		String sta = CommandUHC.kitMOTD ? "§a[Kit PVP]" : UltraHC.started ? "§4[Started]"
				: (Bukkit.getOnlinePlayers().length >= UltraHC.maxPlayers) ? "§4[Full]"
						: Bukkit.hasWhitelist() ? "§4[Whitelisted]" : "§a[Joinable]";
		event.setMotd("§cMCShockwave §7UHC §8- " + sta);
	}

	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		final Player p = event.getPlayer();

		if (BanManager.isBanned(p.getName())) {
			event.disallow(Result.KICK_BANNED, BanManager.getBanReason(p.getName()));
		}

		if (UltraHC.maxPlayers <= Bukkit.getOnlinePlayers().length) {
			event.disallow(Result.KICK_FULL, "§cServer full!");
		}

		if (UltraHC.started) {
			if (!UltraHC.players.contains(p.getName()) && !UltraHC.specs.contains(p.getName()) && canSpectate(p)) {
				UltraHC.specs.add(p.getName());
			}
			if (UltraHC.specs.contains(p.getName()) && !canSpectate(p)) {
				event.disallow(Result.KICK_WHITELIST, "§cYou are out of this game!");
			} else if (!UltraHC.players.contains(p.getName()) && !canSpectate(p)) {
				event.disallow(Result.KICK_WHITELIST, "§cThe game has already started!");
			}
			// else if (logOut.containsKey(p.getName())) {
			// logOut.get(p.getName()).cancel();
			// logOut.remove(p.getName());
			// }
		}
	}

	public static boolean canSpectate(Player p) {
		String op = Option.Spectating.getString();
		if (p.isOp()) {
			return true;
		}
		if (op.equalsIgnoreCase("ALL")) {
			return true;
		}
		if (op.equalsIgnoreCase("Whitelisted Only")) {
			return p.isWhitelisted();
		}
		if (op.equalsIgnoreCase("OP Only")) {
			return p.isOp();
		}

		if (UltraHC.isMCShockwaveEnabled()) {
			return SQLTable.hasRank(p.getName(), Rank.valueOf(op.toUpperCase().replace(' ', '_')));
		} else {
			return p.isWhitelisted();
		}
	}

	// HashMap<String, BukkitTask> logOut = new HashMap<>();

	// @EventHandler
	// public void onPlayerLeave(PlayerQuitEvent event) {
	// final Player p = event.getPlayer();
	//
	// if (UltraHC.started) {
	// } else {
	// // if (!UltraHC.specs.contains(p.getName())) {
	// // BukkitTask bt = new BukkitRunnable() {
	// // public void run() {
	// // logOut.remove(p.getName());
	// // Bukkit.broadcastMessage("§c" + p.getName() +
	// // " was logged out too long!");
	// // p.getLocation().getChunk().load();
	// // UltraHC.players.remove(p.getName());
	// // UltraHC.onDeath(p);
	// // }
	// // }.runTaskLater(UltraHC.ins, 12000);
	// //
	// // logOut.put(p.getName(), bt);
	// // }
	// }
	// }

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();

		if (!UltraHC.started && UltraHC.nts.getTeam(p.getName()) != null) {
			if (UltraHC.nts.getTeam(p.getName()).getOnlinePlayers().size() <= 1) {
				UltraHC.nts.removeTeam(UltraHC.nts.getTeam(p.getName()));
			}
		}

		if (UltraHC.specs.contains(p.getName())) {
			event.setQuitMessage("§3§o" + p.getName() + " is no longer spectating");
		}
	}

	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		if (UltraHC.started && event.getReason().contains("Kicked")
				&& !UltraHC.specs.contains(event.getPlayer().getName())) {
			UltraHC.onDeath(event.getPlayer());
			Bukkit.broadcastMessage("§c" + event.getPlayer().getName()
					+ " was killed for getting kicked for reason: §r\n" + event.getReason());
		}

		if (UltraHC.specs.contains(event.getPlayer().getName())) {
			event.setLeaveMessage("§3§o" + event.getPlayer().getName() + " is no longer spectating");
		}
	}

	public static BlockFace getCardinalDirection(Entity entity) {
		double yaw = entity.getLocation().getYaw();
		yaw = Math.toRadians(yaw);
		return BlockFace2DVector.getClosest(yaw);
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		final Player p = event.getEntity();

		String dm = event.getDeathMessage();
		if (dm.contains(" using ")) {
			dm = event.getDeathMessage().substring(0, dm.lastIndexOf(" using "));
		}
		event.setDeathMessage(dm);

		if (UltraHC.started && UltraHC.getAlive().contains(p)) {

			if (Option.Head_on_Fence.getBoolean() && p.getLocation().getBlock().getType() == Material.AIR
					&& p.getLocation().getBlock().getRelative(BlockFace.UP).getType() == Material.AIR) {
				p.getLocation().getBlock().setType(Material.NETHER_FENCE);
				Block sk = p.getLocation().getBlock().getRelative(BlockFace.UP);
				sk.setType(Material.SKULL);
				sk.setData((byte) 1);
				Skull he = (Skull) sk.getState();
				he.setSkullType(SkullType.PLAYER);
				he.setOwner(p.getName());
				he.setRotation(getCardinalDirection(p));
				he.update(true);
			} else {
				event.getDrops().add(
						ItemMetaUtils.setHeadName(new ItemStack(Material.SKULL_ITEM, 1, (short) 3), p.getName()));
			}
			// if (Option.Hologram_on_Death.getBoolean()) {
			// Hologram h = new
			// HologramFactory(UltraHC.ins).withLocation(p.getLocation().add(0.5,
			// 1.75, 0.5))
			// .withText("§c§lR.I.P.", event.getDeathMessage()).build();
			// for (Player p2 : Bukkit.getOnlinePlayers()) {
			// h.show(p2);
			// }
			// }
			if (Scenarios.Mole.isEnabled()) {
				boolean mole = MoleListener.isMole(p.getName());

				Bukkit.broadcastMessage("§aThis player was " + (mole ? "§ca mole" : "§bnot a mole"));

				for (ItemStack it : event.getDrops().toArray(new ItemStack[0])) {
					if (ItemMetaUtils.hasLore(it)
							&& ItemMetaUtils.getLoreArray(it)[0].equalsIgnoreCase("§7§6Mole Item")) {
						event.getDrops().remove(it);
					}
				}
			}
			for (Player p2 : Bukkit.getOnlinePlayers()) {
				p2.playSound(p.getLocation(), Sound.AMBIENCE_THUNDER, 1000, 2);

				if (Option.Death_Distance.getBoolean()) {
					final Player p3 = p2;
					Bukkit.getScheduler().runTaskLater(UltraHC.ins, new Runnable() {
						public void run() {
							if (p3.getWorld() == p.getWorld()) {
								p3.sendMessage("§cThe player died "
										+ getRoundedDistance(p3.getLocation(), p.getLocation())
										+ " blocks away from you");
							} else {
								p3.sendMessage("§cThe player died in a different world");
							}
						}
					}, 1);
				}
			}
			if (Scenarios.Team_DM.isEnabled()) {
				event.getDrops().clear();
			}
		}
	}

	public double getRoundedDistance(Location l, Location l2) {
		double dis = l.distance(l2);

		dis = Math.round(dis * 10);
		dis /= 10;

		return dis;
	}

	@EventHandler
	public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
		final Player p = event.getPlayer();
		final ItemStack it = event.getItem();
		if (it.getType() == Material.GOLDEN_APPLE) {
			Bukkit.getScheduler().runTask(UltraHC.ins, new Runnable() {
				public void run() {
					if (!Option.Absorption.getBoolean())
						p.removePotionEffect(PotionEffectType.ABSORPTION);
					if (ItemMetaUtils.hasCustomName(it)
							&& ItemMetaUtils.getItemName(it).equalsIgnoreCase("§6Golden Head")) {
						p.removePotionEffect(PotionEffectType.REGENERATION);
						p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 180, 1));
					}
				}
			});
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerRespawn(final PlayerRespawnEvent event) {
		final Player p = event.getPlayer();

		Bukkit.getScheduler().runTaskLater(UltraHC.ins, new Runnable() {
			public void run() {
				p.teleport(event.getRespawnLocation());
			}
		}, 10l);

		event.setRespawnLocation(UltraHC.getScatterLocation(p));

		if (getLives(p.getName()) == -8 || getLives(p.getName()) > 1) {
			if (getLives(p.getName()) != -8) {
				addLives(p.getName(), -1);
				Bukkit.broadcastMessage("§e§l" + p.getName() + " HAS " + getLives(p.getName()) + " RESPAWNS REMAINING");
			}

			if (UltraHC.startCon != null) {
				UltraHC.setInventory(p, UltraHC.startCon, UltraHC.startACon);
			}

			int time = 5;
			p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, time * 20, 100));
			p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, time * 20, 100));
			p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, time * 20, 100));

			return;
		}

		if (getLives(p.getName()) == 1 || getLives(p.getName()) == 0) {
			Bukkit.broadcastMessage("§e§l" + p.getName() + " IS OUT OF RESPAWNS!");
		}

		if (UltraHC.started && !UltraHC.specs.contains(p.getName())) {
			UltraHC.onDeath(p);
		}

		if (p.getWorld() == Multiworld.getKit()) {
			event.setRespawnLocation(Multiworld.getKit().getSpawnLocation());
		} else {
			event.setRespawnLocation(Multiworld.getLobby().getSpawnLocation());
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity().getWorld() == Multiworld.getKit()) {
			return;
		}

		if (!UltraHC.started) {
			event.setCancelled(true);
		}

		Entity e = event.getEntity();

		if (e instanceof Player && UltraHC.specs.contains(((Player) e).getName())) {
			event.setCancelled(true);
		}

		if (e instanceof Player) {
			final Player p = (Player) e;

			if (UltraHC.started && event.getCause() == DamageCause.SUFFOCATION && UltraHC.count.getTime() <= 60) {
				p.teleport(p.getLocation().add(0, 1, 0));
				p.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 10, 100));
			}

			UltraHC.updateHealthFor(p);

			// if (Option.Damage_Indicators.getBoolean()) {
			// final double health = p.getHealth();
			// Bukkit.getScheduler().runTaskLater(UltraHC.ins, new Runnable() {
			// public void run() {
			// double healthEnd = p.getHealth();
			// double damage = health - healthEnd;
			// damage = (double) Math.round(damage * 10) / 10;
			// if (damage <= 0) {
			// return;
			// }
			// HoloAPI.getManager().createSimpleHologram(
			// LocUtils.addRand(p.getLocation().clone().add(0.5, 1, 0.5), 1, 0,
			// 1), 1, true,
			// "§c§l-" + (damage * 5) + "%");
			// }
			// }, 1l);
			// }
		}
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if (!UltraHC.started && event.getEntity().getWorld() != Multiworld.getKit()
				|| UltraHC.specs.contains(event.getEntity().getName())) {
			event.setFoodLevel(20);
			((Player) event.getEntity()).setSaturation(10f);
			event.getEntity().setHealth(event.getEntity().getMaxHealth());
		}

		if (!Option.Hunger.getBoolean()) {
			event.setFoodLevel(20);
			((Player) event.getEntity()).setSaturation(10f);
		}
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		Entity ee = event.getEntity();
		Entity de = event.getDamager();

		if (ee.getWorld() == Multiworld.getKit()) {
			return;
		}

		if (de instanceof Player) {
			// Player p = (Player) ee;s
			Player d = (Player) de;

			if (ee instanceof Player && UltraHC.started && !UltraHC.isPVP()) {
				event.setCancelled(true);
				d.sendMessage("§cKilling is disabled until " + Option.PVP_Time.getInt() + " minutes in!");
			}

			if (UltraHC.started && UltraHC.specs.contains(d.getName())) {
				event.setCancelled(true);
			}
		}

		if (ee instanceof Player) {
			Player p = (Player) ee;

			Player d = null;
			if (de instanceof Projectile && ((Projectile) de).getShooter() instanceof Player) {
				d = (Player) ((Projectile) de).getShooter();
			}
			if (de instanceof Player) {
				d = (Player) de;
			}
			if (d != null) {
				if (!Option.Friendly_Fire.getBoolean() && UltraHC.nts.getTeam(p.getName()) != null
						&& UltraHC.nts.getTeam(d.getName()) != null
						&& UltraHC.nts.getTeam(p.getName()) == UltraHC.nts.getTeam(d.getName())) {
					event.setCancelled(true);
				}
			}
		}

		// spec blocking
		if (ee instanceof Player && de instanceof Projectile) {
			Projectile arrow = (Projectile) de;
			Player d = (Player) arrow.getShooter();
			Player p = (Player) ee;

			if (UltraHC.getAlive().contains(p) && UltraHC.getAlive().contains(d) && UltraHC.started && !UltraHC.isPVP()) {
				event.setCancelled(true);
				d.sendMessage("§cKilling is disabled until " + Option.PVP_Time.getInt() + " minutes in!");
			}

			Vector velocity = arrow.getVelocity();
			Class<? extends Projectile> pc = arrow.getClass();

			if (UltraHC.specs.contains(p.getName())) {
				p.teleport(p.getLocation().add(0, 5, 0));
				String type = arrow.getType().name().toLowerCase().replace('_', ' ');
				String n = ("aeiou".indexOf(type.substring(0, 1)) != -1) ? "n" : "";

				p.sendMessage("§cYou are in the way of a" + n + " " + type);

				Projectile newArrow = d.launchProjectile(pc);
				newArrow.setShooter(d);
				newArrow.setVelocity(velocity);
				newArrow.setBounce(false);

				event.setCancelled(true);
				arrow.remove();
			}
		}
	}

	@EventHandler
	public void onCraftItem(PrepareItemCraftEvent event) {
		CraftingInventory ci = event.getInventory();
		if (ci.getResult().getType() == Material.GOLDEN_APPLE && ItemMetaUtils.hasCustomName(ci.getResult())) {
			if (!Option.Golden_Heads.getBoolean()) {
				ci.setResult(new ItemStack(Material.AIR));
			} else {
				String name = "null";
				for (ItemStack itemStack : event.getInventory().getContents()) {
					if (itemStack.getType() == Material.SKULL_ITEM) {
						SkullMeta sm = (SkullMeta) itemStack.getItemMeta();
						name = sm.getOwner();
					}
				}

				ItemStack res = ci.getResult();
				ci.setResult(ItemMetaUtils.setLore(res, "§eMade from the head of:", "§e§l" + name));
			}
		}
		if (ci.getResult().getType() == Material.GOLDEN_APPLE && ci.getResult().getDurability() == 1) {
			ci.setResult(new ItemStack(Material.AIR));
		}
		if (ci.getResult().getType() == Material.BLAZE_POWDER && !Option.Strength_Potions.getBoolean()) {
			ci.setResult(new ItemStack(Material.AIR));
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (!UltraHC.started && event.getPlayer().getGameMode() != GameMode.CREATIVE
				|| UltraHC.specs.contains(event.getPlayer().getName())) {
			event.setCancelled(true);
		}
		Block b = event.getBlock();
		if (b.getWorld() == Multiworld.getKit()) {
			event.setCancelled(false);
			return;
		}

		if (b.getType() == Material.SOUL_SAND) {
			if (rand.nextInt(10) == 0) {
				event.setCancelled(true);
				b.setType(Material.AIR);
				b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.NETHER_STALK));
			}
		}

		if (Option.Increased_Apples.getBoolean()) {
			if (b.getType() == Material.LEAVES || b.getType() == Material.LEAVES_2) {
				if (rand.nextInt(5) == 0) {
					event.setCancelled(true);
					b.setType(Material.AIR);
					b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, b.getType(), b.getData());
					b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.APPLE));
				}
			}
		}

		if (Option.Increased_Flint.getBoolean() && b.getType() == Material.GRAVEL) {
			if (rand.nextInt(2) == 0) {
				event.setCancelled(true);
				b.setType(Material.AIR);
				b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, b.getType(), b.getData());
				b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.FLINT));
			}
		}

		Material bm = b.getType();
		if (b.getLocation().getWorld() == Multiworld.getUHC() && Math.abs(b.getLocation().getX()) < 4
				&& Math.abs(b.getLocation().getX()) < 4 && UltraHC.count.getTotalMins() >= Option.Game_Length.getInt()
				&& (bm == Material.IRON_BLOCK || bm == Material.BEACON)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (!UltraHC.started && event.getPlayer().getGameMode() != GameMode.CREATIVE
				|| UltraHC.specs.contains(event.getPlayer().getName())) {
			event.setCancelled(true);
		}

		Block b = event.getBlock();
		if (b.getWorld() == Multiworld.getKit()) {
			event.setCancelled(false);
			return;
		}
	}

	@EventHandler
	public void onPlayerRegainHealth(EntityRegainHealthEvent event) {
		Entity e = event.getEntity();
		if (e instanceof Player) {
			Player p = (Player) e;
			if (event.getRegainReason() == RegainReason.SATIATED && Option.UHC_Mode.getBoolean()) {
				event.setCancelled(true);
			}
			UltraHC.updateHealthFor(p);
		}

		// if (e instanceof Player
		// && Option.Damage_Indicators.getBoolean()
		// && (Option.UHC_Mode.getBoolean() && event.getRegainReason() !=
		// RegainReason.SATIATED || !Option.UHC_Mode
		// .getBoolean()) && event.getAmount() < 100) {
		// final Player p = (Player) e;
		// final double health = p.getHealth();
		// Bukkit.getScheduler().runTaskLater(UltraHC.ins, new Runnable() {
		// public void run() {
		// double healthEnd = p.getHealth();
		// double regain = healthEnd - health;
		// regain = (double) Math.round(regain * 10) / 10;
		// if (regain <= 0) {
		// return;
		// }
		// HoloAPI.getManager().createSimpleHologram(
		// LocUtils.addRand(p.getLocation().clone().add(0.5, 1, 0.5), 1, 0, 1),
		// 1, true,
		// "§a§l+" + (regain * 5) + "%");
		// }
		// }, 1l);
		// }
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		final Player p = event.getPlayer();

		if (NumberedTeamSystem.enteringPassword.containsKey(p)) {
			NumberTeam nt = NumberedTeamSystem.enteringPassword.get(p);

			if (UltraHC.started || !Option.Team_Commands.getBoolean()) {

			}

			if (nt.password.equals(event.getMessage())) {
				nt.addPlayer(p.getName());
				p.sendMessage("§aJoined team " + nt.id + "!");
			} else {
				p.sendMessage("§cInvalid password: '" + event.getMessage() + "'");
				p.sendMessage("§eRemember, passwords are cAsE sEnsITiVE!");
			}

			NumberedTeamSystem.enteringPassword.remove(p);
			event.setCancelled(true);
			return;
		}

		if (UltraHC.chatSilenced && !p.isOp()) {
			p.sendMessage("§cChat is silenced!");
			event.setCancelled(true);
			return;
		}

		if (event.getMessage().startsWith("!")) {
			event.getRecipients().clear();
			event.setMessage("§7" + event.getMessage().replaceFirst("!", ""));

			for (Player p2 : Bukkit.getOnlinePlayers()) {
				if (UltraHC.nts.getTeam(p.getName()) == UltraHC.nts.getTeam(p2.getName())) {
					event.getRecipients().add(p2);
				}
			}
		}

		if (!event.getMessage().startsWith("@") && UltraHC.specs.contains(p.getName())) {
			if (p.isOp() && !event.getMessage().startsWith("$") || !p.isOp()) {
				event.setMessage("§7" + event.getMessage());
				for (Player p2 : UltraHC.getAlive()) {
					event.getRecipients().remove(p2);
				}
			} else {
				event.setMessage(event.getMessage().substring(1));
			}
		}

		if (UltraHC.nts.isTeamGame() && !UltraHC.isMCShockwaveEnabled()) {
			NumberTeam t = UltraHC.nts.getTeam(p.getName());
			if (t != null) {
				event.setFormat("<" + UltraHC.nts.getPrefix(t.id, true, false, true) + "§f%s§r> " + event.getMessage());
			}
		}

		if (p.isOp() && !UltraHC.isMCShockwaveEnabled()) {
			event.setFormat("§c[§lOP§c]§r " + event.getFormat());
		}

		if (!UltraHC.isMCShockwaveEnabled() && UltraHC.specs.contains(p.getName())) {
			event.setFormat("§a[§lSPEC§a]§f " + event.getFormat());
		}
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		Player p = event.getPlayer();

		if (event.getCause() == TeleportCause.ENDER_PEARL && !Option.Ender_Pearl_Damage.getBoolean()) {
			event.setCancelled(true);
			p.teleport(event.getTo());
		}
	}

	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		Player p = event.getPlayer();

		if (p.isOp() && !UltraHC.isMCShockwaveEnabled()) {
			String mes = event.getMessage();

			if (mes.contains("@all")) {
				event.setCancelled(true);
				for (Player p2 : Bukkit.getOnlinePlayers()) {
					p.performCommand(mes.replaceFirst("/", "").replace("@all", p2.getName()));
				}
			}
		}

		if (UltraHC.started && UltraHC.specs.contains(p.getName())) {
			String mes = event.getMessage();

			if (mes.equalsIgnoreCase("/tp")) {
				event.setCancelled(true);
				getTPMenu().open(p);
			}
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		Action a = event.getAction();
		ItemStack it = p.getItemInHand();
		Block b = event.getClickedBlock();

		if (UltraHC.specs.contains(p.getName()) && p.getGameMode() != GameMode.CREATIVE) {
			event.setCancelled(true);

			if (a == Action.RIGHT_CLICK_BLOCK) {
				if (b.getType() == Material.CHEST) {
					event.setCancelled(false);
					return;
				}
			}
		}

		if (UltraHC.specs.contains(p.getName()) && a == Action.LEFT_CLICK_AIR
				&& (it == null || it.getType() == Material.AIR)) {
			getTPMenu().open(p);
		}

		if (ItemMetaUtils.hasCustomName(it)
				&& ChatColor.stripColor(ItemMetaUtils.getItemName(it)).equalsIgnoreCase("Hall of Fame")) {
			event.setCancelled(true);

			HallOfFame.getMenu().open(p);
		}
	}

	public ItemMenu getTPMenu() {
		final ArrayList<Player> al = UltraHC.getAlive();
		ItemMenu m = new ItemMenu("Alive Players", al.size());

		int i = 0;
		for (final Player p2 : al) {
			int am = 1;
			if (UltraHC.nts.getTeam(p2.getName()) != null) {
				am = UltraHC.nts.getTeam(p2.getName()).id;
			}
			Team t = Bukkit.getScoreboardManager().getMainScoreboard().getPlayerTeam(p2);
			Button bu = new Button(true, Material.WOOL, am, 0, (t != null ? t.getPrefix() : "") + p2.getName()
					+ (t != null ? t.getSuffix() : ""), "Click to teleport");
			bu.setOnClick(new ButtonRunnable() {
				public void run(Player c, InventoryClickEvent event) {
					c.teleport(p2.getLocation().clone().add(5, 0, 0));
					c.sendMessage("§7Teleported to §a§o" + p2.getName());
				}
			});

			m.addButton(bu, i);
			i++;
		}

		return m;
	}

	public String getColorsHOF(String name) {
		name = "§e" + name;

		if (name.contains(" and ")) {
			name = name.replaceAll(" and ", " §7and§e ");
		}

		if (name.contains(",")) {
			name = name.replaceAll(",", "§7,§e");
		}

		return name;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		Player p = event.getPlayer();

		if (UltraHC.specs.contains(p.getName())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		for (Block b : event.blockList().toArray(new Block[0])) {
			if (b.getLocation().getWorld() == Multiworld.getUHC() && Math.abs(b.getLocation().getX()) < 4
					&& Math.abs(b.getLocation().getX()) < 4
					&& UltraHC.count.getTotalMins() >= Option.Game_Length.getInt()) {
				event.blockList().remove(b);
			}
		}
	}

	@EventHandler
	public void onEntityTarget(EntityTargetEvent event) {
		Entity t = event.getTarget();

		if (t instanceof Player) {
			Player p = (Player) t;

			if (!UltraHC.started) {
				event.setTarget(null);
				return;
			}

			if (UltraHC.specs.contains(p.getName())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (UltraHC.specs.contains(event.getWhoClicked().getName())
				&& event.getWhoClicked().getGameMode() != GameMode.CREATIVE) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		Location to = event.getTo();

		if (to.getWorld() == Multiworld.getLobby() && to.getY() < 55 && p.getGameMode() != GameMode.CREATIVE) {
			UltraHC.resetPlayer(p);

			if (!UltraHC.isMCShockwaveEnabled()) {
				p.getInventory().addItem(UltraHC.getHOF());
			}

			p.teleport(Multiworld.getLobby().getSpawnLocation());
		}

		if (UltraHC.started && !UltraHC.specs.contains(p.getName())) {
			for (Entity e : p.getNearbyEntities(3, 3, 3)) {
				if (e instanceof Player) {
					Player p2 = (Player) e;

					if (!UltraHC.specs.contains(p2.getName())) {
						p2.getLocation().setY(p2.getLocation().getY() + 1);
					}
				}
			}
		}
	}

	@EventHandler
	public void onPortal(PlayerPortalEvent event) {
		if (event.getTo().getWorld().getEnvironment() == Environment.NETHER && !Option.Enable_Nether.getBoolean()) {
			event.setCancelled(true);
		}
	}

	HashMap<Player, BukkitTask>	updateTask	= new HashMap<>();

	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		Player p = event.getPlayer();
		Entity ce = event.getRightClicked();

		if (ce instanceof Player) {
			Player c = (Player) ce;

			if (!UltraHC.getAlive().contains(p) && UltraHC.getAlive().contains(c)) {
				final PlayerInventory pi = c.getInventory();

				final ItemMenu im = new ItemMenu("Inventory of " + c.getName(), 54);

				updateInventory(im, pi);

				im.open(p);

				updateTask.put(p, new BukkitRunnable() {
					public void run() {
						updateInventory(im, pi);
					}
				}.runTaskTimer(UltraHC.ins, 0, 20));
			}
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if (event.getPlayer() instanceof Player) {
			Player p = (Player) event.getPlayer();

			if (updateTask.containsKey(p)) {
				updateTask.get(p).cancel();
				updateTask.remove(p);
			}
		}
	}

	public void updateInventory(ItemMenu im, PlayerInventory in) {
		Player p = (Player) in.getHolder();

		im.addButton(new Button(false, Material.POTION, (int) p.getHealth(), 8261, "Health"), 0);
		im.addButton(new Button(false, Material.COOKED_BEEF, p.getFoodLevel(), 0, "Food"), 1);
		im.addButton(new Button(false, Material.GOLDEN_APPLE, (int) p.getSaturation(), 0, "Saturation"), 2);

		Collection<PotionEffect> pe = p.getActivePotionEffects();
		ArrayList<String> pes = new ArrayList<>();
		for (PotionEffect poe : pe) {
			String rom = romanNumerals(poe.getAmplifier() + 1);
			int sec = poe.getDuration() / 20;
			String dura = String.format("%dm %ds", TimeUnit.SECONDS.toMinutes(sec),
					sec - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(sec)));
			pes.add(WordUtils.capitalizeFully(poe.getType().getName().replace('_', ' ')) + " " + rom + ": " + dura);
		}
		im.addButton(new Button(false, Material.POTION, 1, 0, "Potion Effects", pes.toArray(new String[0])), 8);

		for (int i = 0; i < 9; i++) { // hotbar
			im.i.setItem(i + 45, in.getItem(i));
		}

		for (int i = 18; i < 45; i++) { // inventory
			im.i.setItem(i, in.getItem(i - 9));
		}

		for (int i = 0; i < 4; i++) {
			im.i.setItem(i + 9, in.getArmorContents()[i]);
		}
	}

	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) {
		if (!Multiworld.isUHCWorld(event.getWorld()) && event.toWeatherState()) {
			event.setCancelled(true);
		}
	}

	public int getLives(String name) {
		if (Option.Lives.getString().equalsIgnoreCase("Infinite")) {
			return -8;
		}
		if (Option.Lives.getString().equalsIgnoreCase("No Respawns")) {
			return -1;
		}

		if (!livesRemaining.containsKey(name)) {
			livesRemaining.put(name, Integer.parseInt(Option.Lives.getString()));
		}

		return livesRemaining.get(name);
	}

	public void addLives(String name, int lives) {
		int l = getLives(name);

		if (l > 0) {
			livesRemaining.remove(name);
			livesRemaining.put(name, l + lives);
		}
	}

	@EventHandler
	public void vote(InventoryClickEvent event) {
		if (event.getInventory().getName().equalsIgnoreCase(ChatColor.DARK_PURPLE + "Vote!")
				&& event.getWhoClicked() instanceof Player) {
			Player p = (Player) event.getWhoClicked();
			event.setCancelled(true);
			ItemStack it = event.getCurrentItem();
			if (it != null) {
				String nam = ItemMetaUtils.getItemName(it).replaceFirst(ChatColor.GOLD.toString(), "")
						.replaceAll(" ", "_");
				for (String n : VoteCommand.votes.keySet()) {
					if (n.equalsIgnoreCase(nam)) {
						int v = VoteCommand.votes.get(nam);
						VoteCommand.votes.put(nam, v + 1);
						Bukkit.broadcastMessage(ChatColor.GOLD + p.getName() + " has cast their vote!");
						VoteCommand.voters.add(p.getName());
						p.closeInventory();
					}
				}
			}
		}
	}

	public static String romanNumerals(int Int) {
		LinkedHashMap<String, Integer> roman_numerals = new LinkedHashMap<String, Integer>();
		roman_numerals.put("M", 1000);
		roman_numerals.put("CM", 900);
		roman_numerals.put("D", 500);
		roman_numerals.put("CD", 400);
		roman_numerals.put("C", 100);
		roman_numerals.put("XC", 90);
		roman_numerals.put("L", 50);
		roman_numerals.put("XL", 40);
		roman_numerals.put("X", 10);
		roman_numerals.put("IX", 9);
		roman_numerals.put("V", 5);
		roman_numerals.put("IV", 4);
		roman_numerals.put("I", 1);
		String res = "";
		for (Map.Entry<String, Integer> entry : roman_numerals.entrySet()) {
			int matches = Int / entry.getValue();
			res += repeat(entry.getKey(), matches);
			Int = Int % entry.getValue();
		}
		return res;
	}

	public static String repeat(String s, int n) {
		if (s == null) {
			return null;
		}
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < n; i++) {
			sb.append(s);
		}
		return sb.toString();
	}
}
