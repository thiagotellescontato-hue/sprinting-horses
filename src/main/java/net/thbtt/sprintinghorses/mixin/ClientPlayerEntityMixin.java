package net.thbtt.sprintinghorses.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
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

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
	@Shadow
	private float mountJumpStrength;

	@Shadow
	private boolean riding;

	@Unique
	private boolean sprintinghorses$jumpWasPressedWhileExhausted;
	@Unique
	private boolean sprintinghorses$wasOnSprintMount;
	@Unique
	private int sprintinghorses$mountJumpClearTicks;
	@Unique
	private AbstractHorseEntity sprintinghorses$lastSprintMount;

	@Inject(method = "tickMovement", at = @At("HEAD"))
	private void sprintinghorses$clearMountJumpInputBeforeVanillaReadsIt(CallbackInfo ci) {
		ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
		AbstractHorseEntity sprintMount = sprintinghorses$getSprintMount(player);
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
		method = "tickMovement",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/network/ClientPlayerEntity;getJumpingMount()Lnet/minecraft/entity/JumpingMount;"
		)
	)
	private void sprintinghorses$blockMountJumpInputWhenExhausted(CallbackInfo ci) {
		ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
		if (!sprintinghorses$isJumpBlockedOnSprintMount()) {
			sprintinghorses$jumpWasPressedWhileExhausted = false;
			return;
		}

		boolean jumpPressed = HorseSprintCompat.isJumpPressed(player);
		if (ClientHorseSprintHud.isExhausted() && jumpPressed && !sprintinghorses$jumpWasPressedWhileExhausted && player.getVehicle() instanceof AbstractHorseEntity horse) {
			ClientHorseSprintSounds.playExhaustedJumpSound(MinecraftClient.getInstance(), horse);
		}

		sprintinghorses$jumpWasPressedWhileExhausted = jumpPressed;
		sprintinghorses$clearMountJumpCharge(player);
	}

	@Inject(method = "tickMovement", at = @At("RETURN"))
	private void sprintinghorses$clearMountJumpChargeWhenExhausted(CallbackInfo ci) {
		ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
		AbstractHorseEntity sprintMount = sprintinghorses$getSprintMount(player);
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
	private void sprintinghorses$clearMountJumpCharge(ClientPlayerEntity player) {
		HorseSprintCompat.clearJumpPressed(player);
		mountJumpStrength = 0.0F;
		riding = false;
	}

	@Unique
	private AbstractHorseEntity sprintinghorses$getSprintMount(ClientPlayerEntity player) {
		return player.getVehicle() instanceof AbstractHorseEntity horse && HorseSprintLogic.isSprintMount(horse) ? horse : null;
	}

	@Unique
	private void sprintinghorses$clearHorseJumpState(AbstractHorseEntity horse) {
		if (horse != null) {
			((HorseSprintStateAccess) horse).sprintinghorses$clearJumpState();
		}
	}

	@Inject(method = "startRidingJump", at = @At("HEAD"), cancellable = true)
	private void sprintinghorses$cancelStartRidingJumpWhenExhausted(CallbackInfo ci) {
		if (sprintinghorses$isJumpBlockedOnSprintMount()) {
			mountJumpStrength = 0.0F;
			riding = false;
			ci.cancel();
		}
	}

	@Inject(method = "getMountJumpStrength", at = @At("RETURN"), cancellable = true)
	private void sprintinghorses$hideMountJumpChargeWhenExhausted(CallbackInfoReturnable<Float> cir) {
		if (sprintinghorses$isJumpBlockedOnSprintMount()) {
			cir.setReturnValue(0.0F);
		}
	}

	@Unique
	private boolean sprintinghorses$isJumpBlockedOnSprintMount() {
		ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
		return ClientHorseSprintHud.isJumpBlocked() && HorseSprintLogic.isSprintMount(player.getVehicle());
	}
}
