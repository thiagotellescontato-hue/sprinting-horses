package net.thbtt.sprintinghorses.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.thbtt.sprintinghorses.sprint.HorseSprintLogic;
import net.thbtt.sprintinghorses.sprint.HorseSprintStateAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
	@Shadow
	public ServerPlayerEntity player;

	@Inject(method = "onClientCommand", at = @At("HEAD"), cancellable = true)
	private void sprintinghorses$blockExhaustedMountJumpPacket(ClientCommandC2SPacket packet, CallbackInfo ci) {
		if (packet.getMode() != ClientCommandC2SPacket.Mode.START_RIDING_JUMP) {
			return;
		}

		Entity vehicle = player.getVehicle();
		if (!(vehicle instanceof AbstractHorseEntity horse) || !HorseSprintLogic.isSprintMount(horse)) {
			return;
		}

		HorseSprintStateAccess stateAccess = (HorseSprintStateAccess) horse;
		if (stateAccess.sprintinghorses$isJumpSuppressed()) {
			stateAccess.sprintinghorses$clearJumpState();
			ci.cancel();
		}
	}
}
