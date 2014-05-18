package net.mcshockwave.UHC.Utils;

import net.minecraft.server.v1_7_R2.BiomeBase;
import net.minecraft.server.v1_7_R2.BiomeMeta;
import net.minecraft.server.v1_7_R2.Entity;
import net.minecraft.server.v1_7_R2.EntityInsentient;
import net.minecraft.server.v1_7_R2.EntityTypes;
import net.minecraft.server.v1_7_R2.World;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R2.CraftWorld;
import org.bukkit.entity.EntityType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomEntityRegistrar {

	public static void addCustomEntity(String name, EntityType shown, Class<? extends EntityInsentient> nmsClass,
			Class<? extends EntityInsentient> customClass) {
		@SuppressWarnings("deprecation")
		int id = shown.getTypeId();

		try {

			List<Map<?, ?>> dataMaps = new ArrayList<Map<?, ?>>();
			for (Field f : EntityTypes.class.getDeclaredFields()) {
				if (f.getType().getSimpleName().equals(Map.class.getSimpleName())) {
					f.setAccessible(true);
					dataMaps.add((Map<?, ?>) f.get(null));
				}
			}

			if (dataMaps.get(2).containsKey(id)) {
				dataMaps.get(0).remove(name);
				dataMaps.get(2).remove(id);
			}

			Method method = EntityTypes.class.getDeclaredMethod("a", Class.class, String.class, int.class);
			method.setAccessible(true);
			method.invoke(null, customClass, name, id);

			for (Field f : BiomeBase.class.getDeclaredFields()) {
				if (f.getType().getSimpleName().equals(BiomeBase.class.getSimpleName())) {
					if (f.get(null) != null) {

						for (Field list : BiomeBase.class.getDeclaredFields()) {
							if (list.getType().getSimpleName().equals(List.class.getSimpleName())) {
								list.setAccessible(true);
								@SuppressWarnings("unchecked")
								List<BiomeMeta> metaList = (List<BiomeMeta>) list.get(f.get(null));

								for (BiomeMeta meta : metaList) {
									Field clazz = BiomeMeta.class.getDeclaredFields()[0];
									if (clazz.get(meta).equals(nmsClass)) {
										clazz.set(meta, customClass);
									}
								}
							}
						}

					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static <E> Entity spawnCustomEntity(Class<? extends Entity> entClass, Location loc) {
		try {
			World w = ((CraftWorld) loc.getWorld()).getHandle();
			Entity ent = entClass.getConstructor(World.class).newInstance(w);
			ent.setPosition(loc.getX(), loc.getY(), loc.getZ());
			w.addEntity(ent);
			return ent;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
