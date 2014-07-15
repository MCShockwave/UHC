package net.mcshockwave.UHC.Commands;

import net.mcshockwave.UHC.Option;
import net.mcshockwave.UHC.Scenarios;
import net.mcshockwave.UHC.UltraHC;
import net.mcshockwave.UHC.Kits.Kits;
import net.mcshockwave.UHC.Listeners.HungerGamesHandler;
import net.mcshockwave.UHC.Listeners.ResurrectListener;
import net.mcshockwave.UHC.Menu.ItemMenu;
import net.mcshockwave.UHC.Menu.ItemMenu.Button;
import net.mcshockwave.UHC.Menu.ItemMenu.ButtonRunnable;
import net.mcshockwave.UHC.Utils.NametagUtils;
import net.mcshockwave.UHC.Utils.SerializationUtils;
import net.mcshockwave.UHC.db.ConfigFile;
import net.mcshockwave.UHC.worlds.Multiworld;
import net.minecraft.server.v1_7_R2.ChatSerializer;
import net.minecraft.server.v1_7_R2.PacketPlayOutChat;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;

import java.util.ArrayList;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.patterns.SingleBlockPattern;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;

public class CommandUHC implements CommandExecutor {

	public static boolean	kitMOTD	= false;

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender.isOp() && args.length > 0 && args[0].equalsIgnoreCase("reload")) {
			for (ConfigFile cf : ConfigFile.values()) {
				cf.reload();
				sender.sendMessage("§6" + cf.name + " reloaded");
			}
			return true;
		}

		if (sender instanceof Player) {
			Player p = (Player) sender;

			if (!p.isOp()) {
				return false;
			}

			if (args[0].equalsIgnoreCase("hof")) {
				if (args.length <= 4) {
					p.sendMessage("§c/uhc hof {winner} {scenario} {teams} {reddit}");
				} else {
					String win = args[1];
					win = (win.contains("and") ? win.replace('_', ' ') : win);
					String scen = args[2].replace('_', ' ');
					String team = args[3].replace('_', ' ');
					String link = args[4];

					ArrayList<String> hof = new ArrayList<>(ConfigFile.HOF.get().getStringList("entries"));
					hof.add(String.format("%s;%s;%s;%s", win, scen, team, link));
					ConfigFile.HOF.get().set("entries", hof);
					ConfigFile.HOF.update();

					p.sendMessage("§6Added HOF entry:\n§e"
							+ String.format("Winner: %s\nScenario: %s\nTeams: %s\nReddit: %s", win, scen, team, link));
				}
			}
			if (args[0].equalsIgnoreCase("start")) {
				if (args.length == 1) {
					UltraHC.startSpread(0);
				} else {
					UltraHC.startSpread(Long.parseLong(args[1]));
				}
			}
			if (args[0].equalsIgnoreCase("stop")) {
				UltraHC.stop();
			}
			if (args[0].equalsIgnoreCase("option")) {
				Option.getOptionsMenu(true).open(p);
			}
			if (args[0].equalsIgnoreCase("teams")) {
				UltraHC.nts.getMenu(p, true).open(p);
			}
			if (args[0].equalsIgnoreCase("kitlist")) {
				p.sendMessage("§eAll kits:");
				for (Kits k : Kits.values()) {
					p.sendMessage("§7§o" + k.name());
				}
			}
			if (args[0].equalsIgnoreCase("setKit")) {
				if (args.length > 1) {
					Kits k = Kits.valueOf(args[1]);
					if (k == null) {
						p.sendMessage("§cUnknown kit: \"" + args[1] + "\"");
						p.sendMessage("§e§oKits care case sensitive");
						p.sendMessage("§7§oType /uhc kitlist for a list of kits");
					} else {
						UltraHC.startACon = k.acon;
						UltraHC.startCon = k.con;
						p.sendMessage("§6Set kit to §o" + k.name());
					}
				} else {
					final PlayerInventory inv = p.getInventory();
					UltraHC.startCon = inv.getContents();
					UltraHC.startACon = inv.getArmorContents();
					p.sendMessage("§6Set kit to §ocurrent inventory");
				}
			}
			if (args[0].equalsIgnoreCase("viewKit")) {
				if (UltraHC.startCon != null) {
					UltraHC.setInventory(p, UltraHC.startCon, UltraHC.startACon);
					p.sendMessage("§6Viewing current kit");
				} else
					p.sendMessage("§cNo kit found");
			}
			if (args[0].equalsIgnoreCase("givekit")) {
				getKitMenu().open(p);
			}

			if (args[0].equalsIgnoreCase("loadCruxSpawn")) {
				Location l = p.getLocation();
				l = l.add(5, 0, 0);
				l.setY(l.getWorld().getHighestBlockYAt(l.getBlockX(), l.getBlockZ()) + 1);
				UltraHC.loadSchematic(UltraHC.cruxSchemName, l);
			}

			if (args[0].equalsIgnoreCase("onStart")) {
				for (Scenarios s : Scenarios.getEnabled()) {
					Bukkit.getPluginManager().registerEvents(s.l, UltraHC.ins);
					s.onStart();
				}
			}
			// if (args[0].equalsIgnoreCase("remholos")) {
			// for (Hologram h :
			// HoloAPI.getManager().getAllHolograms().keySet()) {
			// h.clearAllPlayerViews();
			// HoloAPI.getManager().stopTracking(h);
			// HoloAPI.getManager().clearFromFile(h);
			// }
			// }
			if (args[0].equalsIgnoreCase("addplayer")) {
				UltraHC.addPlayer(args[1]);
				p.sendMessage("§c" + args[1] + " removed from spectators and added to players");
			}
			if (args[0].equalsIgnoreCase("remresur")) {
				ResurrectListener.resu.remove(args[1]);
				p.sendMessage("§c" + args[1] + " removed from Resurrect list");
			}
			if (args[0].equalsIgnoreCase("listSpecs")) {
				p.sendMessage(UltraHC.specs.toArray(new String[0]));
			}
			if (args[0].equalsIgnoreCase("listPlayers")) {
				p.sendMessage(UltraHC.players.toArray(new String[0]));
			}
			if (args[0].equalsIgnoreCase("genborder")) {
				int rad = Integer.parseInt(args[1]);
				genWalls(Multiworld.isUHCWorld(p.getWorld()) ? p.getWorld() : Multiworld.getUHC(), rad);
			}
			if (args[0].equalsIgnoreCase("sethealth")) {
				Player set = Bukkit.getPlayer(args[1]);

				set.setHealth(Double.parseDouble(args[2]) / 5);
				UltraHC.updateHealthFor(set);
			}

			if (args[0].equalsIgnoreCase("world")) {
				World w = Bukkit.getWorld(args[1]);
				p.teleport(w.getSpawnLocation());
			}
			if (args[0].equalsIgnoreCase("spawn")) {
				World set = p.getWorld();
				Location to = p.getLocation();
				int x = to.getBlockX(), y = to.getBlockY(), z = to.getBlockZ();

				set.setSpawnLocation(x, y, z);
				p.sendMessage(String.format("§aSet spawn to x%s y%s z%s in world \"%s\"", x, y, z, set.getName()));
			}

			if (args[0].equalsIgnoreCase("kitmode")) {
				kitMOTD = !kitMOTD;

				if (kitMOTD) {
					UltraHC.maxPlayers = 20;
					Bukkit.setWhitelist(false);
					if (UltraHC.kills != null) {
						UltraHC.kills.unregister();
					}
				} else {
					Bukkit.setWhitelist(true);
					UltraHC.registerKillScoreboard();
				}

				p.sendMessage("§aKit mode is now " + kitMOTD);
			}

			if (args[0].equalsIgnoreCase("restart")) {
				for (Player p2 : Bukkit.getOnlinePlayers()) {
					p2.kickPlayer("§e§lServer Restarting");
				}
				Bukkit.getScheduler().runTaskLater(UltraHC.ins, new Runnable() {
					public void run() {
						for (Player p2 : Bukkit.getOnlinePlayers()) {
							p2.kickPlayer("§e§lServer Restarting");
						}
					}
				}, 10);
				Bukkit.getScheduler().runTaskLater(UltraHC.ins, new Runnable() {
					public void run() {
						UltraHC.deleteWorld(Multiworld.getUHC());
						UltraHC.deleteWorld(Multiworld.getNether());

						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
					}
				}, 15);
			}

			if (args[0].equalsIgnoreCase("spread")) {
				int rad = Integer.parseInt(args[1]);
				boolean delay = true;
				if (args.length > 2) {
					try {
						delay = Boolean.parseBoolean(args[2]);
					} catch (Exception e) {
					}
				}

				UltraHC.spreadPlayers(rad, delay, !delay);
			}

			if (args[0].equalsIgnoreCase("starttime")) {
				long startTime = UltraHC.count.startTime;
				((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(ChatSerializer
						.a("{\"text\":\"\",\"extra\":[{\"text\":\"" + startTime + "\",\"color\""
								+ ":\"aqua\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"" + startTime
								+ "\"}}]}"), true));
			}

			if (args[0].equalsIgnoreCase("lobbyall")) {
				for (Player p2 : Bukkit.getOnlinePlayers()) {
					UltraHC.resetPlayer(p2);
					p2.teleport(Multiworld.getLobby().getSpawnLocation());
				}
			}

			if (args[0].equalsIgnoreCase("ts")) {
				String opt = Option.serialize();
				((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(ChatSerializer
						.a("{\"text\":\"\",\"extra\":[{\"text\":\"" + opt + "\",\"color\""
								+ ":\"gold\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"" + opt
								+ "\"}}]}"), true));
			}

			if (args[0].equalsIgnoreCase("fs")) {
				String input = args[1];

				Option.loadFromString(input);

				p.sendMessage("§6Loaded options from " + input);
			}

			if (args[0].equalsIgnoreCase("kills")) {
				String in = args[1];

				if (UltraHC.totKills.containsKey(in)) {
					p.sendMessage("§a" + in + " has " + UltraHC.totKills.get(in) + " kills");
				} else {
					p.sendMessage("§c" + in + " has no kills");
				}
			}

			if (args[0].equalsIgnoreCase("tasks")) {
				p.sendMessage("§aPending Tasks: (Plugin.ID [Running/Queued])");
				p.sendMessage("§e§nAsync§r §6§nSync§r\n §e");
				for (BukkitTask bt : Bukkit.getScheduler().getPendingTasks()) {
					p.sendMessage((bt.isSync() ? "§6" : "§e")
							+ bt.getOwner().getName()
							+ "."
							+ bt.getTaskId()
							+ " "
							+ (Bukkit.getScheduler().isCurrentlyRunning(bt.getTaskId()) ? "Running" : Bukkit
									.getScheduler().isQueued(bt.getTaskId()) ? "Queued" : "Unknown"));
				}
				p.sendMessage("§aActive Workers: (ThreadName: Plugin.ID)");
				for (BukkitWorker bw : Bukkit.getScheduler().getActiveWorkers()) {
					p.sendMessage("§b" + bw.getThread().getName() + ": " + bw.getOwner().getName() + "."
							+ bw.getTaskId());
				}
			}

			if (args[0].equalsIgnoreCase("serit")) {
				String a1 = args[1];
				if (a1.equalsIgnoreCase("to")) {
					String ser = SerializationUtils.toString(p.getItemInHand());
					((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(ChatSerializer
							.a("{\"text\":\"\",\"extra\":[{\"text\":\"" + ser + "\",\"color\""
									+ ":\"red\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"" + ser
									+ "\"}}]}"), true));
				}

				if (a1.equalsIgnoreCase("fr")) {
					String argsstr = "";
					for (int i = 2; i < args.length; i++) {
						argsstr += args[i] + " ";
					}
					argsstr = argsstr.substring(0, argsstr.length() - 1);

					ItemStack gi = SerializationUtils.itemFromString(argsstr);
					p.getInventory().addItem(gi);
				}
			}

			if (args[0].equalsIgnoreCase("serinv")) {
				String a1 = args[1];
				if (a1.equalsIgnoreCase("to")) {
					String ser = SerializationUtils.toString(p.getInventory().getContents());
					String serAC = SerializationUtils.toString(p.getInventory().getArmorContents());

					Block b = p.getTargetBlock(null, 10);
					if (b.getType() == Material.COMMAND) {
						CommandBlock cb = (CommandBlock) b.getState();
						cb.setCommand(ser);
						cb.update();
						p.sendMessage("§cSet command block to: §f" + ser);
					}

					Block bu = b.getRelative(0, 1, 0);
					if (bu.getType() == Material.COMMAND) {
						CommandBlock cb = (CommandBlock) bu.getState();
						cb.setCommand(serAC);
						cb.update();
						p.sendMessage("§cSet command block above to: §f" + serAC);
					}
				}

				if (a1.equalsIgnoreCase("fr")) {
					String argsstr = "";
					for (int i = 2; i < args.length; i++) {
						argsstr += args[i] + " ";
					}
					argsstr = argsstr.substring(0, argsstr.length() - 1);

					ItemStack[] si = SerializationUtils.itemsFromString(argsstr, p.getInventory().getContents().length);
					p.getInventory().setContents(si);
					p.updateInventory();
				}
			}

			if (args[0].equalsIgnoreCase("spreadCir")) {
				double rad = Integer.parseInt(args[1]);
				HungerGamesHandler.spreadAll(rad, Multiworld.getUHC().getHighestBlockYAt(0, 0) + 1);
			}

			if (args[0].equalsIgnoreCase("genCenterHG")) {
				HungerGamesHandler.createCenter();
			}

			if (args[0].equalsIgnoreCase("dropChestsHG")) {
				HungerGamesHandler.dropChests();
			}

			if (args[0].equalsIgnoreCase("displayDeathsHG")) {
				HungerGamesHandler.displayDeaths();
			}

			if (args[0].equalsIgnoreCase("hidetag")) {
				if (args.length > 1) {
					NametagUtils.hideNametag(Bukkit.getPlayer(args[1]));
				} else {
					NametagUtils.hideNametag((Player) sender);
				}
			}

			if (args[0].equalsIgnoreCase("showtag")) {
				if (args.length > 1) {
					NametagUtils.showNametag(Bukkit.getPlayer(args[1]));
				} else {
					NametagUtils.showNametag((Player) sender);
				}
			}
		}
		return true;
	}

	public static void genWalls(World w, int rad) {
		EditSession es = new EditSession(new BukkitWorld(w), Integer.MAX_VALUE);

		Bukkit.broadcastMessage("§bGENERATING WALLS WITH RADIUS " + rad);
		Region r = new CuboidRegion(new Vector(-rad, 0, -rad), new Vector(rad, 256, rad));
		try {
			es.makeCuboidWalls(r, new SingleBlockPattern(new BaseBlock(7)));
			Bukkit.broadcastMessage("§aWALLS GENERATED");
		} catch (MaxChangedBlocksException e) {
			e.printStackTrace();
			Bukkit.broadcastMessage("§cERROR: " + e.getMessage());
		}

		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wb " + w.getName() + " set " + rad + " 0 0");
	}

	public static ItemMenu getKitMenu() {
		ItemMenu m = new ItemMenu("Give Kits", 9);

		Button player = new Button(false, Material.SKULL_ITEM, 1, 3, "Players", "Click to open menu");
		m.addButton(player, 4);
		m.addSubMenu(getPlayerKitMenu(), player, true);

		return m;
	}

	public static ItemMenu getPlayerKitMenu() {
		ItemMenu m = new ItemMenu("Give Kits - Players", Bukkit.getOnlinePlayers().length);

		int slot = 0;
		for (final Player p : Bukkit.getOnlinePlayers()) {
			Button b = new Button(false, Material.SKULL_ITEM, 1, 3, p.getName(), "Click to give kit");
			m.addButton(b, slot);
			b.setOnClick(new ButtonRunnable() {
				public void run(Player c, InventoryClickEvent event) {
					UltraHC.setInventory(p, UltraHC.startCon, UltraHC.startACon);

					c.sendMessage("§cGave kit to " + p.getName());
				}
			});

			slot++;
		}

		return m;
	}
}
