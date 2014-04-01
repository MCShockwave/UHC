package net.mcshockwave.UHC.HoF;

public enum HallOfFame {
	
	andrewbaseball99("FFA::No Hunger"),
	oXTheBigOneXo_and_Yerru("To2::Linked"),
	Nyzian("FFA::Hallucinations"),
	MooshroomCrafter_and_Epiktuu("FFA One Ally::Triple Ores");
	
	private String scenario;
	public String name;
	
	private HallOfFame(String scen) {
		scenario = scen;
		name = name().replace('_', ' ');
	}
	
	public int getNum() {
		for (int i = 0; i < values().length; i++) {
			if (values()[i] == this) {
				return i;
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
