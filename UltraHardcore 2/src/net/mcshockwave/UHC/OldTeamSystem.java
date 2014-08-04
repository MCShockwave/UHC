package net.mcshockwave.UHC;

import net.mcshockwave.UHC.Menu.ItemMenu;
import net.mcshockwave.UHC.Menu.ItemMenu.Button;
import net.mcshockwave.UHC.Menu.ItemMenu.ButtonRunnable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;

import org.apache.commons.lang.WordUtils;

public class OldTeamSystem {

	public Scoreboard					s;
	public HashMap<ChatColor, Team>		teams;
	public HashMap<ChatColor, Score>	scores;

	public static final ChatColor[]		colors		= { ChatColor.WHITE, ChatColor.GOLD, ChatColor.LIGHT_PURPLE,
			ChatColor.AQUA, ChatColor.YELLOW, ChatColor.GREEN, ChatColor.DARK_GRAY, ChatColor.GRAY,
			ChatColor.DARK_AQUA, ChatColor.DARK_PURPLE, ChatColor.BLUE, ChatColor.DARK_GREEN, ChatColor.RED };
	public static final short[]			woolData	= { 0, 1, 2, 3, 4, 5, 7, 8, 9, 10, 11, 13, 14 };

	public OldTeamSystem(Scoreboard s) {
		this.s = s;
		this.teams = new HashMap<>();
		this.scores = new HashMap<>();
	}

	public boolean isTeamGame() {
		for (ChatColor c : colors) {
			if (isEnabled(c)) {
				return true;
			}
		}
		return false;
	}

	public ItemMenu getMenu() {
		ItemMenu m = new ItemMenu("Team System", colors.length + (9 - (colors.length % 9)));

		for (int i = 0; i < colors.length; i++) {
			final ChatColor c = colors[i];
			final short d = woolData[i];

			Button b = new Button(false, Material.WOOL, 1, d, getFullName(c), "Click to edit", "", "Currently: "
					+ (isEnabled(c) ? "§a§oEnabled" : "§c§oDisabled"));
			m.addButton(b, i);
			b.setOnClick(new ButtonRunnable() {
				public void run(final Player p, InventoryClickEvent event) {
					p.closeInventory();
					Bukkit.getScheduler().runTask(UltraHC.ins, new Runnable() {
						public void run() {
							getMenuFor(c).open(p);
						}
					});
				}
			});
		}

		Button b = new Button(true, Material.DIAMOND, 1, 0, "Spread Players", "Click to randomize teams");
		m.addButton(b, m.i.getSize() - 1);
		b.setOnClick(new ButtonRunnable() {
			public void run(Player p, InventoryClickEvent event) {
				// TeamSystem.spreadPlayers();
			}
		});

		return m;
	}

	HashMap<ChatColor, BukkitTask>	liTa	= new HashMap<>();

	// @SuppressWarnings("deprecation")
	public void setScores() {
		for (ChatColor c : colors) {
			if (isEnabled(c)) {
				if (scores.containsKey(c) && scores.get(c) != null) {
					scores.get(c).setScore(teams.get(c).getSize());
				} else {
					// final Score s =
					// UltraHC.stats.getScore(Bukkit.getOfflinePlayer(getSubName(c)));
					// scores.put(c, s);

					if (Scenarios.Linked.isEnabled()) {
						if (liTa.containsKey(c)) {
							liTa.get(c).cancel();
							liTa.remove(c);
						}

						final ChatColor c2 = c;
						liTa.put(c, Bukkit.getScheduler().runTaskTimer(UltraHC.ins, new Runnable() {
							public void run() {
								if (teams.get(c2).getPlayers().size() > 0) {
									for (OfflinePlayer op : teams.get(c2).getPlayers()) {
										if (op.isOnline()) {
											// s.setScore(UltraHC.health.getScore(op).getScore());
											break;
										}
									}
								}
							}
						}, 20, 20));
					}
				}
			}
		}
	}

	public ItemMenu getMenuFor(ChatColor c) {
		if (isEnabled(c)) {
			return getEnabledMenu(c);
		} else
			return getDisabledMenu(c);
	}

