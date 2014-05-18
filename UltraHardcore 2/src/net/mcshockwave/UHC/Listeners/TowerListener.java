package net.mcshockwave.UHC.Listeners;

import net.mcshockwave.UHC.UltraHC;
import net.mcshockwave.UHC.Utils.CustomEntityRegistrar;
import net.mcshockwave.UHC.Utils.LocUtils;
import net.mcshockwave.UHC.Utils.PacketUtils;
import net.mcshockwave.UHC.Utils.PacketUtils.ParticleEffect;
import net.mcshockwave.UHC.worlds.Multiworld;
import net.minecraft.server.v1_7_R2.EntityLiving;
import net.minecraft.server.v1_7_R2.EntitySkeleton;
import net.minecraft.server.v1_7_R2.EntityZombie;
import net.minecraft.server.v1_7_R2.GenericAttributes;
import net.minecraft.server.v1_7_R2.Items;
import net.minecraft.server.v1_7_R2.World;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R2.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Random;

public class TowerListener implements Listener {

	public static BukkitTask											spawn	= null, particles = null;
	public static Random												rand	= new Random();

	public static HashMap<EntityLiving, Class<? extends EntityLiving>>	spawned	= new HashMap<>();

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player p = event.getPlayer();
		Block b = event.getBlock();

		if (b.getType() == Material.BEACON) {
			event.setCancelled(true);
			b.setType(Material.AIR);
			b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, Material.BEACON);

			p.getWorld().playSound(p.getLocation(), Sound.ENDERDRAGON_DEATH, 1000, 0.7f);

			Bukkit.broadcastMessage("§e§l" + p.getName() + " §ehas broke the beacon!");
		}
	}

	public static void start() {
		CustomEntityRegistrar.addCustomEntity("Zombie", EntityType.ZOMBIE, EntityZombie.class, CustomZombie.class);
		CustomEntityRegistrar.addCustomEntity("Skeleton", EntityType.SKELETON, EntitySkeleton.class,
				CustomSkeleton.class);

		particles = new BukkitRunnable() {
			public void run() {
				for (Entity e : Multiworld.getUHC().getEntities()) {
					net.minecraft.server.v1_7_R2.Entity nmsE = ((CraftEntity) e).getHandle();
					if (spawned.containsKey(nmsE)) {
						if (spawned.get(nmsE) == CustomZombie.class) {
							PacketUtils.playParticleEffect(ParticleEffect.FLAME, e.getLocation(), 0.2f, 0.05f, 5);
						}
						if (spawned.get(nmsE) == CustomSkeleton.class) {
							PacketUtils.playParticleEffect(ParticleEffect.MOB_SPELL, e.getLocation(), 0.3f, 1, 10);
						}
					}
				}
			}
		}.runTaskTimer(UltraHC.ins, 2, 2);

		spawn = new BukkitRunnable() {
			public void run() {
				Location loc = LocUtils.addRand(new Location(Multiworld.getUHC(), 0, 60, 0), 50, 25, 50);

				if (isValidSpawn(loc)) {
					EntityLiving el = (EntityLiving) CustomEntityRegistrar.spawnCustomEntity(
							rand.nextBoolean() ? CustomSkeleton.class : CustomZombie.class,
							isAboveBedrock(loc.getBlock(), 5).getRelative(0, 1, 0).getLocation());
					if (el instanceof EntitySkeleton) {
						el.setEquipment(0, new net.minecraft.server.v1_7_R2.ItemStack(Items.BOW));
						spawned.put(el, CustomSkeleton.class);
					}
					if (el instanceof EntityZombie) {
						el.setEquipment(0, new net.minecraft.server.v1_7_R2.ItemStack(Items.GOLD_SWORD));
						spawned.put(el, CustomZombie.class);
					}
				}
			}
		}.runTaskTimer(UltraHC.ins, 4, 4);
	}

	public static boolean isValidSpawn(Location spawn) {
		if (spawn.getChunk().isLoaded()) {
			if (spawn.getBlock().getLightLevel() > 13) {
				return false;
			}

			Block b = spawn.getBlock();
			if (b.getType() == Material.AIR && isAboveBedrock(b, 5) != null) {
				return true;
			}
		}
		return false;
	}

	public static Block isAboveBedrock(Block b, int blocks) {
		for (int i = 0; i < blocks; i++) {
			if (b.getRelative(0, -i, 0).getType() != Material.AIR) {
				if (b.getRelative(0, -i, 0).getType() == Material.BEDROCK) {
					return b.getRelative(0, -i, 0);
				} else
					return null;
			}
		}
		return null;
	}

	public static void stop() {
		spawn.cancel();
		particles.cancel();
	}

	public static class CustomZombie extends EntityZombie {

		public CustomZombie(World world) {
			super(world);

			this.getAttributeInstance(GenericAttributes.a).setValue(30);
			this.getAttributeInstance(GenericAttributes.c).setValue(1);
			this.getAttributeInstance(GenericAttributes.d).setValue(0.4);

		}

	}

	public static class CustomSkeleton extends EntitySkeleton {

		public CustomSkeleton(World world) {
			super(world);

			this.getAttributeInstance(GenericAttributes.a).setValue(30);
		}

		@Override
		public void a(EntityLiving entityliving, float f) {
			for (int i = 0; i < 3; i++) {
				super.a(entityliving, f);
			}
		}
	}

}
