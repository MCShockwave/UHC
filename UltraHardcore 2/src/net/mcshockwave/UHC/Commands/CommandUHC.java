package net.mcshockwave.UHC.Commands;

import net.mcshockwave.UHC.Option;
import net.mcshockwave.UHC.Scenarios;
import net.mcshockwave.UHC.UltraHC;
import net.mcshockwave.UHC.Listeners.ResurrectListener;
import net.mcshockwave.UHC.worlds.Multiworld;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

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
		if (sender instanceof Player) {
			Player p = (Player) sender;

			if (!p.isOp()) {
				return false;
			}

			if (args[0].equalsIgnoreCase("start")) {
				UltraHC.start();
			}
			if (args[0].equalsIgnoreCase("stop")) {
				UltraHC.stop();
			}
			if (args[0].equalsIgnoreCase("option")) {
				Option.getGlobalMenu().open(p);
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

			if (args[0].equalsIgnoreCase("loadCruxSpawn")) {
				Location l = p.getLocation();
				l = l.add(5, 0, 0);
				l.setY(l.getWorld().getHighestBlockYAt(l.getBlockX(), l.getBlockZ()) + 1);
				UltraHC.loadSchematic(UltraHC.cruxSchemName, l);
			}

			if (args[0].equalsIgnoreCase("onStart")) {
				Scenarios s = Option.getScenario();
				Bukkit.getPluginManager().registerEvents(s.l, UltraHC.ins);
				s.onStart();
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
				EditSession es = new EditSession(new BukkitWorld(p.getWorld()), Integer.MAX_VALUE);

				Bukkit.broadcastMessage("§bGENERATING WALLS WITH RADIUS " + rad);
				Region r = new CuboidRegion(new Vector(-rad, 0, -rad), new Vector(rad, 256, rad));
				try {
					es.makeCuboidWalls(r, new SingleBlockPattern(new BaseBlock(7)));
					Bukkit.broadcastMessage("§aWALLS GENERATED");
				} catch (MaxChangedBlocksException e) {
					e.printStackTrace();
					Bukkit.broadcastMessage("§cERROR: " + e.getMessage());
				}
			}
			if (args[0].equalsIgnoreCase("sethealth")) {
				Player set = Bukkit.getPlayer(args[1]);

				set.setHealth(Double.parseDouble(args[2]));
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
			
			if (args[0].equalsIgnoreCase("motd")) {
				kitMOTD = !kitMOTD;
				
				p.sendMessage("§aKit MOTD is now " + kitMOTD);
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
		}
		return true;
	}
}
