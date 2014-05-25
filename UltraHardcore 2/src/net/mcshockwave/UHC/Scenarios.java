package net.mcshockwave.UHC;

import net.mcshockwave.UHC.Listeners.BarebonesListener;
import net.mcshockwave.UHC.Listeners.BloodPrice;
import net.mcshockwave.UHC.Listeners.ChumpHandler;
import net.mcshockwave.UHC.Listeners.Compensation;
import net.mcshockwave.UHC.Listeners.DTMListener;
import net.mcshockwave.UHC.Listeners.HallucinationHandler;
import net.mcshockwave.UHC.Listeners.LinkedListener;
import net.mcshockwave.UHC.Listeners.MoleListener;
import net.mcshockwave.UHC.Listeners.ResurrectListener;
import net.mcshockwave.UHC.Listeners.SkyhighHandler;
import net.mcshockwave.UHC.Listeners.SwitchListener;
import net.mcshockwave.UHC.Listeners.TowerListener;
import net.mcshockwave.UHC.Listeners.TripleListener;
import net.mcshockwave.UHC.Listeners.WeakestLink;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public enum Scenarios {

	Mini_UHC,
	Team_DM,
	DTM(
		new DTMListener()),
	Infinite_Enchanter,
	// Crux(
	// new CruxListener()),
	Linked(
		new LinkedListener()),
	Triple_Ores(
		new TripleListener()),
	Hallucinations(),
	Barebones(
		new BarebonesListener()),
	Mole(
		new MoleListener()),
	Resurrect(
		new ResurrectListener()),
	Switcheroo(
		new SwitchListener()),
	Chump_Charity(),
	Blood_Price(),
	Weakest_Link(),
	Compensation(
		new Compensation()),
	Tower_of_Death(
		new TowerListener()),
	Skyhigh;

	public static List<Scenarios>	enabled	= new ArrayList<>();

	public Listener					l		= null;

	private Scenarios() {
	}

	private Scenarios(Listener l) {
		this.l = l;
	}

	public boolean isEnabled() {
		return enabled.contains(this);
	}

	public void setEnabled(boolean en) {
		enabled.remove(this);
		if (en) {
			enabled.add(this);
		}

		if (UltraHC.started) {
			if (en) {
				if (l != null) {
					Bukkit.getPluginManager().registerEvents(l, UltraHC.ins);
				}
				onStart();
			} else {
				if (l != null) {
					HandlerList.unregisterAll(l);
				}
				onStop();
			}
		}
	}

	public static List<Scenarios> getEnabled() {
		ArrayList<Scenarios> ret = new ArrayList<>();

		for (Scenarios s : values()) {
			if (s.isEnabled()) {
				ret.add(s);
			}
		}

		return ret;
	}

	// public static void setToDefaults() {
	// for (Option o : Option.values()) {
	// if (o == Option.Scenario) {
	// continue;
	// }
	//
	// if (o.getType() == Integer.class) {
	// o.setInt(o.defInt);
	// } else if (o.getType() == String.class) {
	// o.setString(o.defString);
	// } else {
	// o.setBoolean(o.defBool);
	// }
	// }
	// }
	//
	// public void setOptions() {
	// setToDefaults();
	//
	// if (this == Vanilla) {
	// UltraHC.startCon = null;
	// UltraHC.startACon = null;
	// }
	//
	// if (this == Mini_UHC) {
	// // Option.Border_Radius.setInt(250);
	// // Option.Border_Rate.setInt(2);
	// // Option.Border_Time.setInt(15);
	// Option.Meet_Up_Time.setInt(15);
	// Option.Mark_Time.setInt(5);
	// Option.No_Kill_Time.setInt(2);
	// Option.Spread_Radius.setInt(250);
	// }
	//
	// if (this == OP_Enchants) {
	// // Option.Border_Radius.setInt(250);
	// // Option.Border_Time.setInt(15);
	// Option.Meet_Up_Time.setInt(15);
	// Option.Mark_Time.setInt(5);
	// Option.No_Kill_Time.setInt(3);
	// Option.Spread_Radius.setInt(250);
	// }
	//
	// if (this == Crux) {
	// // Option.Border_Rate.setInt(1);
	// // Option.Border_Time.setInt(180);
	// Option.Hunger.setBoolean(true);
	// Option.Spread_Radius.setInt(1000);
	// Option.Death_Distance.setBoolean(false);
	// }
	//
	// if (this == Triple_Ores) {
	// // Option.Border_Time.setInt(30);
	// Option.Meet_Up_Time.setInt(30);
	// }
	//
	// if (this == Team_DM) {
	// // Option.Border_Radius.setInt(250);
	// // Option.Border_Rate.setInt(2);
	// // Option.Border_Time.setInt(15);
	// Option.Meet_Up_Time.setInt(15);
	// Option.Mark_Time.setInt(5);
	// Option.No_Kill_Time.setInt(1);
	// Option.Spread_Radius.setInt(250);
	// Option.Head_on_Fence.setBoolean(false);
	// Option.UHC_Mode.setBoolean(false);
	// }
	// }

	public void onStart() {
		// if (this == Crux) {
		// // CruxListener.onStartGame();
		// }
		if (this == Linked) {
			LinkedListener.onStart();
		}
		if (this == Infinite_Enchanter) {
			for (Player p : UltraHC.getAlive()) {
				p.setLevel(Short.MAX_VALUE);
			}
		}
		if (this == Hallucinations) {
			HallucinationHandler.onStartGame();
		}
		if (this == Mole) {
			MoleListener.onStart();
		}
		if (this == Chump_Charity) {
			ChumpHandler.start(Option.Chump_Charity_Interval.getInt());
		}
		if (this == Blood_Price) {
			BloodPrice.start(Option.Blood_Price_Interval.getInt());
		}
		if (this == Weakest_Link) {
			WeakestLink.start(Option.Weakest_Link_Interval.getInt());
		}
		if (this == DTM) {
			DTMListener.start();
		}
		if (this == Tower_of_Death) {
			TowerListener.start();
		}
		if (this == Skyhigh) {
			SkyhighHandler.start();
		}
	}

	public void onStop() {
		// if (this == Crux) {
		// // CruxListener.part.cancel();
		// // CruxListener.cruxi.clear();
		// // CruxListener.cruxh.clear();
		// }
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
		if (this == DTM) {
			DTMListener.stop();
		}
		if (this == Tower_of_Death) {
			TowerListener.stop();
		}
		if (this == Skyhigh) {
			SkyhighHandler.stop();
		}
	}

}
