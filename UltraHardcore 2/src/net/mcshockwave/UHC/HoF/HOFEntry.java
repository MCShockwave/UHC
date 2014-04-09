package net.mcshockwave.UHC.HoF;

public class HOFEntry {
	
	public int game;
	public String winner;
	public String scen;
	public String teams;
	public String reddit;
	
	public HOFEntry(int game, String winner, String scen, String teams, String reddit) {
		this.game = game;
		this.winner = winner;
		this.scen = scen;
		this.teams = teams;
		this.reddit = reddit;
	}

}
