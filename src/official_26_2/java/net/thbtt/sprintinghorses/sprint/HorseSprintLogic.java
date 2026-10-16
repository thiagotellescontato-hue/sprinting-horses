package net.thbtt.sprintinghorses.sprint;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.animal.equine.Donkey;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.entity.animal.equine.Mule;
import net.minecraft.world.entity.animal.equine.SkeletonHorse;
import net.minecraft.world.entity.animal.equine.ZombieHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.thbtt.sprintinghorses.compat.HorseSprintCompat;

public final class HorseSprintLogic {
	public static final int MAX_STAMINA_TICKS = 200;
	public static final int EXHAUSTION_RECOVERY_TICKS = 200;
	public static final int STAMINA_RECHARGE_TICKS = 200;
	public static final int DEPLETED_RECHARGE_DELAY_TICKS = 60;
	public static final int JUMP_STAMINA_COST_TICKS = 30;
	public static final int POST_EXHAUSTION_MOUNT_JUMP_SUPPRESS_TICKS = 10;
	public static final int EXHAUSTED_JUMP_SOUND_COOLDOWN_TICKS = 20;
	public static final int NO_ARMOR_STAMINA_TICKS = 200;
	public static final int LEATHER_ARMOR_STAMINA_TICKS = 240;
	public static final int COPPER_ARMOR_STAMINA_TICKS = 280;
	public static final int IRON_ARMOR_STAMINA_TICKS = 320;
	public static final int GOLDEN_ARMOR_STAMINA_TICKS = 360;
	public static final int DIAMOND_ARMOR_STAMINA_TICKS = 420;
	public static final int NETHERITE_ARMOR_STAMINA_TICKS = 500;

	public static final float NO_ARMOR_SPEED_MULTIPLIER = 1.35F;
	public static final float LEATHER_ARMOR_SPEED_MULTIPLIER = 1.45F;
	public static final float COPPER_ARMOR_SPEED_MULTIPLIER = 1.55F;
	public static final float IRON_ARMOR_SPEED_MULTIPLIER = 1.65F;
	public static final float GOLDEN_ARMOR_SPEED_MULTIPLIER = 1.75F;
	public static final float DIAMOND_ARMOR_SPEED_MULTIPLIER = 1.90F;
	public static final float NETHERITE_ARMOR_SPEED_MULTIPLIER = 2.10F;
	public static final float DONKEY_SPEED_MULTIPLIER = 1.35F;

	private static final Identifier COPPER_HORSE_ARMOR_ID = Identifier.withDefaultNamespace("copper_horse_armor");
	private static final Identifier NETHERITE_HORSE_ARMOR_ID = Identifier.withDefaultNamespace("netherite_horse_armor");
	private static final int HORSE_ARMOR_SLOT = 0;

	private HorseSprintLogic() {
	}

	public static boolean isSprintMount(Entity entity) {
		return entity instanceof Horse
			|| entity instanceof ZombieHorse
			|| entity instanceof SkeletonHorse
			|| entity instanceof Donkey
			|| entity instanceof Mule;
	}

	public static boolean canSprint(Entity entity, Player player) {
		return isSprintMount(entity)
			&& player != null
			&& entity.hasPassenger(player);
	}

	public static boolean shouldSprint(AbstractHorse horse, Player player) {
		return canSprint(horse, player)
			&& horse.isTamed()
			&& HorseSprintInputs.isPressed(player);
	}

	public static boolean isSprintRequested(AbstractHorse horse, Player player) {
		return canSprint(horse, player)
			&& horse.isTamed()
			&& HorseSprintInputs.isPressed(player);
	}

	public static float getSpeedMultiplier(AbstractHorse horse, Player player) {
		if (isUnarmoredSprintMount(horse)) {
			return DONKEY_SPEED_MULTIPLIER;
		}

		return getArmorStats(horse).speedMultiplier();
	}

	public static int getMaxStaminaTicks(AbstractHorse horse) {
		if (isUnarmoredSprintMount(horse)) {
			return NO_ARMOR_STAMINA_TICKS;
		}

		return getArmorStats(horse).maxStaminaTicks();
	}

	public static SoundEvent getAngrySound(AbstractHorse horse) {
		if (horse instanceof Donkey) {
			return SoundEvents.DONKEY_ANGRY;
		}

		if (horse instanceof Mule) {
			return SoundEvents.MULE_ANGRY;
		}

		return SoundEvents.HORSE_ANGRY;
	}

	private static boolean isUnarmoredSprintMount(AbstractHorse horse) {
		return horse instanceof Donkey
			|| horse instanceof Mule
			|| horse instanceof SkeletonHorse;
	}

	private static ArmorSprintStats getArmorStats(AbstractHorse horse) {
		ItemStack armorStack = getArmorStack(horse);
		if (armorStack.isEmpty()) {
			return new ArmorSprintStats(NO_ARMOR_SPEED_MULTIPLIER, NO_ARMOR_STAMINA_TICKS);
		}

		Item armor = armorStack.getItem();
		if (armor == Items.LEATHER_HORSE_ARMOR) {
			return new ArmorSprintStats(LEATHER_ARMOR_SPEED_MULTIPLIER, LEATHER_ARMOR_STAMINA_TICKS);
		}
		if (armor == Items.IRON_HORSE_ARMOR) {
			return new ArmorSprintStats(IRON_ARMOR_SPEED_MULTIPLIER, IRON_ARMOR_STAMINA_TICKS);
		}
		if (armor == Items.GOLDEN_HORSE_ARMOR) {
			return new ArmorSprintStats(GOLDEN_ARMOR_SPEED_MULTIPLIER, GOLDEN_ARMOR_STAMINA_TICKS);
		}
		if (armor == Items.DIAMOND_HORSE_ARMOR) {
			return new ArmorSprintStats(DIAMOND_ARMOR_SPEED_MULTIPLIER, DIAMOND_ARMOR_STAMINA_TICKS);
		}

		Identifier armorId = BuiltInRegistries.ITEM.getKey(armor);
		if (COPPER_HORSE_ARMOR_ID.equals(armorId)) {
			return new ArmorSprintStats(COPPER_ARMOR_SPEED_MULTIPLIER, COPPER_ARMOR_STAMINA_TICKS);
		}
		if (NETHERITE_HORSE_ARMOR_ID.equals(armorId)) {
			return new ArmorSprintStats(NETHERITE_ARMOR_SPEED_MULTIPLIER, NETHERITE_ARMOR_STAMINA_TICKS);
		}

		return new ArmorSprintStats(NO_ARMOR_SPEED_MULTIPLIER, NO_ARMOR_STAMINA_TICKS);
	}

	private static ItemStack getArmorStack(AbstractHorse horse) {
		ItemStack armorStack = HorseSprintCompat.getHorseArmorStack(horse, HORSE_ARMOR_SLOT);
		if (armorStack.isEmpty()) {
			return ItemStack.EMPTY;
		}

		return armorStack;
	}

	private record ArmorSprintStats(float speedMultiplier, int maxStaminaTicks) {
	}
}
