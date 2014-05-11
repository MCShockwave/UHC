package net.mcshockwave.UHC;

import net.mcshockwave.UHC.db.ConfigFile;

import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class BanManager {

	public static boolean isBanned(String name) {
		if (getBanFor(name) != null) {
			Ban b = getBanFor(name);
			if (b.games == 0) {
				return false;
			}
			return true;
		}
		return false;
	}

	public static String getBanReason(String by, String reason, int games, boolean recent) {
		if (games == -1) {
			return String.format("§aBanned by %s: §f%s §b[Permanent]", by, reason);
		}
		return String.format("§aBanned by %s: §f%s §b[%s games left" + (recent ? " after current" : "") + "]", by,
				reason, games);
	}

	public static String getBanReason(String name) {
		Ban b = getBanFor(name);

		return getBanReason(b.bannedBy, b.reason, b.games, b.recent);
	}

	public static void setBanned(String name, int games, String reason, String by, boolean recent) {
		setBanned(getBans().length, name, games, reason, by, recent);
	}

	public static void setBanned(int id, String name, int games, String reason, String by, boolean recent) {
		if (getBanFor(name) != null) {
			unBan(name);
		}

		Ban ban = new Ban(id, name, games, by, reason, recent);
		ArrayList<String> bans = getBansString();
		bans.add(ban.toString());
		ConfigFile.Bans.get().set("bans", bans);
		ConfigFile.Bans.update();

		if (Bukkit.getPlayer(name) != null) {
			Bukkit.getPlayer(name).kickPlayer(getBanReason(by, reason, games, recent));
		}
	}

	public static ArrayList<String> incrGames(int games) {
		ArrayList<String> ret = new ArrayList<>();
		for (Ban b : getBans()) {
			if (b.recent) {
				b.recent = false;
				updateBan(b);
				continue;
			}

			int gs = b.games;
			if (gs == -1) {
				continue;
			}

			gs += games;

			if (gs == 0) {
				unBan(b.name);
				ret.add(b.name);
			} else {
				b.games = gs;
				updateBan(b);
			}
		}
		return ret;
	}

	public static void unBan(String unban) {
		List<String> list = ConfigFile.Bans.get().getStringList("bans");

		if (getBanFor(unban) != null) {
			list.remove(getBanFor(unban).toString());
		}

		ConfigFile.Bans.get().set("bans", list);
		ConfigFile.Bans.update();
	}

	public static void updateBan(Ban b) {
		int id = b.id;
		unBan(getBanFromId(id).name);

		setBanned(b.id, b.name, b.games, b.reason, b.bannedBy, b.recent);
	}

	public static ArrayList<String> getBansString() {
		return new ArrayList<>(ConfigFile.Bans.get().getStringList("bans"));
	}

	public static Ban[] getBans() {
		ArrayList<String> bans = getBansString();
		ArrayList<Ban> ret = new ArrayList<>();

		for (int i = 0; i < bans.size(); i++) {
			String b = bans.get(i);
			String[] bs = b.split(";");

			Ban ba = new Ban(Integer.parseInt(bs[0]), bs[1], Integer.parseInt(bs[2]), bs[3], bs[4],
					Integer.parseInt(bs[5]) == 1);

			ret.add(ba);
		}

		return ret.toArray(new Ban[0]);
	}

	public static class Ban {
		public String	name, bannedBy, reason;
		public int		games;
		public boolean	recent;
		public int		id;

		public Ban(int id, String name, int games, String bannedBy, String reason, boolean recent) {
			this.id = id;
			this.name = name;
			this.games = games;
			this.bannedBy = bannedBy;
			this.reason = reason;
			this.recent = recent;
		}

		public Ban(String name, int games, String bannedBy, String reason, boolean recent) {
			this.id = getBans().length;
			this.name = name;
			this.games = games;
			this.bannedBy = bannedBy;
			this.reason = reason;
			this.recent = recent;
		}

		@Override
		public String toString() {
			return String.format("%s;%s;%s;%s;%s;%s", id, name, games, bannedBy, reason, recent ? 1 : 0);
		}
	}

	public static Ban getBanFor(String name) {
		for (Ban b : getBans()) {
			if (b.name.equalsIgnoreCase(name)) {
				return b;
			}
		}
		return null;
	}

	public static Ban getBanFromId(int id) {
		for (Ban b : getBans()) {
			if (b.id == id) {
				return b;
			}
		}
		return null;
	}

}
