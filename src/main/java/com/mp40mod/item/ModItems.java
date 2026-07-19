package com.mp40mod.item;

import com.mp40mod.Mp40Mod;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModItems {

	public static final Item MP40 = registerItem("mp40",
			settings -> new Mp40Item(settings.maxCount(1).maxDamage(600)));

	public static final Item MP40_BULLET = registerItem("mp40_bullet",
			Item.Settings::new);

	private static Item registerItem(String path, java.util.function.Function<Item.Settings, Item> factory) {
		RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Mp40Mod.MOD_ID, path));
		Item.Settings settings = new Item.Settings().registryKey(key);
		Item item = factory.apply(settings);
		return Registry.register(Registries.ITEM, key, item);
	}

	public static void registerModItems() {
		Mp40Mod.LOGGER.info("[MP40Mod] كيسجل الأدوات ديال المود: {}", Mp40Mod.MOD_ID);
	}
}
