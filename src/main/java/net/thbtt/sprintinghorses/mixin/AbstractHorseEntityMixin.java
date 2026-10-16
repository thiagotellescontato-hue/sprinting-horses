package net.thbtt.sprintinghorses.mixin;

import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.thbtt.sprintinghorses.compat.HorseSprintCompat;
import net.thbtt.sprintinghorses.sprint.ClientHorseSprintHud;
import net.thbtt.sprintinghorses.sprint.HorseSprintLogic;
import net.thbtt.sprintinghorses.sprint.HorseSprintState;
import net.thbtt.sprintinghorses.sprint.HorseSprintStateAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHorseEntity.class)
public class AbstractHorseEntityMixin implements HorseSprintStateAccess {
	@Unique
	private final HorseSprintState sprintinghorses$state = new HorseSprintState();

	@Shadow
	protected float jumpStrength;

	@Shadow
	protected boolean jumping;

	@Inject(method = "tick", at = @At("TAIL"))
	private void sprintinghorses$tickSprintState(CallbackInfo ci) {
		AbstractHorseEntity horse = (AbstractHorseEntity) (Object) this;
		if (HorseSprintLogic.isSprintMount(horse)) {
			sprintinghorses$state.tick(horse);
		}
	}

	@Inject(method = "getSaddledSpeed", at = @At("RETURN"), cancellable = true)
	private void sprintinghorses$boostSaddledSpeed(PlayerEntity controllingPlayer, CallbackInfoReturnable<Float> cir) {
		AbstractHorseEntity horse = (AbstractHorseEntity) (Object) this;
		if (!sprintinghorses$isExhausted(horse) && sprintinghorses$state.canBoost(horse, controllingPlayer)) {
			cir.setReturnValue(cir.getReturnValue() * HorseSprintLogic.getSpeedMultiplier(horse, controllingPlayer));
		}
	}

	@Inject(method = "startJumping", at = @At("HEAD"), cancellable = true)
	private void sprintinghorses$spendJumpStamina(int height, CallbackInfo ci) {
		AbstractHorseEntity horse = (AbstractHorseEntity) (Object) this;
		if (!HorseSprintLogic.isSprintMount(horse)) {
			return;
		}

		boolean jumpBlocked = sprintinghorses$state.isJumpBlocked() || sprintinghorses$isClientJumpBlocked(horse);
		if (sprintinghorses$state.isJumpSuppressed() || sprintinghorses$isClientJumpBlocked(horse)) {
			sprintinghorses$clearJumpState();
			if (jumpBlocked) {
				sprintinghorses$state.playExhaustedJumpFeedback(horse);
			}
			ci.cancel();
			return;
		}

		if (!sprintinghorses$state.spendJumpStamina(horse)) {
			sprintinghorses$clearJumpState();
			ci.cancel();
		}
	}

	@Inject(method = "setJumpStrength", at = @At("HEAD"), cancellable = true)
	private void sprintinghorses$blockJumpWhenExhausted(int strength, CallbackInfo ci) {
		AbstractHorseEntity horse = (AbstractHorseEntity) (Object) this;
		if (!HorseSprintLogic.isSprintMount(horse)) {
			return;
		}

		boolean jumpBlocked = sprintinghorses$state.isJumpBlocked() || sprintinghorses$isClientJumpBlocked(horse);
		if (strength > 0 && (sprintinghorses$state.isJumpSuppressed() || sprintinghorses$isClientJumpBlocked(horse))) {
			sprintinghorses$clearJumpState();
			if (jumpBlocked) {
				sprintinghorses$state.playExhaustedJumpFeedback(horse);
			}
			ci.cancel();
		}
	}

	@Inject(method = "jump", at = @At("HEAD"), cancellable = true)
	private void sprintinghorses$cancelJumpWhenExhausted(float strength, Vec3d movementInput, CallbackInfo ci) {
		AbstractHorseEntity horse = (AbstractHorseEntity) (Object) this;
		if (!HorseSprintLogic.isSprintMount(horse)) {
			return;
		}

		boolean jumpBlocked = sprintinghorses$state.isJumpBlocked() || sprintinghorses$isClientJumpBlocked(horse);
		if (sprintinghorses$state.isJumpSuppressed() || sprintinghorses$isClientJumpBlocked(horse)) {
			sprintinghorses$clearJumpState();
			if (jumpBlocked) {
				sprintinghorses$state.playExhaustedJumpFeedback(horse);
			}
			ci.cancel();
		}
	}

	@Unique
	private boolean sprintinghorses$isExhausted(AbstractHorseEntity horse) {
		return sprintinghorses$state.isExhausted()
			|| (HorseSprintCompat.getWorld(horse).isClient() && ClientHorseSprintHud.isExhausted());
	}

	@Unique
	private boolean sprintinghorses$isClientJumpBlocked(AbstractHorseEntity horse) {
		return HorseSprintCompat.getWorld(horse).isClient() && ClientHorseSprintHud.isJumpBlocked();
	}

	@Override
	public boolean sprintinghorses$canBoost(PlayerEntity player) {
		AbstractHorseEntity horse = (AbstractHorseEntity) (Object) this;
		return !sprintinghorses$isExhausted(horse) && sprintinghorses$state.canBoost(horse, player);
	}

	@Override
	public boolean sprintinghorses$isJumpBlocked() {
		AbstractHorseEntity horse = (AbstractHorseEntity) (Object) this;
		return HorseSprintLogic.isSprintMount(horse)
			&& (sprintinghorses$state.isJumpBlocked() || sprintinghorses$isClientJumpBlocked(horse));
	}

	@Override
	public boolean sprintinghorses$isJumpSuppressed() {
		AbstractHorseEntity horse = (AbstractHorseEntity) (Object) this;
		return HorseSprintLogic.isSprintMount(horse)
			&& (sprintinghorses$state.isJumpSuppressed() || sprintinghorses$isClientJumpBlocked(horse));
	}

	@Override
	public void sprintinghorses$clearJumpState() {
		jumpStrength = 0.0F;
		jumping = false;
	}
}
