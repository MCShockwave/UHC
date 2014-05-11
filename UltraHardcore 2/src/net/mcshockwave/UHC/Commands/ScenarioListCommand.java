package net.mcshockwave.UHC.Commands;

import net.mcshockwave.UHC.Menu.ItemMenu.Button;
import net.mcshockwave.UHC.Scenarios;
import net.mcshockwave.UHC.Menu.ItemMenu;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ScenarioListCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (sender instanceof Player) {
			getMenu().open((Player) sender);
		}

		return false;
	}

	private ItemMenu getMenu() {
		int size = Scenarios.enabled.size();
		ItemMenu m = new ItemMenu("Enabled Scenarios", size <= 0 ? Scenarios.enabled.size() : 1);

		int id = -1;
		for (Scenarios s : Scenarios.getEnabled()) {
			Button b = new Button(false, Material.DIAMOND, 1, 0, s.name().replace('_', ' '));
			m.addButton(b, ++id);
		}

		return m;
	}

}
