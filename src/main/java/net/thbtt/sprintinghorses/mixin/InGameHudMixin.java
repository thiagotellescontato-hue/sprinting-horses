package net.thbtt.sprintinghorses.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.JumpingMount;
import net.thbtt.sprintinghorses.compat.HorseSprintCompat;
import net.thbtt.sprintinghorses.config.HorseSprintConfig;
import net.thbtt.sprintinghorses.sprint.ClientHorseSprintHudVisibility;
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
	@Unique
	private static final int EXPERIENCE_BAR_SHIFT = -8;
	@Unique
	private static final int EXPERIENCE_LEVEL_SHIFT = -5;

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

	@Inject(method = "renderExperienceBar", at = @At("HEAD"))
	private void sprintinghorses$pushExperienceBarUp(DrawContext context, int x, CallbackInfo ci) {
		if (shouldRenderHorseSprintHud()) {
			HorseSprintCompat.pushHudOffset(context, EXPERIENCE_BAR_SHIFT);
		}
	}

	@Inject(method = "renderExperienceBar", at = @At("RETURN"))
	private void sprintinghorses$popExperienceBar(DrawContext context, int x, CallbackInfo ci) {
		if (shouldRenderHorseSprintHud()) {
			HorseSprintCompat.popHudOffset(context);
		}
	}

	@Inject(method = "renderMountJumpBar", at = @At("HEAD"))
	private void sprintinghorses$pushMountJumpBarUp(JumpingMount mount, DrawContext context, int x, CallbackInfo ci) {
		if (shouldRenderHorseSprintHud()) {
			HorseSprintCompat.pushHudOffset(context, EXPERIENCE_BAR_SHIFT);
		}
	}

	@Inject(method = "renderMountJumpBar", at = @At("RETURN"))
	private void sprintinghorses$popMountJumpBar(JumpingMount mount, DrawContext context, int x, CallbackInfo ci) {
		if (shouldRenderHorseSprintHud()) {
			HorseSprintCompat.popHudOffset(context);
		}
	}

	@Inject(method = "renderExperienceLevel", at = @At("HEAD"))
	private void sprintinghorses$pushExperienceLevelUp(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
		if (shouldRenderHorseSprintHud()) {
			HorseSprintCompat.pushHudOffset(context, EXPERIENCE_LEVEL_SHIFT);
		}
	}

	@Inject(method = "renderExperienceLevel", at = @At("RETURN"))
	private void sprintinghorses$popExperienceLevel(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
		if (shouldRenderHorseSprintHud()) {
			HorseSprintCompat.popHudOffset(context);
		}
	}

	@Unique
	private boolean shouldRenderHorseSprintHud() {
		return ClientHorseSprintHudVisibility.shouldRender(
			client.options.hudHidden,
			HorseSprintConfig.isStaminaEnabled(),
			client.player != null && HorseSprintLogic.isSprintMount(client.player.getVehicle())
		);
	}
}
