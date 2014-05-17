package net.mcshockwave.UHC.Listeners;

import net.mcshockwave.UHC.NumberedTeamSystem.NumberTeam;
import net.mcshockwave.UHC.Option;
import net.mcshockwave.UHC.UltraHC;
import net.mcshockwave.UHC.Utils.LocUtils;
import net.mcshockwave.UHC.Utils.PacketUtils;
import net.mcshockwave.UHC.Utils.PacketUtils.ParticleEffect;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map.Entry;

public class DTMListener implements Listener {

	public static HashMap<Block, Integer>	monu	= new HashMap<>();

	public static BukkitTask				part	= null;

	public static Block getFromId(int id) {
		for (Entry<Block, Integer> ent : monu.entrySet()) {
			if (ent.getValue() == id) {
				return ent.getKey();
			}
		}
		return null;
	}

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
					PacketUtils.playParticleEffect(ParticleEffect.ENCHANTMENT_TABLE,
							b.getLocation().add(0.5, 0.5, 0.5), 0.3f, 0.3f, 20);
				}
			}
		}, 2, 2);
	}

	public static void onPVP() {
		Bukkit.broadcastMessage("§b§lScouting is now allowed!");
	}

	public static void stop() {
		part.cancel();
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		NumberTeam nt = UltraHC.nts.getTeam(p.getName());

		if (nt != null && !UltraHC.isPVP()
				&& p.getLocation().distanceSquared(getFromId(nt.id).getLocation()) > 100 * 100) {
			event.setFrom(event.getTo());
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player && !UltraHC.isPVP()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Block b = event.getBlock();
		Player p = event.getPlayer();

		if (monu.containsKey(b)) {
			NumberTeam nt = UltraHC.nts.getFromId(monu.get(b));
			NumberTeam des = UltraHC.nts.getTeam(p.getName());
			event.setCancelled(true);

			if (nt == des) {
				return;
			}

			b.setType(Material.AIR);
			for (int i = 0; i < 10; i++) {
				PacketUtils.playParticleEffect(ParticleEffect.LARGE_EXPLODE,
						LocUtils.addRand(b.getLocation(), 10, 10, 10), 0, 1, 10);
			}
			Bukkit.broadcastMessage("§c§lTeam " + nt.id + "'s Monument has been broken by " + p.getName() + " [Team "
					+ des.id + "]!");
			b.getWorld().playSound(b.getLocation(), Sound.ENDERDRAGON_DEATH, 1000, 2);

			monu.remove(b);

			for (Player tm : nt.getOnlinePlayers()) {
				tm.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, Integer.MAX_VALUE, 0));
			}

			UltraHC.nts.removeTeam(nt);
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Location b = event.getBlock().getLocation();

		for (Block m : monu.keySet()) {
			Location ml = m.getLocation();

			if (ml.getBlockX() == b.getBlockX() && ml.getBlockZ() == b.getBlockZ() && ml.getBlockY() <= b.getBlockY()) {
				event.setCancelled(true);
			}
		}
	}
}
