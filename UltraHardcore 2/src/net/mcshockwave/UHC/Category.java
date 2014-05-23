package net.mcshockwave.UHC;

import org.bukkit.Material;

public enum Category {

	Game_Settings(
		Material.BEDROCK,
		0),
	End_Game(
		Material.BEACON,
		0),
	Teams(
		Material.WOOL,
		0),
	Scenarios(
		Material.DIAMOND,
		0);

	public Material	ico;
	public int		icodata;

	public String	name;

	private Category(Material ico, int icodata) {
		this.ico = ico;
		this.icodata = icodata;

		name = name().replace('_', ' ');
	}

}