	public ItemMenu getDisabledMenu(final ChatColor c) {
		ItemMenu m = new ItemMenu(ChatColor.DARK_RED + getName(c) + " Team", 9);

		Button b = new Button(true, Material.WOOL, 1, 5, "Enable Team", "", "Click to enable");
		m.addButton(b, 4);
		b.setOnClick(new ButtonRunnable() {
			public void run(final Player p, InventoryClickEvent event) {
				p.sendMessage("§aEnabled team: " + getFullName(c));

				Team t = s.registerNewTeam(getName(c).replace(' ', '_'));
				t.setAllowFriendlyFire(false);
				t.setPrefix(c.toString());
				t.setSuffix(ChatColor.RESET.toString());
				teams.put(c, t);

				p.closeInventory();
				Bukkit.getScheduler().runTask(UltraHC.ins, new Runnable() {
					public void run() {
						getMenuFor(c).open(p);
					}
				});
			}
		});

		Button ba = new Button(false, Material.STICK, 1, 0, "Back", "Click to go back");
		m.addButton(ba, 8);
		ba.setOnClick(new ButtonRunnable() {
			public void run(final Player p, InventoryClickEvent event) {
				p.closeInventory();
				Bukkit.getScheduler().runTask(UltraHC.ins, new Runnable() {
					public void run() {
						getMenu().open(p);
					}
				});
			}
		});

		return m;
	}

	public ItemMenu getEnabledMenu(final ChatColor c) {
		ItemMenu m = new ItemMenu(ChatColor.DARK_GREEN + getName(c) + " Team", 9);

		Button b = new Button(true, Material.WOOL, 1, 14, "Disable Team", "", "Click to disable");
		m.addButton(b, 0);
		b.setOnClick(new ButtonRunnable() {
			public void run(final Player p, InventoryClickEvent event) {
				p.sendMessage("§cDisabled team: " + getFullName(c));

				s.getTeam(getName(c).replace(' ', '_')).unregister();
				teams.remove(c);

				p.closeInventory();
				Bukkit.getScheduler().runTask(UltraHC.ins, new Runnable() {
					public void run() {
						getMenuFor(c).open(p);
					}
				});
			}
		});

		Button add = new Button(false, Material.SKULL_ITEM, 1, 3, "Add Players...", "", "Click to open menu");
		m.addButton(add, 2);
		add.setOnClick(new ButtonRunnable() {
			public void run(final Player p, InventoryClickEvent event) {
				p.closeInventory();
				Bukkit.getScheduler().runTask(UltraHC.ins, new Runnable() {
					public void run() {
						getAddMenu(c).open(p);
					}
				});
			}
		});

		Button rem = new Button(false, Material.SKULL_ITEM, 1, 3, "Remove Players...", "", "Click to open menu");
		m.addButton(rem, 3);
		rem.setOnClick(new ButtonRunnable() {
			public void run(final Player p, InventoryClickEvent event) {
				p.closeInventory();
				Bukkit.getScheduler().runTask(UltraHC.ins, new Runnable() {
					public void run() {
						getRemoveMenu(c).open(p);
					}
				});
			}
		});

		Button ba = new Button(false, Material.STICK, 1, 0, "Back", "Click to go back");
		m.addButton(ba, 8);
		ba.setOnClick(new ButtonRunnable() {
			public void run(final Player p, InventoryClickEvent event) {
				p.closeInventory();
				Bukkit.getScheduler().runTask(UltraHC.ins, new Runnable() {
					public void run() {
						getMenu().open(p);
					}
				});
			}
		});

		return m;
	}

	public ItemMenu getAddMenu(final ChatColor c) {
		Player[] ps = Bukkit.getOnlinePlayers().toArray(new Player[0]);

		ItemMenu m = new ItemMenu("Add Players - " + getName(c), 54);

		for (int i = 0; i < ps.length; i++) {
			final Player p = ps[i];
			if (teams.get(c).hasPlayer(p))
				continue;
			Button pl = new Button(false, Material.SKULL_ITEM, 1, 3, getPrefix(p) + p.getName(), "",
					"Click to add player");
			m.addButton(pl, i);
			pl.setOnClick(new ButtonRunnable() {
				public void run(final Player p2, InventoryClickEvent event) {
					p2.sendMessage("§aAdded player " + p.getName() + " to team " + getName(c));

					if (p.getName().length() > DefaultListener.maxLength) {
						p.setPlayerListName(c + DefaultListener.getShortName(p));
					}
					teams.get(c).addPlayer(p);
				}
			});
		}

		Button ba = new Button(false, Material.STICK, 1, 0, "Back", "Click to go back");
		m.addButton(ba, m.i.getSize() - 1);
		ba.setOnClick(new ButtonRunnable() {
			public void run(final Player p, InventoryClickEvent event) {
				p.closeInventory();
				Bukkit.getScheduler().runTask(UltraHC.ins, new Runnable() {
					public void run() {
						getMenuFor(c).open(p);
					}
				});
			}
		});

		return m;
	}

