package net.thbtt.sprintinghorses.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.OnAStickItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.thbtt.sprintinghorses.SprintingHorses;

public final class ModItems {
	public static final Item GOLDEN_CARROT_ON_A_STICK = register(
		"golden_carrot_on_a_stick",
		new OnAStickItem<>(new Item.Settings().maxDamage(25), EntityType.PIG, 7)
	);
	public static final Item CRIMSON_FUNGUS_ON_A_STICK = register(
		"crimson_fungus_on_a_stick",
		new OnAStickItem<>(new Item.Settings().maxDamage(100), EntityType.STRIDER, 1)
	);

	private ModItems() {
	}

	public static void register() {
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries ->
			entries.addAfter(Items.CARROT_ON_A_STICK, GOLDEN_CARROT_ON_A_STICK)
		);
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries ->
			entries.addAfter(Items.WARPED_FUNGUS_ON_A_STICK, CRIMSON_FUNGUS_ON_A_STICK)
		);
	}

	private static Item register(String name, Item item) {
		return Registry.register(Registries.ITEM, Identifier.of(SprintingHorses.MOD_ID, name), item);
	}
}
