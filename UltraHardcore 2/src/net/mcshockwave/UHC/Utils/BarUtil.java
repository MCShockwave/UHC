package net.mcshockwave.UHC.Utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

public class BarUtil {

	public static final ProtocolManager	protMan			= ProtocolLibrary.getProtocolManager();
	public static final PacketContainer	m_spawnPacket	= protMan
																.createPacket(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
	public static final PacketContainer	m_destroyPacket	= protMan.createPacket(PacketType.Play.Server.ENTITY_DESTROY);

	public static final int				entID			= Short.MAX_VALUE - 375;

	public static void enable() {
		m_destroyPacket.getIntegerArrays().write(0, new int[] { entID });
	}

	public static void destroyTimer() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			try {
				ProtocolLibrary.getProtocolManager().sendServerPacket(p, m_destroyPacket);
			} catch (InvocationTargetException ignored) {
			}
		}
	}

	@SuppressWarnings("deprecation")
	public static void displayTextBar(String text, float percent) {
		destroyTimer();
		for (Player p : Bukkit.getOnlinePlayers()) {
			try {
				PacketContainer pc = m_spawnPacket.deepClone();
				pc.getIntegers().write(0, entID).write(1, (int) EntityType.ENDER_DRAGON.getTypeId())
						.write(2, (int) p.getLocation().getX() * 32).write(3, -200 * 32)
						.write(4, (int) p.getLocation().getZ() * 32);
				WrappedDataWatcher watcher = pc.getDataWatcherModifier().read(0);
				watcher.setObject(0, (byte) 0x20);
				watcher.setObject(6, percent * 2);
				watcher.setObject(10, text.substring(0, Math.min(text.length(), 64)));
				protMan.sendServerPacket(p, pc);
			} catch (InvocationTargetException ignored) {
			}
		}
	}

}
