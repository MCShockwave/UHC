package net.mcshockwave.UHC.Utils;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;

public class CustomSignUtils {

	public static ArrayList<CustomSign>	reg	= new ArrayList<>();

	public static class CustomSign {
		public String[]		line	= new String[4];
		public String[]		pre		= new String[4];
		public SignRunnable	run;

		public CustomSign(String line1, String line2, String line3, String line4, String pre1, String pre2,
				String pre3, String pre4) {
			this.line[0] = line1;
			this.line[1] = line2;
			this.line[2] = line3;
			this.line[3] = line4;

			this.pre[0] = pre1;
			this.pre[1] = pre2;
			this.pre[2] = pre3;
			this.pre[3] = pre4;

			reg.add(this);
		}

		public void onClick(SignRunnable r) {
			this.run = r;
		}
	}

	public static class CustomSignListener implements Listener {
		@EventHandler
		public void onSignChange(SignChangeEvent event) {
			for (CustomSign cs : reg) {
				boolean match = true;
				for (int i = 0; i < 4; i++) {
					if (cs.pre[i] != null && !cs.pre[i].equalsIgnoreCase(event.getLine(i))) {
						match = false;
						continue;
					}
				}

				if (match) {
					for (int i = 0; i < 4; i++) {
						event.setLine(i, cs.line[i]);
					}
				}
			}
		}

		@EventHandler
		public void onPlayerInteract(PlayerInteractEvent event) {
			Player p = event.getPlayer();
			Block b = event.getClickedBlock();

			if (b != null && b.getState() instanceof Sign) {
				Sign s = (Sign) b.getState();
				for (CustomSign cs : reg) {
					if (cs.run == null)
						continue;
					boolean match = true;
					for (int i = 0; i < 4; i++) {
						if (cs.line[i] != null && !cs.line[i].equalsIgnoreCase(s.getLine(i))) {
							match = false;
							continue;
						}
					}

					if (match) {
						cs.run.run(p, s, event);
					}
				}
			}
		}
	}

	public static interface SignRunnable {
		public void run(Player p, Sign s, PlayerInteractEvent e);
	}

}
