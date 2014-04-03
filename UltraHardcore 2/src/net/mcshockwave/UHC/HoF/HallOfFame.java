package net.mcshockwave.UHC.HoF;

public enum HallOfFame {

	Num1(
		"andrewbaseball99",
		"FFA::No Hunger"),
	Num2(
		"oXTheBigOneXo and Yerru",
		"To2::Linked"),
	Num3(
		"Nyzian",
		"FFA::Hallucinations"),
	Num4(
		"MooshroomCrafter and Epiktuu",
		"FFA One Ally::Triple Ores"),
	Num5(
		"Blaxcraft, Dianab0522, Offical_Sam and TnTToy",
		"RTo4::Vanilla"),
	Num6(
		"1bennettc",
		"FFA::Barebones");

	private String	scenario;
	public String	name;

	private HallOfFame(String players, String scen) {
		scenario = scen;
		name = players;
	}

	public int getNum() {
		for (int i = 0; i < values().length; i++) {
			if (values()[i] == this) {
				return i + 1;
			}
		}
		return 0;
	}

	public String getTeams() {
		return scenario.split("::")[0];
	}

	public String getScenario() {
		return scenario.split("::")[1];
	}
}
