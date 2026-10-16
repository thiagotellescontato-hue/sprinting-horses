package net.thbtt.sprintinghorses.mixin;

import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.thbtt.sprintinghorses.sprint.HorseSprintLogic;
import net.thbtt.sprintinghorses.sprint.HorseSprintStateAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerPlayNetworkHandlerMixin {
	@Shadow
	public ServerPlayer player;

	@Inject(method = "handlePlayerCommand", at = @At("HEAD"), cancellable = true)
	private void sprintinghorses$blockExhaustedMountJumpPacket(ServerboundPlayerCommandPacket packet, CallbackInfo ci) {
		if (packet.getAction() != ServerboundPlayerCommandPacket.Action.START_RIDING_JUMP) {
			return;
		}

		Entity vehicle = player.getVehicle();
		if (!(vehicle instanceof AbstractHorse horse) || !HorseSprintLogic.isSprintMount(horse)) {
			return;
		}

		HorseSprintStateAccess stateAccess = (HorseSprintStateAccess) horse;
		if (stateAccess.sprintinghorses$isJumpSuppressed()) {
			stateAccess.sprintinghorses$clearJumpState();
			ci.cancel();
		}
	}
}
