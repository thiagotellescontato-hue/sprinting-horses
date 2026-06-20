package net.thbtt.sprintinghorses.mixin;

import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.CamelEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.thbtt.sprintinghorses.sprint.HorseSprintLogic;
import net.thbtt.sprintinghorses.sprint.HorseSprintStateAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CamelEntity.class)
public abstract class CamelEntityMixin {
	@Unique
	private static final float VANILLA_CAMEL_SPRINT_BONUS = 0.1F;

	@Shadow
	private int dashCooldown;

	@Inject(method = "getSaddledSpeed", at = @At("RETURN"), cancellable = true)
	private void sprintinghorses$boostSaddledSpeed(PlayerEntity controllingPlayer, CallbackInfoReturnable<Float> cir) {
		AbstractHorseEntity horse = (AbstractHorseEntity) (Object) this;
		float speed = cir.getReturnValue();
		if (controllingPlayer != null && controllingPlayer.isSprinting() && dashCooldown == 0) {
			speed -= VANILLA_CAMEL_SPRINT_BONUS;
		}

		if (((HorseSprintStateAccess) this).sprintinghorses$canBoost(controllingPlayer)) {
			speed *= HorseSprintLogic.getSpeedMultiplier(horse, controllingPlayer);
		}

		cir.setReturnValue(speed);
	}
}
