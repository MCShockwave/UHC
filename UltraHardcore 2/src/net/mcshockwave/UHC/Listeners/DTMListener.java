package net.mcshockwave.UHC.Listeners;

import net.mcshockwave.UHC.DefaultListener;
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
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map.Entry;

public class DTMListener implements Listener {

	public static HashMap<Block, Integer>	monum	= new HashMap<>();

	public static BukkitTask				part	= null;

	public static Block getFromId(int id) {
		for (Entry<Block, Integer> ent : monum.entrySet()) {
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
				monuLoc.setY(Option.Base_Height.getInt());

				Block m = monuLoc.getBlock();
				m.setType(Material.ENDER_STONE);

				monum.put(m, nt.id);
			} else {
				UltraHC.nts.removeTeam(nt);
			}
		}

		part = Bukkit.getScheduler().runTaskTimer(UltraHC.ins, new Runnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				for (Block b : monum.keySet()) {
					PacketUtils.playParticleEffect(ParticleEffect.ENCHANTMENT_TABLE,
							b.getLocation().add(0.5, 0.5, 0.5), 0.3f, 0.3f, 20);

					for (int y = Option.Base_Height.getInt() + 1; y < 256; y++) {
						Location l = b.getLocation();
						Block bl = b.getWorld().getBlockAt(l.getBlockX(), y, l.getBlockZ());
						if (bl.getType() != Material.AIR) {
							bl.setTypeId(0, false);
						}
					}
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

		if (!UltraHC.specs.contains(p.getName()) && monum.containsKey(b)) {
			NumberTeam nt = UltraHC.nts.getFromId(monum.get(b));
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
			Bukkit.broadcastMessage("§c§lTeam " + nt.id + "'s Monument has been broken by " + p.getName()
					+ (des == null ? "" : " [Team " + des.id + "]") + "!");
			b.getWorld().playSound(b.getLocation(), Sound.ENDERDRAGON_DEATH, 1000, 2);

			monum.remove(b);

			if (Option.Wither_on_Break.getBoolean()) {
				for (Player tm : nt.getOnlinePlayers()) {
					tm.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, Integer.MAX_VALUE, 1));
				}
			}

			for (String tm : nt.getPlayers()) {
				DefaultListener.livesRemaining.remove(tm);
				DefaultListener.livesRemaining.put(tm, -1);
			}
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Location b = event.getBlock().getLocation();

		for (Block m : monum.keySet()) {
			Location ml = m.getLocation();

			if (ml.getBlockX() == b.getBlockX() && ml.getBlockZ() == b.getBlockZ() && ml.getBlockY() <= b.getBlockY()) {
				event.setCancelled(true);
			}
		}

		if (b.getBlockY() > (Option.Base_Height.getInt() + 40)) {
			event.setCancelled(true);
			event.getPlayer().sendMessage("§cDo not build that high!");
		}
	}

	@EventHandler
	public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
		final Player p = event.getPlayer();
		final ItemStack it = event.getItem();
		if (it.getType() == Material.MILK_BUCKET && p.hasPotionEffect(PotionEffectType.WITHER)) {
			p.damage(p.getMaxHealth());
		}
	}

	@EventHandler
	public void onPlayerRegainHealth(EntityRegainHealthEvent event) {
		Entity e = event.getEntity();
		if (e instanceof Player) {
			Player p = (Player) e;
			if (event.getRegainReason() == RegainReason.SATIATED && UltraHC.nts.getTeam(p.getName()) != null
					&& !monum.containsValue(UltraHC.nts.getTeam(p.getName()).id)) {
				event.setCancelled(true);
			}
			UltraHC.updateHealthFor(p);
		}
	}
}
