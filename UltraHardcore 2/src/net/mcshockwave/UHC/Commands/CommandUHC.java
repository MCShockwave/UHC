package net.mcshockwave.UHC.Commands;

import net.mcshockwave.UHC.Option;
import net.mcshockwave.UHC.Scenarios;
import net.mcshockwave.UHC.UltraHC;
import net.mcshockwave.UHC.Listeners.ResurrectListener;
import net.mcshockwave.UHC.Menu.ItemMenu;
import net.mcshockwave.UHC.Menu.ItemMenu.Button;
import net.mcshockwave.UHC.Menu.ItemMenu.ButtonRunnable;
import net.mcshockwave.UHC.db.ConfigFile;
import net.mcshockwave.UHC.worlds.Multiworld;
import net.minecraft.server.v1_7_R2.ChatSerializer;
import net.minecraft.server.v1_7_R2.PacketPlayOutChat;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.Hologram;
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
					UltraHC.start(0);
				} else {
					UltraHC.start(Long.parseLong(args[1]));
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
			if (args[0].equalsIgnoreCase("setKit")) {
				final PlayerInventory inv = p.getInventory();
				UltraHC.startCon = inv.getContents();
				UltraHC.startACon = inv.getArmorContents();
				p.sendMessage("§6Set kit to current inventory");
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
			if (args[0].equalsIgnoreCase("remholos")) {
				for (Hologram h : HoloAPI.getManager().getAllHolograms().keySet()) {
					h.clearAllPlayerViews();
					HoloAPI.getManager().stopTracking(h);
					HoloAPI.getManager().clearFromFile(h);
				}
			}
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

				UltraHC.deleteWorld(Multiworld.getUHC());
				UltraHC.deleteWorld(Multiworld.getNether());

				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
			}

			if (args[0].equalsIgnoreCase("spread")) {
				int rad = Integer.parseInt(args[1]);

				UltraHC.spreadPlayers(rad);
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
