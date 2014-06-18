package net.mcshockwave.UHC.Kits;

import net.mcshockwave.UHC.UltraHC;
import net.mcshockwave.UHC.Utils.SerializationUtils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public enum Kits {

	// warning: spam, do not worry

	None(
		"",
		""),
	DTM(
		"0,276:1:0*1,261:1:0*2,278:1:0*3,279:1:0*4,277:1:0*5,326:1:0*6"
				+ ",326:1:0*7,4:64:0*8,4:64:0*9,116:4:0*10,262:64:0*14,58:8:0*"
				+ "15,4:64:0*16,4:64:0*17,4:64:0*18,264:3:0*19,262:64:0*20,384:"
				+ "64:0*23,280:64:0*24,4:64:0*25,4:64:0*26,4:64:0*27,265:32:0*2"
				+ "8,262:64:0*29,384:64:0*32,5:64:0*33,4:64:0*34,4:64:0*35,4:64:0",
		"0,309:1:0*1,308:1:0*2,307:1:0*3,306:1:0"),
	DTMInfinite(
		"0,276:1:0*1,261:1:0*2,278:1:0*3,279:1:0*4,277:1:0*5,326:1:0*6,3"
				+ "26:1:0*7,4:64:0*8,4:64:0*9,311:1:0*10,265:32:0*14,58:8:0*15,4"
				+ ":64:0*16,4:64:0*17,4:64:0*18,276:1:0*19,262:64:0*20,116:4:0*2"
				+ "3,280:64:0*24,4:64:0*25,4:64:0*26,4:64:0*27,261:1:0*28,262:64:"
				+ "0*29,47:64:0*32,5:64:0*33,4:64:0*34,4:64:0*35,4:64:0",
		"0,309:1:0*1,308:1:0*2,311:1:0*3,306:1:0"),
	InfiniteEnchanter(
		"0,276:1:0*1,261:1:0*3,116:4:0*4,47:64:0*7,322:2:0:>dn/&6Golden"
				+ " Head>l/&7&eMade from the head of:||&7&e&lNobody*8,322:4:0*10,"
				+ "262:64:0*16,276:1:0*17,261:1:0*19,262:64:0*25,276:1:0*26,261:1:0*28,262:64:0",
		"0,313:1:0*1,312:1:0*2,311:1:0*3,310:1:0");

	public ItemStack[]	con;
	public ItemStack[]	acon;

	Kits(String inv, String armor) {
		this.con = SerializationUtils.itemsFromString(inv, 36);
		this.acon = SerializationUtils.itemsFromString(armor, 4);
	}

	public void give(Player p) {
		UltraHC.setInventory(p, con, acon);
	}

}
