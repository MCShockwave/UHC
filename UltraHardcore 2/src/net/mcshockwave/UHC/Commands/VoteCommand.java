package net.mcshockwave.UHC.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;

public class VoteCommand implements CommandExecutor, Listener {

	public static HashMap<String, Integer> votes = new HashMap<String, Integer>();

	public static ArrayList<String> voters = new ArrayList<String>();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (args.length == 0) {
				if (votes.size() > 0) {
					if (!voters.contains(p.getName())) {
						Inventory i = Bukkit.createInventory(null, 18, ChatColor.DARK_PURPLE + "Vote!");
						for (String s : votes.keySet()) {
							i.addItem(rename(new ItemStack(Material.WOOL), ChatColor.GOLD + s.replaceAll("_", " ")));
						}
						p.openInventory(i);
					} else {
						p.sendMessage(ChatColor.RED + "You already voted!");
					}
				} else {
					sender.sendMessage(ChatColor.RED + "No vote is in progress!");
				}
			} else if (sender.isOp()) {
				if (args[0].equalsIgnoreCase("set")) {
					votes.clear();
					voters.clear();
					for (int i = 1; i < args.length; i++) {
						votes.put(args[i], 0);
					}
					Bukkit.broadcastMessage(ChatColor.YELLOW + ChatColor.BOLD.toString()
							+ "A new vote has been set! Type /v to vote!");
				}
				if (args[0].equalsIgnoreCase("end")) {
					Bukkit.broadcastMessage(ChatColor.YELLOW + ChatColor.BOLD.toString()
							+ "The vote has ended!");
					String mes = "\n";
					for (String s : votes.keySet()) {
						mes += ChatColor.GREEN + s + ": " + votes.get(s) + " votes\n";
					}
					sender.sendMessage(mes);
					votes.clear();
					voters.clear();
				}
				if (args[0].equalsIgnoreCase("list")) {
					String mes = "\n";
					for (String s : votes.keySet()) {
						mes += ChatColor.GREEN + s + ": " + votes.get(s) + " votes\n";
					}
					sender.sendMessage(mes);
				}
			}
		}
		return false;
	}

	public ItemStack rename(ItemStack it, String name) {
		ItemMeta m = it.getItemMeta();
		m.setDisplayName(name);
		it.setItemMeta(m);
		return it;
	}
}
