package net.mcshockwave.UHC.Listeners;

import org.bukkit.event.Listener;

public class CruxListener implements Listener {

	/* TODO Fix this
	public static HashMap<Block, String>			cruxi	= new HashMap<>();
	public static HashMap<Block, Hologram>			cruxh	= new HashMap<>();
	public static HashMap<String, Integer>			id		= new HashMap<>();
	public static HashMap<String, SchedulerUtils>	sched	= new HashMap<>();

	public static BukkitTask						part;

	public static int								count	= 0;

	public static final PotionEffectType[]			pos		= { PotionEffectType.SPEED, PotionEffectType.FAST_DIGGING,
			PotionEffectType.DAMAGE_RESISTANCE, PotionEffectType.NIGHT_VISION, PotionEffectType.WATER_BREATHING,
			PotionEffectType.FIRE_RESISTANCE, PotionEffectType.INCREASE_DAMAGE };
	public static final PotionEffectType[]			neg		= { PotionEffectType.WEAKNESS, PotionEffectType.SLOW,
			PotionEffectType.SLOW_DIGGING, PotionEffectType.HUNGER, PotionEffectType.BLINDNESS,
			PotionEffectType.CONFUSION, PotionEffectType.WITHER };

	public static ArrayList<PotionEffectType>		total	= new ArrayList<>();
	public static int								neutral	= neg.length;

	public static boolean							isTeam	= false;

	public static void onStartGame() {
		isTeam = UltraHC.ts.isTeamGame();

		List<Material> norem = Arrays.asList(Material.OBSIDIAN, Material.BEDROCK, Material.BEACON);

		for (PotionEffectType pet : Lists.reverse(Arrays.asList(neg))) {
			total.add(pet);
		}
		total.add(null);
		for (PotionEffectType pet : pos) {
			total.add(pet);
		}

		if (isTeam) {
			for (Team t : UltraHC.ts.teams.values()) {
				Location tl = t.getPlayers().toArray(new OfflinePlayer[0])[0].getPlayer().getLocation();
				Location cl = tl.clone().add(5, 0, 0);
				cl.setY(Multiworld.getUHC().getHighestBlockYAt(cl.getBlockX(), cl.getBlockZ()) + 1);
				UltraHC.loadSchematic(UltraHC.cruxSchemName, cl);

				for (int x = -2; x < 2; x++) {
					for (int y = -2; y < 2; y++) {
						for (int z = -2; z < 2; z++) {
							Location m = cl.clone().add(x, y, z);
							if (m.getBlock().getType() == Material.BEACON && !cruxi.containsValue(t.getName())
									&& !cruxi.containsKey(m.getBlock())) {
								cruxi.put(m.getBlock(), t.getName());
								String ns = t.getName().endsWith("s") ? "'" : "'s";
								Hologram h = new HologramFactory(UltraHC.ins).withLocation(m.clone().add(0.5, 3, 0.5))
										.withText("§d§l" + t.getName() + ns + " Crux", "§eOwned by " + t.getName())
										.build();
								for (Player p2 : Bukkit.getOnlinePlayers()) {
									h.show(p2);
								}
								cruxh.put(m.getBlock(), h);
							} else if (!norem.contains(m.getBlock().getType())) {
								m.getBlock().setType(Material.AIR);
							}
						}
					}
				}
				id.put(t.getName(), neutral);
			}
		} else {
			for (Player p : UltraHC.getAlive()) {
				Location l = p.getLocation();
				l = l.add(5, 0, 0);
				l.setY(Multiworld.getUHC().getHighestBlockYAt(l.getBlockX(), l.getBlockZ()) + 1);
				UltraHC.loadSchematic(UltraHC.cruxSchemName, l);

				for (int x = -2; x < 2; x++) {
					for (int y = -2; y < 2; y++) {
						for (int z = -2; z < 2; z++) {
							Location m = l.clone().add(x, y, z);
							if (m.getBlock().getType() == Material.BEACON && !cruxi.containsValue(p.getName())
									&& !cruxi.containsKey(m.getBlock())) {
								cruxi.put(m.getBlock(), p.getName());
								String ns = p.getName().endsWith("s") ? "'" : "'s";
								Hologram h = new HologramFactory(UltraHC.ins).withLocation(m.clone().add(0.5, 3, 0.5))
										.withText("§d§l" + p.getName() + ns + " Crux", "§eOwned by " + p.getName())
										.build();
								for (Player p2 : Bukkit.getOnlinePlayers()) {
									h.show(p2);
								}
								cruxh.put(m.getBlock(), h);
							} else if (!norem.contains(m.getBlock().getType())) {
								m.getBlock().setType(Material.AIR);
							}
						}
					}
				}

				id.put(p.getName(), neutral);
			}
		}

		part = Bukkit.getScheduler().runTaskTimer(UltraHC.ins, new Runnable() {
			public void run() {
				count++;

				if (count >= 20) {
					count = 0;

					for (Hologram i : cruxh.values()) {
						i.clearAllPlayerViews();
						for (Player p : Bukkit.getOnlinePlayers()) {
							i.show(p);
						}
					}
				}

				if (count == 10 || count == 0) {
					if (!isTeam) {
						for (Player p : UltraHC.getAlive()) {
							if (!id.containsKey(p.getName())) {
								id.put(p.getName(), neutral);
							}

							int lvl = id.get(p.getName()) < neutral ? id.get(p.getName()) : getOwnedCruxes(p.getName())
									+ neutral;
							PotionEffect[] types = getEffects(lvl);
							for (PotionEffectType pe : total) {
								if (pe != null && !Arrays.asList(types).contains(pe)) {
									p.removePotionEffect(pe);
								}
							}
							for (PotionEffect pe : types) {
								p.addPotionEffect(pe);
							}
						}
					} else {
						for (Team t : UltraHC.ts.teams.values()) {
							if (!id.containsKey(t.getName())) {
								id.put(t.getName(), neutral);
							}

							int lvl = id.get(t.getName()) < neutral ? id.get(t.getName()) : getOwnedCruxes(t.getName())
									+ neutral;
							PotionEffect[] types = getEffects(lvl);
							for (OfflinePlayer op : t.getPlayers()) {
								if (!op.isOnline()) {
									continue;
								}
								Player p = op.getPlayer();
								for (PotionEffectType pe : total) {
									if (pe != null && !Arrays.asList(types).contains(pe)) {
										p.removePotionEffect(pe);
									}
								}
								for (PotionEffect pe : types) {
									p.addPotionEffect(pe);
								}
							}
						}
					}
				}

				for (Block b : cruxi.keySet()) {
					PacketUtils.playParticleEffect(ParticleEffect.ENCHANTMENT_TABLE,
							b.getLocation().add(0.5, 0.5, 0.5), 0, 1, 10);

					Block t1 = b.getLocation().clone().add(2, 3, 2).getBlock();
					Block t2 = b.getLocation().clone().add(-2, 3, 2).getBlock();
					Block t3 = b.getLocation().clone().add(2, 3, -2).getBlock();
					Block t4 = b.getLocation().clone().add(-2, 3, -2).getBlock();
					for (Block bl : new Block[] { t1, t2, t3, t4 }) {
						PacketUtils.playParticleEffect(ParticleEffect.WITCH_MAGIC, bl.getLocation().add(0.5, 0, 0.5),
								0, 1, 10);
					}
				}
			}
		}, 0, 5);
	}

	public static int getOwnedCruxes(String pl) {
		int own = 0;
		for (Entry<Block, Hologram> e : cruxh.entrySet()) {
			if (e.getValue().getLines()[1].replaceFirst(ownPre, "").equalsIgnoreCase(pl)
					&& !cruxi.get(e.getKey()).equalsIgnoreCase(pl)) {
				own++;
			}
		}
		return own;
	}

	public static PotionEffect[] getEffects(int lvl) {
		List<PotionEffect> ret = new ArrayList<>();

		if (lvl > total.size() - 1) {
			lvl = total.size() - 1;
		}

		if (lvl < neutral) {
			for (int i = lvl; i < neutral; i++) {
				ret.add(new PotionEffect(total.get(i), 80, 0));
			}
		}
		if (lvl > neutral) {
			for (int i = lvl; i > neutral; i--) {
				ret.add(new PotionEffect(total.get(i), 80, 0));
			}
		}

		return ret.toArray(new PotionEffect[0]);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player p = event.getPlayer();

		for (Block b : cruxi.keySet()) {
			if (b.getLocation().distanceSquared(event.getBlock().getLocation()) <= 10 * 10) {
				event.setCancelled(true);
				p.sendMessage("§cYou can't break blocks near a player's Crux!");
			}
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player p = event.getPlayer();

		for (Block b : cruxi.keySet()) {
			if (b.getLocation().distanceSquared(event.getBlock().getLocation()) <= 10 * 10) {
				event.setCancelled(true);
				p.sendMessage("§cYou can't place blocks near a player's Crux!");
			}
		}
	}

	public static final String	ownPre	= "§eOwned by ";

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		Action a = event.getAction();
		Block b = event.getClickedBlock();

		if (a == Action.RIGHT_CLICK_BLOCK && cruxi.containsKey(b)) {
			event.setCancelled(true);
			if (UltraHC.specs.contains(p.getName())) {
				p.sendMessage("§cSpectators can't capture Cruxs!");
				return;
			}
			if (UltraHC.started && Option.No_Kill_Time.getInt() > UltraHC.count.getTotalMins()) {
				p.sendMessage("§cNo capturing of a Crux until " + Option.No_Kill_Time.getInt() + " minutes in!");
				return;
			}
			final String ow = cruxi.get(b);
			Hologram h = cruxh.get(b);

			if (isTeam) {
				Team t = UltraHC.score.getPlayerTeam(p);
				String owner = cruxh.get(b).getLines()[1].replaceFirst(ownPre, "");
				if (owner.equalsIgnoreCase(t.getName())) {
					p.sendMessage("§a§lYou can't capture one of your team's captured Cruxes!");
					return;
				}
				if (t.getName().equalsIgnoreCase(ow)) {
					if (owner.equalsIgnoreCase(ow)) {
						p.sendMessage("§a§lYou can't capture your own team's Crux!");
					} else {
						Hologram n = new HologramFactory(UltraHC.ins).withLocation(h.getDefaultLocation())
								.withText(h.getLines()[0], ownPre + t.getName()).build();
						for (Player p2 : Bukkit.getOnlinePlayers()) {
							n.show(p2);
						}
						HoloAPI.getManager().stopTracking(h);
						HoloAPI.getManager().clearFromFile(h);
						for (Player p2 : Bukkit.getOnlinePlayers()) {
							h.clear(p2);
						}

						sched.get(ow).terminate();
						for (OfflinePlayer op : t.getPlayers()) {
							if (op.isOnline()) {
								Player p2 = op.getPlayer();
								for (PotionEffect pe : p2.getActivePotionEffects()) {
									p2.removePotionEffect(pe.getType());
								}
							}
						}
						id.put(t.getName(), neutral);
					}
					return;
				}
			} else {
				String owner = cruxh.get(b).getLines()[1].replaceFirst(ownPre, "");
				if (owner.equalsIgnoreCase(ow)) {
					p.sendMessage("§a§lYou can't capture one of your captured Cruxes!");
					return;
				}
				if (p.getName().equalsIgnoreCase(ow)) {
					if (owner.equalsIgnoreCase(ow)) {
						p.sendMessage("§a§lYou can't capture your own Crux!");
					} else {
						Hologram n = new HologramFactory(UltraHC.ins).withLocation(h.getDefaultLocation())
								.withText(h.getLines()[0], ownPre + p.getName()).build();
						for (Player p2 : Bukkit.getOnlinePlayers()) {
							n.show(p2);
						}
						HoloAPI.getManager().stopTracking(h);
						HoloAPI.getManager().clearFromFile(h);
						for (Player p2 : Bukkit.getOnlinePlayers()) {
							h.clear(p2);
						}

						sched.get(ow).terminate();
						for (PotionEffect pe : p.getActivePotionEffects()) {
							p.removePotionEffect(pe.getType());
						}
						id.put(p.getName(), neutral);
					}
					return;
				}
			}

			Hologram n;
			if (!isTeam) {
				n = new HologramFactory(UltraHC.ins).withLocation(h.getDefaultLocation())
						.withText(h.getLines()[0], ownPre + p.getName()).build();
			} else {
				Team t = UltraHC.score.getPlayerTeam(p);
				n = new HologramFactory(UltraHC.ins).withLocation(h.getDefaultLocation())
						.withText(h.getLines()[0], ownPre + t.getName()).build();
			}

			h.clearAllPlayerViews();
			HoloAPI.getManager().stopTracking(h);
			HoloAPI.getManager().clearFromFile(h);

			for (Player p2 : Bukkit.getOnlinePlayers()) {
				n.show(p2);
			}
			cruxh.remove(b);
			cruxh.put(b, n);

			if (!isTeam) {
				final Player o = Bukkit.getPlayer(ow);
				if (o != null) {
					o.sendMessage("§c§lYour Crux has been captured by " + p.getName() + "!");
					id.remove(ow);
					id.put(ow, neg.length);

					if (!sched.containsKey(ow)) {
						SchedulerUtils util = SchedulerUtils.getNew();
						sched.put(ow, util);

						util.add(o);
						for (int i = 0; i < neg.length; i++) {
							util.add(new Runnable() {
								public void run() {
									int i = id.get(ow);
									id.remove(ow);
									id.put(ow, --i);
								}
							});
							util.add(2000);
							util.add("§c§oYou feel your life draining as time goes by without your Crux captured...");
						}

						util.execute();
					}
				}
			} else {
				final Team t = UltraHC.score.getTeam(ow);
				if (t != null) {
					for (OfflinePlayer op : t.getPlayers()) {
						if (!op.isOnline()) {
							continue;
						}
						Player p2 = op.getPlayer();
						p2.sendMessage("§c§lYour team's Crux has been captured by " + p.getName() + " [" + t.getName()
								+ "§c§l]!");
					}
					id.remove(ow);
					id.put(ow, neg.length);

					if (!sched.containsKey(ow)) {
						SchedulerUtils util = SchedulerUtils.getNew();
						sched.put(ow, util);

						for (int i = 0; i < neg.length; i++) {
							util.add(new Runnable() {
								public void run() {
									int i = id.get(ow);
									id.remove(ow);
									id.put(ow, --i);
								}
							});
							util.add(2000);
							util.add(new Runnable() {
								public void run() {
									for (OfflinePlayer op : t.getPlayers()) {
										if (!op.isOnline()) {
											continue;
										}
										Player p2 = op.getPlayer();
										p2.sendMessage("§c§oYou feel your life draining as time goes by without your Crux captured...");
									}
								}
							});
						}

						util.execute();
					}
				}
			}

			p.sendMessage("§c§lCaptured Crux");
		}
	}
	*/
}
