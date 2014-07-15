package net.mcshockwave.UHC.Utils;

import net.mcshockwave.MCS.Entities.CustomEntityRegistrar;
import net.minecraft.server.v1_7_R2.Entity;
import net.minecraft.server.v1_7_R2.EntityAmbient;
import net.minecraft.server.v1_7_R2.EntityBat;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R2.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class NametagUtils {

	private static Map<String, NametagEntity>	entities	= new HashMap<>();

	public static void init() {
		CustomEntityRegistrar.addCustomEntity("NametagHider", EntityType.BAT, EntityBat.class, NametagEntity.class);

		Iterator<org.bukkit.entity.Entity> localIterator2;
		for (Iterator<World> localIterator1 = Bukkit.getWorlds().iterator(); localIterator1.hasNext(); localIterator2
				.hasNext()) {
			World world = (World) localIterator1.next();
			localIterator2 = world.getEntities().iterator();
			org.bukkit.entity.Entity entity = (org.bukkit.entity.Entity) localIterator2.next();
			if ((entity instanceof NametagEntity)) {
				entity.remove();
			}
		}
	}

	public static void disable() {
		for (NametagEntity entity : entities.values()) {
			entity.die();
		}
		entities.clear();
	}

	public static void hideNametag(Player player) {
		initPlayer(player);
	}

	public static void showNametag(Player player) {
		String playerName = player.getName();
		if (entities.containsKey(playerName)) {
			((NametagEntity) entities.get(playerName)).die();
			entities.remove(playerName);
		}
	}

	public static boolean isNametagHidden(Player player) {
		String playerName = player.getName();
		return entities.containsKey(playerName);
	}

	private static void initPlayer(Player player) {
		String playerName = player.getName();
		if (!entities.containsKey(playerName)) {
			NametagEntity entity = new NametagEntity(player);
			entities.put(playerName, entity);
		} else {
			NametagEntity entity = new NametagEntity(player);
			entity.hideTag(player);
		}
	}

	private static class NametagEntity extends EntityAmbient {
		public NametagEntity(Player player) {
			super(((CraftWorld) player.getWorld()).getHandle());

			Location location = player.getLocation();

			setInvisible(true);
			setPosition(location.getX(), location.getY(), location.getZ());
			try {
				Field invulnerable = Entity.class.getDeclaredField("invulnerable");
				invulnerable.setAccessible(true);
				invulnerable.setBoolean(this, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
			this.world.addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);

			this.persistent = true;

			hideTag(player);
		}

		public void hideTag(Player player) {
			setPassengerOf(((CraftPlayer) player).getHandle());
		}

		// public void showTag() {
		// setPassengerOf(null);
		// }

		public void h() {
			this.motX = this.motY = this.motZ = 0.0D;
			a(0.0F, 0.0F);
			a(0.0F, 0.0F, 0.0F);
		}

		public void o(Entity entity) {
		}
	}

}
