package net.thbtt.sprintinghorses.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.recipe.Ingredient;
import net.minecraft.world.World;
import net.thbtt.sprintinghorses.item.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PigEntity.class)
public abstract class PigEntityMixin extends AnimalEntity {
	@Unique
	private static final float GOLDEN_CARROT_ON_A_STICK_SPEED_MULTIPLIER = 1.5F;
	@Unique
	private static final double GOLDEN_CARROT_ON_A_STICK_TEMPT_SPEED = 1.2D;

	protected PigEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = "initGoals", at = @At("TAIL"))
	private void sprintinghorses$addGoldenCarrotOnAStickTemptGoal(CallbackInfo ci) {
		this.goalSelector.add(3, new TemptGoal(
			(PigEntity) (Object) this,
			GOLDEN_CARROT_ON_A_STICK_TEMPT_SPEED,
			Ingredient.ofItems(ModItems.GOLDEN_CARROT_ON_A_STICK),
			false
		));
	}

	@Inject(method = "getControllingPassenger", at = @At("RETURN"), cancellable = true)
	private void sprintinghorses$acceptGoldenCarrotOnAStick(CallbackInfoReturnable<LivingEntity> cir) {
		if (cir.getReturnValue() != null) {
			return;
		}

		Entity passenger = ((PigEntity) (Object) this).getFirstPassenger();
		if (passenger instanceof PlayerEntity player && sprintinghorses$isHoldingGoldenCarrotOnAStick(player)) {
			cir.setReturnValue(player);
		}
	}

	@Inject(method = "getSaddledSpeed", at = @At("RETURN"), cancellable = true)
	private void sprintinghorses$boostGoldenCarrotOnAStickSpeed(PlayerEntity controllingPlayer, CallbackInfoReturnable<Float> cir) {
		if (sprintinghorses$isHoldingGoldenCarrotOnAStick(controllingPlayer)) {
			cir.setReturnValue(cir.getReturnValue() * GOLDEN_CARROT_ON_A_STICK_SPEED_MULTIPLIER);
		}
	}

	@Unique
	private boolean sprintinghorses$isHoldingGoldenCarrotOnAStick(PlayerEntity player) {
		return player != null && player.isHolding(ModItems.GOLDEN_CARROT_ON_A_STICK);
	}
}
