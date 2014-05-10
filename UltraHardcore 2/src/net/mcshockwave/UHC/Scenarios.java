package net.mcshockwave.UHC;

import net.mcshockwave.UHC.Listeners.*;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public enum Scenarios {

	UHC,
	Mini_UHC,
	OP_Enchants,
	Crux(
		new CruxListener()),
	Linked(
		new LinkedListener()),
	Triple_Ores(
		new TripleListener()),
	Hallucinations(),
	Barebones(
		new BarebonesListener()),
	Mole(
		new MoleListener()),
	Team_DM,
	Resurrect(
		new ResurrectListener()),
	Switcheroo(
		new SwitchListener()),
	Chump_Charity(),
	Blood_Price(),
	Weakest_Link(),
	Compensation(
		new Compensation());

	public Listener	l	= null;

	private Scenarios() {
	}

	private Scenarios(Listener l) {
		this.l = l;
	}

	public static void setToDefaults() {
		for (Option o : Option.values()) {
			if (o == Option.Scenario) {
				continue;
			}

			if (o.getType() == Integer.class) {
				o.setInt(o.defInt);
			} else if (o.getType() == String.class) {
				o.setString(o.defString);
			} else {
				o.setBoolean(o.defBool);
			}
		}
	}

	public void setOptions() {
		setToDefaults();

		if (this == UHC) {
			UltraHC.startCon = null;
			UltraHC.startACon = null;
		}

		if (this == Mini_UHC) {
			// Option.Border_Radius.setInt(250);
			// Option.Border_Rate.setInt(2);
			// Option.Border_Time.setInt(15);
			Option.Meet_Up_Time.setInt(15);
			Option.Mark_Time.setInt(5);
			Option.No_Kill_Time.setInt(2);
			Option.Spread_Radius.setInt(250);
		}

		if (this == OP_Enchants) {
			// Option.Border_Radius.setInt(250);
			// Option.Border_Time.setInt(15);
			Option.Meet_Up_Time.setInt(15);
			Option.Mark_Time.setInt(5);
			Option.No_Kill_Time.setInt(3);
			Option.Spread_Radius.setInt(250);
		}

		if (this == Crux) {
			// Option.Border_Rate.setInt(1);
			// Option.Border_Time.setInt(180);
			Option.Hunger.setBoolean(true);
			Option.Spread_Radius.setInt(1000);
			Option.Death_Distance.setBoolean(false);
		}

		if (this == Triple_Ores) {
			// Option.Border_Time.setInt(30);
			Option.Meet_Up_Time.setInt(30);
		}

		if (this == Team_DM) {
			// Option.Border_Radius.setInt(250);
			// Option.Border_Rate.setInt(2);
			// Option.Border_Time.setInt(15);
			Option.Meet_Up_Time.setInt(15);
			Option.Mark_Time.setInt(5);
			Option.No_Kill_Time.setInt(1);
			Option.Spread_Radius.setInt(250);
			Option.Head_on_Fence.setBoolean(false);
			Option.UHC_Mode.setBoolean(false);
		}
	}

	public void onStart() {
		if (this == Crux) {
			// CruxListener.onStartGame();
		}
		if (this == Linked) {
			LinkedListener.onStart();
		}
		if (this == OP_Enchants) {
			for (Player p : UltraHC.getAlive()) {
				p.setLevel(1000);
			}
		}
		if (this == Hallucinations) {
			HallucinationHandler.onStartGame();
		}
		if (this == Mole) {
			MoleListener.onStart();
		}
		if (this == Chump_Charity) {
			ChumpHandler.start(10);
		}
		if (this == Blood_Price) {
			BloodPrice.start(10);
		}
		if (this == Weakest_Link) {
			WeakestLink.start(10);
		}
	}

	public void onStop() {
		if (this == Crux) {
			// CruxListener.part.cancel();
			// CruxListener.cruxi.clear();
			// CruxListener.cruxh.clear();
		}
		if (this == Linked) {
			LinkedListener.startSize.clear();
		}
		if (this == Hallucinations) {
			HallucinationHandler.onEndGame();
		}
		if (this == Mole) {
			MoleListener.moleKit.clear();
			MoleListener.moles.clear();
		}
		if (this == Chump_Charity) {
			ChumpHandler.stop();
		}
		if (this == Blood_Price) {
			BloodPrice.stop();
		}
		if (this == Weakest_Link) {
			WeakestLink.stop();
		}
	}

}