	public ItemMenu getRemoveMenu(final ChatColor c) {
		OfflinePlayer[] ps = teams.get(c).getPlayers().toArray(new OfflinePlayer[0]);

		ItemMenu m = new ItemMenu("Remove Players - " + getName(c), 54);

		for (int i = 0; i < ps.length; i++) {
			final OfflinePlayer p = ps[i];
			Button pl = new Button(false, Material.SKULL_ITEM, 1, 3, getPrefix(p) + p.getName(), "",
					"Click to remove player");
			m.addButton(pl, i);
			pl.setOnClick(new ButtonRunnable() {
				public void run(final Player p2, InventoryClickEvent event) {
					p2.sendMessage("§cRemoved player " + p.getName() + " from team " + getName(c));

					if (p.isOnline() && p.getName().length() > DefaultListener.maxLength) {
						p.getPlayer().setPlayerListName(DefaultListener.getShortName(p.getPlayer()));
					}
					teams.get(c).removePlayer(p);
				}
			});
		}

		Button ba = new Button(false, Material.STICK, 1, 0, "Back", "Click to go back");
		m.addButton(ba, m.i.getSize() - 1);
		ba.setOnClick(new ButtonRunnable() {
			public void run(final Player p, InventoryClickEvent event) {
				p.closeInventory();
				Bukkit.getScheduler().runTask(UltraHC.ins, new Runnable() {
					public void run() {
						getMenuFor(c).open(p);
					}
				});
			}
		});

		return m;
	}

	public String getName(ChatColor c) {
		return WordUtils.capitalizeFully(c.name().replace('_', ' '));
	}

	public String getFullName(ChatColor c) {
		return c + getName(c);
	}

	public String getSubName(ChatColor c) {
		String n = getFullName(c);
		n = n.substring(0, n.length() > 16 ? 16 : n.length());
		return n;
	}

	public boolean isEnabled(ChatColor c) {
		return s.getTeam(getName(c).replace(' ', '_')) != null;
	}

	public String getPrefix(OfflinePlayer p) {
		for (Team t : teams.values()) {
			if (t.hasPlayer(p)) {
				return t.getPrefix();
			}
		}
		return "";
	}

	// public static void spreadPlayers() {
	// Random rand = new Random();
	// List<Player> undef = Arrays.asList(Bukkit.getOnlinePlayers());
	// do {
	// for (Team t : UltraHC.nts.teams) {
	// if (undef.size() > 0) {
	// boolean done = false;
	// do {
	// Player p =
	// Bukkit.getOnlinePlayers()[rand.nextInt(Bukkit.getOnlinePlayers().length)];
	// if (u.getPlayers().contains(p)) {
	// u.removePlayer(p);
	// t.addPlayer(p);
	// if (p.getName().length() > DefaultListener.maxLength) {
	// p.setPlayerListName(t.getPrefix() + DefaultListener.getShortName(p));
	// }
	// done = true;
	// }
	// } while (!done);
	// }
	// }
	// } while (u.getPlayers().size() > 0);
	// u.unregister();
	// }

	public ItemMenu getTeamSelector(Player p) {
		ItemMenu m = new ItemMenu(p.getName() + " - Teams", colors.length + (9 - (colors.length % 9)));

		for (int i = 0; i < colors.length; i++) {
			final ChatColor c = colors[i];
			final short d = woolData[i];

			Button b = new Button(false, Material.WOOL, 1, d, getFullName(c), "Click to join team", "", "Currently: "
					+ (isEnabled(c) ? "§a§oEnabled" : "§c§oDisabled"));
			m.addButton(b, i);
			b.setOnClick(new ButtonRunnable() {
				public void run(final Player p, InventoryClickEvent event) {
					if (isEnabled(c)) {
						Team j = teams.get(c);

						if (j.getPlayers().size() >= Option.Team_Limit.getInt()) {
							p.sendMessage("§cTeam is full!");
						} else {
							j.addPlayer(p);
							if (p.getName().length() > DefaultListener.maxLength) {
								p.setPlayerListName(c + DefaultListener.getShortName(p));
							}
							p.sendMessage("§aJoined team: " + getFullName(c));
						}
					} else {
						p.sendMessage("§cTeam is disabled!");
					}
				}
			});
		}

		return m;
	}
}
