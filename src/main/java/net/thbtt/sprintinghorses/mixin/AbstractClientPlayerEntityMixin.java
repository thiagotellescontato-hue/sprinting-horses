package net.thbtt.sprintinghorses.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.thbtt.sprintinghorses.sprint.ClientHorseSprintHud;
import net.thbtt.sprintinghorses.sprint.HorseSprintLogic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public class AbstractClientPlayerEntityMixin {
	@Inject(method = "getFovMultiplier", at = @At("RETURN"), cancellable = true)
	private void sprintinghorses$keepNormalFovWhenExhausted(CallbackInfoReturnable<Float> cir) {
		AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) (Object) this;
		if (ClientHorseSprintHud.isExhausted() && HorseSprintLogic.isSprintMount(player.getVehicle())) {
			cir.setReturnValue(1.0F);
		}
	}
}
