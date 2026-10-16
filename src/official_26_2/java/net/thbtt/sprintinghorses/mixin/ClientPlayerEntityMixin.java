package net.thbtt.sprintinghorses.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.thbtt.sprintinghorses.compat.HorseSprintCompat;
import net.thbtt.sprintinghorses.sprint.ClientHorseSprintHud;
import net.thbtt.sprintinghorses.sprint.ClientHorseSprintSounds;
import net.thbtt.sprintinghorses.sprint.HorseSprintLogic;
import net.thbtt.sprintinghorses.sprint.HorseSprintStateAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public class ClientPlayerEntityMixin {
	@Shadow
	private float jumpRidingScale;

	@Shadow
	private boolean handsBusy;

	@Unique
	private boolean sprintinghorses$jumpWasPressedWhileExhausted;
	@Unique
	private boolean sprintinghorses$wasOnSprintMount;
	@Unique
	private int sprintinghorses$mountJumpClearTicks;
	@Unique
	private AbstractHorse sprintinghorses$lastSprintMount;

	@Inject(method = "aiStep", at = @At("HEAD"))
	private void sprintinghorses$clearMountJumpInputBeforeVanillaReadsIt(CallbackInfo ci) {
		LocalPlayer player = (LocalPlayer) (Object) this;
		AbstractHorse sprintMount = sprintinghorses$getSprintMount(player);
		boolean onSprintMount = sprintMount != null;
		if (!sprintinghorses$wasOnSprintMount && onSprintMount) {
			sprintinghorses$mountJumpClearTicks = HorseSprintLogic.POST_EXHAUSTION_MOUNT_JUMP_SUPPRESS_TICKS;
		}

		if (sprintinghorses$mountJumpClearTicks > 0 || sprintinghorses$isJumpBlockedOnSprintMount()) {
			sprintinghorses$clearMountJumpCharge(player);
			sprintinghorses$clearHorseJumpState(sprintMount);
			if (sprintinghorses$mountJumpClearTicks > 0) {
				sprintinghorses$mountJumpClearTicks--;
			}
		}
	}

	@Inject(
		method = "aiStep",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/player/LocalPlayer;jumpableVehicle()Lnet/minecraft/world/entity/PlayerRideableJumping;"
		)
	)
	private void sprintinghorses$blockMountJumpInputWhenExhausted(CallbackInfo ci) {
		LocalPlayer player = (LocalPlayer) (Object) this;
		if (!sprintinghorses$isJumpBlockedOnSprintMount()) {
			sprintinghorses$jumpWasPressedWhileExhausted = false;
			return;
		}

		boolean jumpPressed = HorseSprintCompat.isJumpPressed(player);
		if (ClientHorseSprintHud.isExhausted() && jumpPressed && !sprintinghorses$jumpWasPressedWhileExhausted && player.getVehicle() instanceof AbstractHorse horse) {
			ClientHorseSprintSounds.playExhaustedJumpSound(Minecraft.getInstance(), horse);
		}

		sprintinghorses$jumpWasPressedWhileExhausted = jumpPressed;
		sprintinghorses$clearMountJumpCharge(player);
	}

	@Inject(method = "aiStep", at = @At("RETURN"))
	private void sprintinghorses$clearMountJumpChargeWhenExhausted(CallbackInfo ci) {
		LocalPlayer player = (LocalPlayer) (Object) this;
		AbstractHorse sprintMount = sprintinghorses$getSprintMount(player);
		boolean onSprintMount = sprintMount != null;
		if (sprintinghorses$wasOnSprintMount && !onSprintMount) {
			sprintinghorses$clearMountJumpCharge(player);
			sprintinghorses$clearHorseJumpState(sprintinghorses$lastSprintMount);
			sprintinghorses$jumpWasPressedWhileExhausted = false;
			sprintinghorses$mountJumpClearTicks = 0;
		} else if (sprintinghorses$isJumpBlockedOnSprintMount()) {
			sprintinghorses$clearMountJumpCharge(player);
			sprintinghorses$clearHorseJumpState(sprintMount);
		}

		sprintinghorses$wasOnSprintMount = onSprintMount;
		sprintinghorses$lastSprintMount = sprintMount;
	}

	@Unique
	private void sprintinghorses$clearMountJumpCharge(LocalPlayer player) {
		HorseSprintCompat.clearJumpPressed(player);
		jumpRidingScale = 0.0F;
		handsBusy = false;
	}

	@Unique
	private AbstractHorse sprintinghorses$getSprintMount(LocalPlayer player) {
		return player.getVehicle() instanceof AbstractHorse horse && HorseSprintLogic.isSprintMount(horse) ? horse : null;
	}

	@Unique
	private void sprintinghorses$clearHorseJumpState(AbstractHorse horse) {
		if (horse != null) {
			((HorseSprintStateAccess) horse).sprintinghorses$clearJumpState();
		}
	}

	@Inject(method = "sendRidingJump", at = @At("HEAD"), cancellable = true)
	private void sprintinghorses$cancelStartRidingJumpWhenExhausted(CallbackInfo ci) {
		if (sprintinghorses$isJumpBlockedOnSprintMount()) {
			jumpRidingScale = 0.0F;
			handsBusy = false;
			ci.cancel();
		}
	}

	@Inject(method = "getJumpRidingScale", at = @At("RETURN"), cancellable = true)
	private void sprintinghorses$hideMountJumpChargeWhenExhausted(CallbackInfoReturnable<Float> cir) {
		if (sprintinghorses$isJumpBlockedOnSprintMount()) {
			cir.setReturnValue(0.0F);
		}
	}

	@Unique
	private boolean sprintinghorses$isJumpBlockedOnSprintMount() {
		LocalPlayer player = (LocalPlayer) (Object) this;
		return ClientHorseSprintHud.isJumpBlocked() && HorseSprintLogic.isSprintMount(player.getVehicle());
	}
}
