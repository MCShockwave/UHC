package net.mcshockwave.UHC;

import net.mcshockwave.UHC.Menu.ItemMenu;
import net.mcshockwave.UHC.Menu.ItemMenu.Button;
import net.mcshockwave.UHC.Menu.ItemMenu.ButtonRunnable;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;

public class NumberedTeamSystem {

	public boolean					friendlyfire	= false;

	public Scoreboard				s;

	public ArrayList<NumberTeam>	teams			= new ArrayList<>();

	public BukkitTask				updater			= null;

	public NumberedTeamSystem(Scoreboard s) {
		this.s = s;

		updater = Bukkit.getScheduler().runTaskTimer(UltraHC.ins, new Runnable() {
			public void run() {
				updateScoreboard();
			}
		}, 1l, 1l);
	}

	public void updateScoreboard() {
		for (NumberTeam nt : teams) {
			for (Objective o : s.getObjectives()) {
				Objective nto = nt.sc.getObjective(o.getName());
				if (nto == null) {
					nto = nt.sc.registerNewObjective(o.getName(), o.getCriteria());
				}
				cloneObjective(nto, o);
			}

			cloneScores(nt.sc, s);

			for (NumberTeam nt2 : teams) {
				Team ntt = nt.sc.getTeam("T" + nt2.id);
				if (ntt == null) {
					ntt = nt.sc.registerNewTeam("T" + nt2.id);
					ntt.setCanSeeFriendlyInvisibles(true);
					ntt.setDisplayName("Team " + nt2.id);
					ntt.setPrefix((nt2 == nt ? "§a" : "§c") + "[" + nt2.id + "]§f");
					if (nt2 == nt) {
						nt.t = ntt;
					}
				}
				ntt.setAllowFriendlyFire(friendlyfire);
			}

			updatePlayersForTeam(nt);
		}
	}

	private void cloneObjective(Objective n, Objective cl) {
		n.setDisplayName(cl.getDisplayName());
		n.setDisplaySlot(cl.getDisplaySlot());
	}

	@SuppressWarnings("deprecation")
	private void updatePlayersForTeam(NumberTeam tou) {
		for (NumberTeam nt : teams) {
			Team t = tou.sc.getTeam("T" + nt.id);

			for (String s : nt.players) {
				OfflinePlayer op = Bukkit.getOfflinePlayer(s);
				if (!t.hasPlayer(op)) {
					t.addPlayer(op);
				}
			}
		}
	}

	private void cloneScores(Scoreboard to, Scoreboard from) {
		for (OfflinePlayer op : from.getPlayers()) {
			for (Score f : from.getScores(op)) {
				if (to.getObjective(f.getObjective().getName()) != null) {
					Objective t = to.getObjective(f.getObjective().getName());
					
					if (t.getScore(op).getScore() != f.getScore()) {
						t.getScore(op).setScore(f.getScore());
					}
				}
			}
		}
	}

	public ItemMenu getMenu(Player p, boolean edit) {
		ItemMenu m = new ItemMenu("Teams", teams.size());

		int teamlimit = Option.Team_Limit.getInt();

		for (NumberTeam nt : teams) {
			int data = getTeam(p.getName()) == nt ? 4 : nt.players.size() >= teamlimit ? 14 : 5;

			Button b = new Button(false, Material.WOOL, nt.id, data, "Team #" + nt.id, "", "§eOwner: " + nt.owner,
					"Players: " + nt.players.size() + " / " + teamlimit, "§aOnline Players: "
							+ nt.getOnlinePlayers().size());
			m.addButton(b, nt.id - 1);
			if (edit) {
				m.addSubMenu(nt.getSubMenu(), b, true);
			}
		}

		return m;
	}

	public NumberTeam getTeam(String s) {
		for (NumberTeam nt : teams) {
			if (nt.players.contains(s)) {
				return nt;
			}
		}
		return null;
	}

	public boolean isOnTeam(String name, int id) {
		return getTeam(name) == getFromId(id);
	}

	public NumberTeam createTeam(String pass, String owner) {
		return new NumberTeam(pass, owner);
	}

	public NumberTeam getFromId(int id) {
		for (NumberTeam nt : teams) {
			if (nt.id == id) {
				return nt;
			}
		}
		return null;
	}

	public class NumberTeam {
		public int			id;
		public String		password, owner;
		ArrayList<String>	players;
		public Scoreboard	sc;
		public Team			t;

		NumberTeam(String pass, String owner) {
			id = getValidId();
			password = pass;
			this.owner = owner;
			players = new ArrayList<>();

			teams.add(this);

			sc = Bukkit.getScoreboardManager().getNewScoreboard();
		}

		public void addPlayer(String name) {
			for (NumberTeam nt : teams) {
				nt.removePlayer(name);
			}

			players.add(name);

			if (Bukkit.getPlayer(name) != null) {
				Bukkit.getPlayer(name).setScoreboard(sc);
			}
		}

		public void removePlayer(String name) {
			players.remove(name);

			if (Bukkit.getPlayer(name) != null) {
				Bukkit.getPlayer(name).setScoreboard(s);
			}
		}

		public ArrayList<Player> getOnlinePlayers() {
			ArrayList<Player> ret = new ArrayList<>();

			for (String s : players) {
				if (Bukkit.getPlayer(s) != null) {
					ret.add(Bukkit.getPlayer(s));
				}
			}

			return ret;
		}

		public ItemMenu getSubMenu() {
			ItemMenu m = new ItemMenu("Team " + id + " - Editing", 9);

			Button delete = new Button(false, Material.WOOL, 1, 14, "Delete Team", "", "§cWARNING: CAN NOT BE UNDONE");
			m.addButton(delete, 0);
			final NumberTeam team = this;
			delete.setOnClick(new ButtonRunnable() {
				public void run(final Player p, InventoryClickEvent event) {
					teams.remove(team);
					p.sendMessage("§cDeleted team #" + team.id);

					p.closeInventory();
					Bukkit.getScheduler().runTaskLater(UltraHC.ins, new Runnable() {
						public void run() {
							getMenu(p, true).open(p);
						}
					}, 1l);
				}
			});

			return m;
		}

		public ArrayList<String> getPlayers() {
			return players;
		}
		
		public String[] getPlayersArray() {
			return players.toArray(new String[0]);
		}
	}

	public int getValidId() {
		int check = 0;
		while (check <= 64) {
			check++;

			boolean found = true;
			for (NumberTeam nt : teams) {
				if (nt.id == check) {
					found = false;
				}
			}

			if (found) {
				break;
			}
		}

		return check;
	}

	public boolean isTeamGame() {
		return teams.size() > 0;
	}

}
