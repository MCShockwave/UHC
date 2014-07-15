package net.mcshockwave.UHC.Utils;

import net.mcshockwave.MCS.MCShockwave;
import net.mcshockwave.UHC.UltraHC;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import com.comphenix.packetwrapper.WrapperPlayServerAnimation;
import com.comphenix.packetwrapper.WrapperPlayServerBed;
import com.comphenix.packetwrapper.WrapperPlayServerEntityDestroy;
import com.comphenix.packetwrapper.WrapperPlayServerEntityTeleport;
import com.comphenix.packetwrapper.WrapperPlayServerNamedEntitySpawn;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

public class FakePlayer {

	public static HashMap<Short, FakePlayer>	fakePlayers	= new HashMap<>();

	public short								id;
	public String								name;
	public Location								loc;

	public ArrayList<String>					canSee		= new ArrayList<>();

	Inventory									inv;

	public boolean								exists		= false;

	FakePlayer(short id, String name, Location loc) {
		this.id = id;
		this.name = name;
		this.loc = loc;
	}

	public static final int	MAX_RADIUS	= 50;

	public static void init() {
		new BukkitRunnable() {
			public void run() {
				for (FakePlayer fp : fakePlayers.values()) {
					for (Player p : fp.loc.getWorld().getPlayers()) {
						if (fp.loc.distanceSquared(p.getLocation()) < MAX_RADIUS * MAX_RADIUS) {
							if (!fp.canSee.contains(p.getName())) {
								fp.showTo(p);
								fp.canSee.add(p.getName());
							}
						} else {
							if (fp.canSee.contains(p.getName())) {
								fp.hideFrom(p);
								fp.canSee.remove(p.getName());
							}
						}
					}
				}
			}
		}.runTaskTimer(UltraHC.ins, 100, 100);
	}

	public void setInventory(PlayerInventory inv, boolean withHead) {
		this.inv = Bukkit.createInventory(null, 45, name + (name.endsWith("s") ? "'" : "'s") + " Inventory");

		for (int i = 0; i < 9; i++) {
			this.inv.setItem(i + 36, inv.getItem(i));
		}
		for (int i = 9; i < 36; i++) {
			this.inv.setItem(i, inv.getItem(i));
		}
		for (int i = 0; i < 4; i++) {
			this.inv.setItem(3 - i, inv.getArmorContents()[i]);
		}

		if (withHead) {
			this.inv.setItem(8, ItemMetaUtils.setHeadName(new ItemStack(Material.SKULL_ITEM, 1, (short) 3), name));
		}
	}

	public Inventory getInventory() {
		return inv;
	}

	public static FakePlayer spawnNew(Location loc, String name) {
		short id = getEntityId();

		FakePlayer fp = new FakePlayer(id, name, loc);
		fp.exists = true;
		fakePlayers.put(id, fp);

		for (Player p : loc.getWorld().getPlayers()) {
			fp.showTo(p);
		}

		return fp;
	}

	public void showTo(Player p) {
		WrapperPlayServerNamedEntitySpawn spawn = new WrapperPlayServerNamedEntitySpawn();
		spawn.setEntityID(id);
		spawn.setPosition(loc.toVector());
		spawn.setPlayerName(name);
		spawn.setPlayerUUID(UUID.randomUUID().toString());

		spawn.setYaw(0);
		spawn.setPitch(0);

		WrappedDataWatcher watcher = new WrappedDataWatcher();
		watcher.setObject(0, (byte) 0);

		spawn.setMetadata(watcher);

		WrapperPlayServerBed bed = new WrapperPlayServerBed();
		bed.setEntityId(id);
		bed.setX(loc.getBlockX());
		bed.setY((byte) loc.getBlockY());
		bed.setZ(loc.getBlockZ());

		WrapperPlayServerEntityTeleport tele = new WrapperPlayServerEntityTeleport();
		tele.setEntityID(id);
		tele.setX(loc.getX());
		tele.setY(loc.getY() + 0.4);
		tele.setZ(loc.getZ());

		spawn.sendPacket(p);
		bed.sendPacket(p);
		tele.sendPacket(p);
	}

	public void hideFrom(Player p) {
		WrapperPlayServerEntityDestroy des = new WrapperPlayServerEntityDestroy();
		des.setEntities(new int[] { id });
		des.sendPacket(p);
	}

	public void startAnimation(int times, boolean destroy) {
		long delay = 0;
		for (int i = 0; i < times; i++) {
			Bukkit.getScheduler().runTaskLater(MCShockwave.instance, new Runnable() {
				public void run() {
					if (!exists) {
						return;
					}

					WrapperPlayServerAnimation ani = new WrapperPlayServerAnimation();
					ani.setEntityID(id);
					ani.setAnimation(0);

					WrapperPlayServerAnimation ani2 = new WrapperPlayServerAnimation();
					ani2.setEntityID(id);
					ani2.setAnimation(1);

					PacketUtils.playBlockDustParticles(Material.REDSTONE_BLOCK, 0, loc.clone().add(-0.5, -0.2, -0.5),
							0, 0.1f);

					loc.getWorld().playSound(loc, Sound.HURT_FLESH, 3, 0);

					for (Player p : loc.getWorld().getPlayers()) {
						ani.sendPacket(p);
						ani2.sendPacket(p);
					}
				}
			}, delay += rand.nextInt(15) + 5);
		}
		if (destroy && exists) {
			Bukkit.getScheduler().runTaskLater(MCShockwave.instance, new Runnable() {
				public void run() {
					destroy();
				}
			}, delay += 20);
		}
	}

	public void destroy() {
		WrapperPlayServerEntityDestroy des = new WrapperPlayServerEntityDestroy();
		des.setEntities(new int[] { id });
		for (Player p : loc.getWorld().getPlayers()) {
			des.sendPacket(p);
		}
		fakePlayers.remove(id);
		exists = false;
	}

	static Random	rand	= new Random();

	public static short getEntityId() {
		return (short) rand.nextInt(Short.MAX_VALUE);
	}

}
