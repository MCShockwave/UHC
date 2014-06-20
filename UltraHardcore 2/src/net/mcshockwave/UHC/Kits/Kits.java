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
		"0,313:1:0*1,312:1:0*2,311:1:0*3,310:1:0"),
	LegendaryWeps(
		"0,339:2:0:>dn/&bScroll of Enchanting>l/&7Type: &oSword*1,339:2:0:>dn/&bScroll"
				+ " of Enchanting>l/&7Type: &oBow*9,262:64:0*10,262:64:0*11,262:64:0*"
				+ "12,262:64:0*13,262:64:0*14,262:64:0*15,262:64:0*16,262:64:0*17,262:64:0",
		"0,313:1:0:>e/0-2|2-3*1,312:1:0:>e/0-2*2,311:1:0:>e/0-2*3,310:1:0:>e/0-2"),
	DTMGuns(
		"0,294:1:0:>dn/&cM16>l/&7Type: &b&oSingle Shot||&7Ammo Type: &b&oPrimar"
				+ "y Ammo||||&7Fire Rate: &e&oMedium||&7Range: &e&oLong||&7Accuracy: &e"
				+ "&oExtremely Good||&7Reload Time: &e&o3.0 seconds||&7Clip Size: &e&o10||&7"
				+ "Damage: &e&o5.0 Hearts*1,258:1:0:>dn/&cDesert Eagle>l/&7Type: &b&oPistol||"
				+ "&7Ammo Type: &b&oSecondary Ammo||||&7Fire Rate: &e&oMedium||&7Range: &e&oM"
				+ "edium||&7Accuracy: &e&oVery Good||&7Reload Time: &e&o3.0 seconds||&7Clip S"
				+ "ize: &e&o8||&7Damage: &e&o4.0 Hearts*2,270:1:0:>e/32-4*3,279:1:0:>e/32-4*4,"
				+ "277:1:0:>e/32-4*5,326:1:0*6,326:1:0*7,4:64:0*8,4:64:0*11,351:64:8:>dn/&rPrim"
				+ "ary Ammo>l/&eUsed in primary weapons*12,351:64:8:>dn/&rPrimary Ammo>l/&eUsed"
				+ " in primary weapons*13,351:64:7:>dn/&rSecondary Ammo>l/&eUsed in pistols*14"
				+ "58:8:0*15,4:64:0*16,4:64:0*17,4:64:0*18,292:1:0:>dn/&cRifle>l/&7Type: &b&oSn"
				+ "iper||&7Ammo Type: &b&oPrimary Ammo||||&7Fire Rate: &e&oVery Slow||&7Range: "
				+ "&e&oVery Far||&7Accuracy: &e&oPerfect||&7Reload Time: &e&o3.0 seconds||&7Cli"
				+ "p Size: &e&o8||&7Damage: &e&o6.0 Hearts*19,273:1:0:>dn/&cAK-47>l/&7Type: &b&"
				+ "oAutomatic||&7Ammo Type: &b&oPrimary Ammo||||&7Fire Rate: &e&oAutomatic||&7Ra"
				+ "nge: &e&oMedium||&7Accuracy: &e&oGood||&7Reload Time: &e&o4.0 seconds||&7Clip"
				+ " Size: &e&o30||&7Damage: &e&o2.0 Hearts*20,351:64:8:>dn/&rPrimary Ammo>l/&eUse"
				+ "d in primary weapons*21,351:64:8:>dn/&rPrimary Ammo>l/&eUsed in primary weapon"
				+ "s*22,351:64:7:>dn/&rSecondary Ammo>l/&eUsed in pistols*23,280:64:0*24,4:64:0*2"
				+ "5,4:64:0*26,4:64:0*27,293:1:0:>dn/&cShotgun>l/&7Type: &b&oShotgun||&7Ammo Type:"
				+ " &b&oPrimary Ammo||||&7Fire Rate: &e&oSlow||&7Range: &e&oVery Short||&7Accuracy"
				+ ": &e&oBad||&7Reload Time: &e&o4.0 seconds||&7Clip Size: &e&o12||&7Damage: &e&o7"
				+ ".5 Hearts*28,275:1:0:>dn/&cGlock>l/&7Type: &b&oPistol||&7Ammo Type: &b&oSecondar"
				+ "y Ammo||||&7Fire Rate: &e&oVery Fast||&7Range: &e&oMedium||&7Accuracy: &e&oGood|"
				+ "|&7Reload Time: &e&o2.0 seconds||&7Clip Size: &e&o5||&7Damage: &e&o3.0 Hearts*29,"
				+ "351:64:8:>dn/&rPrimary Ammo>l/&eUsed in primary weapons*30,351:64:8:>dn/&rPrimary"
				+ " Ammo>l/&eUsed in primary weapons*31,351:64:7:>dn/&rSecondary Ammo>l/&eUsed in pi"
				+ "stols*32,5:64:0*33,4:64:0*34,4:64:0*35,4:64:0",
		"0,309:1:0*1,308:1:0*2,307:1:0:>e/0-2*3,306:1:0");

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
