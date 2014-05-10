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

	public static String getBanReason(String by, String reason, int games) {
		if (games == -1) {
			return String.format("§aBanned by %s: §f%s §b[Permanent]", by, reason);
		}
		return String.format("§aBanned by %s: §f%s §b[%s games left]", by, reason, games);
	}

	public static String getBanReason(String name) {
		Ban b = getBanFor(name);

		return getBanReason(b.bannedBy, b.reason, b.games);
	}

	public static void setBanned(String name, int games, String reason, String by) {
		if (getBanFor(name) != null) {
			unBan(name);
		}

		Ban ban = new Ban(name, games, by, reason, true);
		ArrayList<String> bans = getBansString();
		bans.add(ban.getString());
		ConfigFile.Bans.get().set("bans", bans);
		ConfigFile.Bans.update();

		if (Bukkit.getPlayer(name) != null) {
			Bukkit.getPlayer(name).kickPlayer(getBanReason(by, reason, games));
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
			list.remove(getBanFor(unban).getString());
		}

		ConfigFile.Bans.update();
	}

	public static void updateBan(Ban b) {
		ArrayList<String> bs = getBansString();

		for (int i = 0; i < bs.size(); i++) {
			String ban = bs.get(i);
			if (ban.equalsIgnoreCase(b.getString())) {
				bs.remove(ban);
			}
		}

		ConfigFile.Bans.get().set("bans", bs);
		ConfigFile.Bans.update();
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

			Ban ba = new Ban(i, bs[0], Integer.parseInt(bs[1]), bs[2], bs[3], Integer.parseInt(bs[4]) == 1);

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

		public String getString() {
			return String.format("%s;%s;%s;%s;%s", name, games, bannedBy, reason, recent ? 1 : 0);
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
