package net.mcshockwave.UHC.Utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

public class BarUtil {

	public static final PacketContainer	m_spawnPacket	= ProtocolLibrary.getProtocolManager().createPacket(
																PacketType.Play.Server.SPAWN_ENTITY_LIVING);
	public static final PacketContainer	m_destroyPacket	= ProtocolLibrary.getProtocolManager().createPacket(
																PacketType.Play.Server.ENTITY_DESTROY);

	public static void destroyTimer() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			try {
				ProtocolLibrary.getProtocolManager().sendServerPacket(p, m_destroyPacket);
			} catch (InvocationTargetException ignored) {
			}
		}
	}

	@SuppressWarnings("deprecation")
	public static void displayTextBar(String text, float health) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			try {
				destroyTimer();
				PacketContainer pc = m_spawnPacket.deepClone();
				pc.getIntegers().write(0, Short.MAX_VALUE - 375).write(1, (int) EntityType.ENDER_DRAGON.getTypeId())
						.write(2, (int) player.getLocation().getX() * 32) // x
						.write(3, -200 * 32) // y
						.write(4, (int) player.getLocation().getZ() * 32); // z
				WrappedDataWatcher watcher = pc.getDataWatcherModifier().read(0);
				watcher.setObject(0, (byte) 0x20); // invisible
				watcher.setObject(6, health); // health
				watcher.setObject(10, text.substring(0, Math.min(text.length(), 64)));
				ProtocolLibrary.getProtocolManager().sendServerPacket(player, pc);
			} catch (InvocationTargetException ignored) {
			}
		}
	}

}
