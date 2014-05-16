package net.mcshockwave.UHC.Listeners;

import net.mcshockwave.MCS.Utils.PacketUtils;
import net.mcshockwave.MCS.Utils.PacketUtils.ParticleEffect;
import net.mcshockwave.UHC.NumberedTeamSystem.NumberTeam;
import net.mcshockwave.UHC.Option;
import net.mcshockwave.UHC.UltraHC;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

public class DTMListener implements Listener {

	public static HashMap<Block, Integer>	monu	= new HashMap<>();

	public static BukkitTask				part	= null;

	public static void start() {
		Bukkit.broadcastMessage("§a§lYou have " + Option.PVP_Time.getInt() + " minutes to build your base!");

		for (NumberTeam nt : UltraHC.nts.teams.toArray(new NumberTeam[0])) {
			if (nt.getOnlinePlayers().size() > 0) {
				Location monuLoc = nt.getOnlinePlayers().get(0).getLocation();
				monuLoc.setY(150);

				Block m = monuLoc.getBlock();
				m.setType(Material.ENDER_STONE);

				monu.put(m, nt.id);
			} else {
				UltraHC.nts.removeTeam(nt);
			}
		}

		part = Bukkit.getScheduler().runTaskTimer(UltraHC.ins, new Runnable() {
			public void run() {
				for (Block b : monu.keySet()) {
					PacketUtils.playParticleEffect(ParticleEffect.ENCHANTMENT_TABLE, b.getLocation(), 0, 1, 10);
				}
			}
		}, 2, 2);
	}

	public static void onPVP() {
		Bukkit.broadcastMessage("§b§lScouting is now allowed!");
		for (Player p : UltraHC.getAlive()) {
			p.getInventory().addItem(new ItemStack(Material.SHEARS));
		}
	}

	public static void stop() {
		part.cancel();
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Block b = event.getBlock();
		Player p = event.getPlayer();

		if (monu.keySet().contains(b)) {
			NumberTeam nt = UltraHC.nts.getFromId(monu.get(b));
			NumberTeam des = UltraHC.nts.getTeam(p.getName());

			Bukkit.broadcastMessage("§c§lTeam " + nt.id + "'s Monument has been broken by " + p.getName() + " [Team "
					+ des.id + "]!");

			UltraHC.nts.removeTeam(des);
			for (Player tm : nt.getOnlinePlayers()) {
				tm.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, Integer.MAX_VALUE, 1));
			}
		}
	}
}
