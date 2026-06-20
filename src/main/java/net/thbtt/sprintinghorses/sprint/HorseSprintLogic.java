package net.thbtt.sprintinghorses.sprint;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.SkeletonHorseEntity;
import net.minecraft.entity.mob.ZombieHorseEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.CamelEntity;
import net.minecraft.entity.passive.DonkeyEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.passive.MuleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public final class HorseSprintLogic {
	public static final int MAX_STAMINA_TICKS = 200;
	public static final int EXHAUSTION_RECOVERY_TICKS = 200;
	public static final int STAMINA_RECHARGE_TICKS = 200;
	public static final int DEPLETED_RECHARGE_DELAY_TICKS = 60;
	public static final int JUMP_STAMINA_COST_TICKS = 30;
	public static final int POST_EXHAUSTION_MOUNT_JUMP_SUPPRESS_TICKS = 10;
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

	private static final Identifier COPPER_HORSE_ARMOR_ID = Identifier.ofVanilla("copper_horse_armor");
	private static final Identifier NETHERITE_HORSE_ARMOR_ID = Identifier.ofVanilla("netherite_horse_armor");
	private static final int HORSE_ARMOR_SLOT = 0;

	private HorseSprintLogic() {
	}

	public static boolean isSprintMount(Entity entity) {
		return entity instanceof HorseEntity
			|| entity instanceof ZombieHorseEntity
			|| entity instanceof SkeletonHorseEntity
			|| entity instanceof DonkeyEntity
			|| entity instanceof MuleEntity
			|| entity instanceof CamelEntity;
	}

	public static boolean canSprint(Entity entity, PlayerEntity player) {
		return isSprintMount(entity)
			&& player != null
			&& entity.hasPassenger(player);
	}

	public static boolean shouldSprint(AbstractHorseEntity horse, PlayerEntity player) {
		return canSprint(horse, player)
			&& horse.isTame()
			&& HorseSprintInputs.isPressed(player)
			&& player.forwardSpeed > 0.0F;
	}

	public static boolean isSprintRequested(AbstractHorseEntity horse, PlayerEntity player) {
		return canSprint(horse, player)
			&& horse.isTame()
			&& HorseSprintInputs.isPressed(player);
	}

	public static float getSpeedMultiplier(AbstractHorseEntity horse, PlayerEntity player) {
		if (isUnarmoredSprintMount(horse)) {
			return DONKEY_SPEED_MULTIPLIER;
		}

		return getArmorStats(horse).speedMultiplier();
	}

	public static int getMaxStaminaTicks(AbstractHorseEntity horse) {
		if (isUnarmoredSprintMount(horse)) {
			return NO_ARMOR_STAMINA_TICKS;
		}

		return getArmorStats(horse).maxStaminaTicks();
	}

	public static SoundEvent getAngrySound(AbstractHorseEntity horse) {
		if (horse instanceof DonkeyEntity) {
			return SoundEvents.ENTITY_DONKEY_ANGRY;
		}

		if (horse instanceof MuleEntity) {
			return SoundEvents.ENTITY_MULE_ANGRY;
		}

		if (horse instanceof CamelEntity) {
			return SoundEvents.ENTITY_CAMEL_DASH_READY;
		}

		return SoundEvents.ENTITY_HORSE_ANGRY;
	}

	private static boolean isUnarmoredSprintMount(AbstractHorseEntity horse) {
		return horse instanceof DonkeyEntity
			|| horse instanceof MuleEntity
			|| horse instanceof SkeletonHorseEntity
			|| horse instanceof CamelEntity;
	}

	private static ArmorSprintStats getArmorStats(AbstractHorseEntity horse) {
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

		Identifier armorId = Registries.ITEM.getId(armor);
		if (COPPER_HORSE_ARMOR_ID.equals(armorId)) {
			return new ArmorSprintStats(COPPER_ARMOR_SPEED_MULTIPLIER, COPPER_ARMOR_STAMINA_TICKS);
		}
		if (NETHERITE_HORSE_ARMOR_ID.equals(armorId)) {
			return new ArmorSprintStats(NETHERITE_ARMOR_SPEED_MULTIPLIER, NETHERITE_ARMOR_STAMINA_TICKS);
		}

		return new ArmorSprintStats(NO_ARMOR_SPEED_MULTIPLIER, NO_ARMOR_STAMINA_TICKS);
	}

	private static ItemStack getArmorStack(AbstractHorseEntity horse) {
		if (horse.getInventory().size() <= HORSE_ARMOR_SLOT) {
			return ItemStack.EMPTY;
		}

		return horse.getInventory().getStack(HORSE_ARMOR_SLOT);
	}

	private record ArmorSprintStats(float speedMultiplier, int maxStaminaTicks) {
	}
}
