package net.mcshockwave.UHC;

import org.bukkit.Bukkit;

public class BanManager {

	private static SQLTable	bt	= SQLTable.Bans;

	public static boolean isBanned(String name) {
		if (bt.has("Username", name)) {
			int games = bt.getInt("Username", name, "Games");

			if (games == -1 || games > 0) {
				return true;
			}
		}
		return false;
	}
	
	public static String getBanReason(String by, String reason, int games) {
		if (games == -1) {
			return String.format("§aBanned by %s: §f%s §b[Permanent]", by, reason);
		}
		return String.format("§aBanned by %s: §f%s §b[%s games left]", by, reason, games);
	}

	public static String getBanReason(String name) {
		String by = bt.get("Username", name, "BannedBy");
		String reason = bt.get("Username", name, "Reason");
		int games = bt.getInt("Username", name, "Games");

		return getBanReason(by, reason, games);
	}

	public static void setBanned(String name, int games, String reason, String by) {
		if (bt.has("Username", name)) {
			bt.del("Username", name);
		}

		bt.add("Username", name, "Games", "" + games, "Reason", reason, "BannedBy", by);
		
		if (Bukkit.getPlayer(name) != null) {
			Bukkit.getPlayer(name).kickPlayer(getBanReason(by, reason, games));
		}
	}

	public static void incrGames(int games) {
		for (String s : bt.getAll("Username")) {
			int gs = bt.getInt("Username", s, "Games");

			bt.set("Games", "" + (gs + games), "Username", s);
		}
	}

}
