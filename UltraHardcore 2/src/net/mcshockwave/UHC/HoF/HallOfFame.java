package net.mcshockwave.UHC.HoF;

import net.mcshockwave.UHC.Menu.ItemMenu;
import net.mcshockwave.UHC.Menu.ItemMenu.Button;
import net.mcshockwave.UHC.Menu.ItemMenu.ButtonRunnable;
import net.mcshockwave.UHC.db.ConfigFile;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;

public class HallOfFame {

	public static HOFEntry[] getEntries() {
		ArrayList<HOFEntry> ret = new ArrayList<>();

		ArrayList<String> entries = new ArrayList<>(ConfigFile.HOF.get().getStringList("entries"));

		for (int i = 0; i < entries.size(); i++) {
			String ent = entries.get(i);
			String[] split = ent.split(";");

			ret.add(new HOFEntry(i + 1, split[0], split[1], split[2], split[3]));
		}

		return ret.toArray(new HOFEntry[0]);
	}

	public static ItemMenu getMenu() {
		HOFEntry[] en = getEntries();
		ItemMenu m = new ItemMenu("Hall of Fame", en.length);

		for (int i = 0; i < en.length; i++) {
			final HOFEntry hof = en[i];
			String title = getColorsHOF(hof.winner);
			String line2 = "";
			if (title.contains("//")) {
				String[] spl = title.split("//");
				title = spl[0];
				line2 = "�e" + spl[1];
			}
			Button h = new Button(false, Material.SKULL_ITEM, 1, hof.teams.contains("FFA") ? 1 : 0, title, line2,
					"�3Game #" + hof.game, "�7Teams: �o" + hof.teams, "�bScenario: �o" + hof.scen);
			h.setOnClick(new ButtonRunnable() {
				public void run(Player p, InventoryClickEvent event) {
					p.sendMessage("�7Match link for Game #" + hof.game + ": �e" + hof.reddit);
				}
			});
			m.addButton(h, i);
		}

		return m;
	}

	public static String getColorsHOF(String name) {
		name = "�e" + name;

		if (name.contains(" and ")) {
			name = name.replaceAll(" and ", " �7and�e ");
		}

		if (name.contains(",")) {
			name = name.replaceAll(",", "�7,�e");
		}

		return name;
	}

}
