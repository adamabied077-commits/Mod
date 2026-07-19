package com.mp40mod;

import com.mp40mod.item.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mp40Mod implements ModInitializer {

	public static final String MOD_ID = "mp40mod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("[MP40Mod] كيتشغل السلاح ديال MP40...");

		ModItems.registerModItems();

		// نزيدو الأسلحة والذخيرة فـ tab ديال القتال (Combat) فـ Creative
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register((FabricItemGroupEntries entries) -> {
			entries.add(ModItems.MP40);
			entries.add(ModItems.MP40_BULLET);
		});
	}
}
