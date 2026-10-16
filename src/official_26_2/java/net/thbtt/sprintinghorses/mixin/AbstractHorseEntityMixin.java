package net.thbtt.sprintinghorses.mixin;

import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
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

@Mixin(AbstractHorse.class)
public class AbstractHorseEntityMixin implements HorseSprintStateAccess {
	@Unique
	private final HorseSprintState sprintinghorses$state = new HorseSprintState();

	@Shadow
	protected float playerJumpPendingScale;

	@Shadow
	protected boolean allowStandSliding;

	@Inject(method = "tick", at = @At("TAIL"))
	private void sprintinghorses$tickSprintState(CallbackInfo ci) {
		AbstractHorse horse = (AbstractHorse) (Object) this;
		if (HorseSprintLogic.isSprintMount(horse)) {
			sprintinghorses$state.tick(horse);
		}
	}

	@Inject(method = "getRiddenSpeed", at = @At("RETURN"), cancellable = true)
	private void sprintinghorses$boostSaddledSpeed(Player controllingPlayer, CallbackInfoReturnable<Float> cir) {
		AbstractHorse horse = (AbstractHorse) (Object) this;
		if (!sprintinghorses$isExhausted(horse) && sprintinghorses$state.canBoost(horse, controllingPlayer)) {
			cir.setReturnValue(cir.getReturnValue() * HorseSprintLogic.getSpeedMultiplier(horse, controllingPlayer));
		}
	}

	@Inject(method = "handleStartJump", at = @At("HEAD"), cancellable = true)
	private void sprintinghorses$spendJumpStamina(int height, CallbackInfo ci) {
		AbstractHorse horse = (AbstractHorse) (Object) this;
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

	@Inject(method = "onPlayerJump", at = @At("HEAD"), cancellable = true)
	private void sprintinghorses$blockJumpWhenExhausted(int strength, CallbackInfo ci) {
		AbstractHorse horse = (AbstractHorse) (Object) this;
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

	@Inject(method = "executeRidersJump", at = @At("HEAD"), cancellable = true)
	private void sprintinghorses$cancelJumpWhenExhausted(float strength, Vec3 movementInput, CallbackInfo ci) {
		AbstractHorse horse = (AbstractHorse) (Object) this;
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
	private boolean sprintinghorses$isExhausted(AbstractHorse horse) {
		return sprintinghorses$state.isExhausted()
			|| (HorseSprintCompat.getWorld(horse).isClientSide() && ClientHorseSprintHud.isExhausted());
	}

	@Unique
	private boolean sprintinghorses$isClientJumpBlocked(AbstractHorse horse) {
		return HorseSprintCompat.getWorld(horse).isClientSide() && ClientHorseSprintHud.isJumpBlocked();
	}

	@Override
	public boolean sprintinghorses$canBoost(Player player) {
		AbstractHorse horse = (AbstractHorse) (Object) this;
		return !sprintinghorses$isExhausted(horse) && sprintinghorses$state.canBoost(horse, player);
	}

	@Override
	public boolean sprintinghorses$isJumpBlocked() {
		AbstractHorse horse = (AbstractHorse) (Object) this;
		return HorseSprintLogic.isSprintMount(horse)
			&& (sprintinghorses$state.isJumpBlocked() || sprintinghorses$isClientJumpBlocked(horse));
	}

	@Override
	public boolean sprintinghorses$isJumpSuppressed() {
		AbstractHorse horse = (AbstractHorse) (Object) this;
		return HorseSprintLogic.isSprintMount(horse)
			&& (sprintinghorses$state.isJumpSuppressed() || sprintinghorses$isClientJumpBlocked(horse));
	}

	@Override
	public void sprintinghorses$clearJumpState() {
		playerJumpPendingScale = 0.0F;
		allowStandSliding = false;
	}
}
