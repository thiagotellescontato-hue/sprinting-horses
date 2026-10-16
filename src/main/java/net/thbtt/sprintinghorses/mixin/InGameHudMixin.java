package net.thbtt.sprintinghorses.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.thbtt.sprintinghorses.compat.HorseSprintCompat;
import net.thbtt.sprintinghorses.config.HorseSprintConfig;
import net.thbtt.sprintinghorses.sprint.HorseSprintLogic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
	@Unique
	private static final int HUD_SHIFT = -10;

	@Shadow
	@Final
	private MinecraftClient client;

	@Inject(method = "renderStatusBars", at = @At("HEAD"))
	private void sprintinghorses$pushStatusBarsUp(DrawContext context, CallbackInfo ci) {
		if (shouldRenderHorseSprintHud()) {
			HorseSprintCompat.pushHudOffset(context, HUD_SHIFT);
		}
	}

	@Inject(method = "renderStatusBars", at = @At("RETURN"))
	private void sprintinghorses$popStatusBars(DrawContext context, CallbackInfo ci) {
		if (shouldRenderHorseSprintHud()) {
			HorseSprintCompat.popHudOffset(context);
		}
	}

	@Inject(method = "renderMountHealth", at = @At("HEAD"))
	private void sprintinghorses$pushMountHealthUp(DrawContext context, CallbackInfo ci) {
		if (shouldRenderHorseSprintHud()) {
			HorseSprintCompat.pushHudOffset(context, HUD_SHIFT);
		}
	}

	@Inject(method = "renderMountHealth", at = @At("RETURN"))
	private void sprintinghorses$popMountHealth(DrawContext context, CallbackInfo ci) {
		if (shouldRenderHorseSprintHud()) {
			HorseSprintCompat.popHudOffset(context);
		}
	}

	@Unique
	private boolean shouldRenderHorseSprintHud() {
		return HorseSprintConfig.isStaminaEnabled()
			&& client.player != null
			&& HorseSprintLogic.isSprintMount(client.player.getVehicle());
	}
}
