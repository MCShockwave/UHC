package net.mcshockwave.UHC.HoF;

public enum HallOfFameOLD {

	Num1(
		"andrewbaseball99",
		"FFA::No Hunger",
		"http://redd.it/211pzv"),
	Num2(
		"oXTheBigOneXo and Yerru",
		"To2::Linked",
		"http://redd.it/219x5j"),
	Num3(
		"Nyzian",
		"FFA::Hallucinations",
		"http://redd.it/21kk2l"),
	Num4(
		"MooshroomCrafter and Epiktuu",
		"FFA One Ally::Triple Ores",
		"http://redd.it/21vru1"),
	Num5(
		"Blaxcraft, Dianab0522, Offical_Sam,// and TnTToy",
		"RTo4::Vanilla",
		"http://redd.it/21wfbc"),
	Num6(
		"1bennettc",
		"FFA::Barebones",
		"http://redd.it/21zo1i"),
	Num7(
		"matthew010411 and osi12345678",
		"RTo2::Triple Ores",
		"http://redd.it/222cy6"),
	Num8(
		"matthew010411, ShockeryFlame, dashdude,// NEONpooP, and GreenDoomsDay (Mole)",
		"RTo5::Mole",
		"http://redd.it/22565d"),
	Num9(
		"JamieTheElite",
		"FFA::Barebones",
		"http://redd.it/22agw3");

	private String	scenario;
	public String	name;
	public String match;

	private HallOfFameOLD(String players, String scen, String matchLink) {
		scenario = scen;
		name = players;
		match = matchLink;
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
