package net.mcshockwave.UHC;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public enum SQLTable {
	Settings,
	Stats,
	Hall_Of_Fame,
	Bans;

	public static String		SqlIP	= "127.0.0.1";
	public static String		SqlName	= "vahost_38";
	public static String		SqlUser	= SqlName;

	// DONT LOOK AT THIS PLEASEEEEE
	public static String		SqlPass	= "24eilrahC";
	// TURN AWAY NOW!!!!

	public static Statement		stmt	= null;
	public static Connection	con		= null;
	
	public static BukkitTask bt = null;

	public static void enable() {
		try {
			if (bt != null) {
				bt.cancel();
			}
			
			con = DriverManager.getConnection("jdbc:mysql://" + SqlIP + ":3306/" + SqlName, SqlUser, new StringBuffer(
					SqlPass).reverse().toString());
			stmt = (Statement) con.createStatement();
			
			bt = Bukkit.getScheduler().runTaskTimer(UltraHC.ins, new Runnable() {
				public void run() {
					try {
						if (stmt.isClosed()) {
							enable();
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}, 12000, 6000);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean has(String col, String val) {
		String in = "SELECT " + col + " FROM " + name() + " WHERE " + col + "='" + val + "';";
		try {
			ResultSet rs = stmt.executeQuery(in);
			if (!rs.next())
				return false;
			Object o = rs.getObject(col);
			return o != null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public String get(String col, String val, String colGet) {
		String in = "SELECT " + colGet + " FROM " + name() + " WHERE " + col + "='" + val + "';";
		try {
			ResultSet rs = stmt.executeQuery(in);
			if (!rs.next())
				return null;
			String s = rs.getString(colGet);
			return s;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getWhere(String where, String colGet) {
		String in = "SELECT " + colGet + " FROM " + name() + " WHERE " + where + ";";
		try {
			ResultSet rs = stmt.executeQuery(in);
			if (!rs.next())
				return null;
			String s = rs.getString(colGet);
			return s;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public int getIntWhere(String where, String colGet) {
		String in = "SELECT " + colGet + " FROM " + name() + " WHERE " + where + ";";
		try {
			ResultSet rs = stmt.executeQuery(in);
			if (!rs.next())
				return -1;
			int s = rs.getInt(colGet);
			return s;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public int getInt(String col, String val, String colGet) {
		String in = "SELECT " + colGet + " FROM " + name() + " WHERE " + col + "='" + val + "';";
		try {
			ResultSet rs = stmt.executeQuery(in);
			if (!rs.next())
				return -1;
			int s = rs.getInt(colGet);
			return s;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public float getFloat(String col, String val, String colGet) {
		String in = "SELECT " + colGet + " FROM " + name() + " WHERE " + col + "='" + val + "';";
		try {
			ResultSet rs = stmt.executeQuery(in);
			if (!rs.next())
				return -1;
			float s = rs.getFloat(colGet);
			return s;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public ArrayList<String> getAll(String col, String val) {
		String in = "SELECT " + col + " FROM " + name() + " WHERE " + col + "='" + val + "';";
		try {
			ResultSet rs = stmt.executeQuery(in);
			ArrayList<String> b = new ArrayList<String>();
			while (rs.next()) {
				b.add(rs.getString(col));
			}
			return b;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public ArrayList<String> getAll(String col, String val, String colGet) {
		String in = "SELECT " + colGet + " FROM " + name() + " WHERE " + col + "='" + val + "';";
		try {
			ResultSet rs = stmt.executeQuery(in);
			ArrayList<String> b = new ArrayList<String>();
			while (rs.next()) {
				b.add(rs.getString(colGet));
			}
			return b;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public ArrayList<String> getAll(String colGet) {
		String in = "SELECT " + colGet + " FROM " + name() + ";";
		try {
			ResultSet rs = stmt.executeQuery(in);
			ArrayList<String> b = new ArrayList<String>();
			while (rs.next()) {
				b.add(rs.getString(colGet));
			}
			return b;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void add(String... add) {
		String in = "INSERT INTO " + name() + " (" + add[0];
		String vals = "'" + add[1] + "'";
		for (int i = 2; i < add.length; i++) {
			String s = add[i];
			if (i % 2 == 0) {
				in += ", " + s;
			} else {
				vals += ", '" + s + "'";
			}
		}
		in += ") VALUES (" + vals + ");";
		try {
			stmt.execute(in);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void set(String col, String val, String whereCol, String whereVal) {
		String in = "UPDATE " + name() + " SET " + col + "='" + val + "' WHERE " + whereCol + "='" + whereVal + "';";
		try {
			stmt.executeUpdate(in);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void del(String col, String val) {
		String in = "DELETE FROM " + name() + " WHERE " + col + "='" + val + "';";
		try {
			stmt.execute(in);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean hasWhere(String col, String where) {
		String in = "SELECT " + col + " FROM " + name() + " WHERE " + where + ";";
		try {
			ResultSet rs = stmt.executeQuery(in);
			if (!rs.next())
				return false;
			Object o = rs.getObject(col);
			return o != null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public LinkedHashMap<String, String> getAllOrder(String col1, String col2, int ord) {
		LinkedHashMap<String, String> ret = new LinkedHashMap<>();
		String in = "SELECT * FROM " + name() + " ORDER BY " + col2 + " DESC LIMIT 0, " + ord + ";";
		try {
			ResultSet rs = stmt.executeQuery(in);
			while (rs.next()) {
				ret.put(rs.getString(col1), rs.getString(col2));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public ResultSet getRSet(String where) {
		String in = "SELECT * FROM " + name() + " WHERE " + where + ";";
		try {
			ResultSet rs = stmt.executeQuery(in);
			return rs;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void delWhere(String where) {
		String in = "DELETE FROM " + name() + " WHERE " + where + ";";
		try {
			stmt.execute(in);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Example: "DELETE FROM %s WHERE 1" would purge table
	 */
	public void execute(String exec) {
		String in = String.format(exec, "'" + name() + "'");
		try {
			stmt.execute(in);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
