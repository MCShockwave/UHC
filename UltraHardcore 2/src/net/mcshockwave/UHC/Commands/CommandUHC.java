package net.mcshockwave.UHC.Commands;

import net.mcshockwave.UHC.Option;
import net.mcshockwave.UHC.Scenarios;
import net.mcshockwave.UHC.UltraHC;

import org.bukkit.Bukkit;
import org.bukkit.Location;
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

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;

			if (!p.isOp()) {
				return false;
			}

			if (args[0].equalsIgnoreCase("start")) {
				UltraHC.start(p.getWorld());
			}
			if (args[0].equalsIgnoreCase("stop")) {
				UltraHC.stop(p.getWorld());
			}
			if (args[0].equalsIgnoreCase("option")) {
				Option.getGlobalMenu().open(p);
			}
			if (args[0].equalsIgnoreCase("teams")) {
				UltraHC.ts.getMenu().open(p);
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
				UltraHC.specs.remove(args[1]);
				UltraHC.players.add(args[1]);
				p.sendMessage("§c" + args[1] + " removed from spectators and added to players");
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
		}
		return true;
	}
}
