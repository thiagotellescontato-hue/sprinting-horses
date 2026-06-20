package net.thbtt.sprintinghorses.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.JumpingMount;
import net.minecraft.util.Identifier;
import net.thbtt.sprintinghorses.SprintingHorses;
import net.thbtt.sprintinghorses.config.HorseSprintConfig;
import net.thbtt.sprintinghorses.sprint.ClientHorseSprintHud;
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
	private static final Identifier STAMINA_BAR_BACKGROUND = Identifier.of(SprintingHorses.MOD_ID, "textures/stamina_bar_background.png");
	@Unique
	private static final Identifier STAMINA_BAR_PROGRESS = Identifier.of(SprintingHorses.MOD_ID, "textures/stamina_bar_progress.png");
	@Unique
	private static final Identifier STAMINA_BAR_PROGRESS_RED = Identifier.of(SprintingHorses.MOD_ID, "textures/stamina_bar_progress_red.png");
	@Unique
	private static final int HUD_SHIFT = -10;

	@Shadow
	@Final
	private MinecraftClient client;

	@Inject(method = "renderMountJumpBar", at = @At("RETURN"))
	private void sprintinghorses$renderStaminaBar(JumpingMount mount, DrawContext context, int x, CallbackInfo ci) {
		if (!shouldRenderHorseSprintHud()) {
			return;
		}

		int y = context.getScaledWindowHeight() - 37;
		int width = 182;
		int progress = Math.round(ClientHorseSprintHud.getStamina() * width);
		Identifier progressTexture = ClientHorseSprintHud.isExhausted() ? STAMINA_BAR_PROGRESS_RED : STAMINA_BAR_PROGRESS;

		RenderSystem.enableBlend();
		context.drawTexture(STAMINA_BAR_BACKGROUND, x, y, 0.0F, 0.0F, width, 5, width, 5);
		if (progress > 0) {
			context.drawTexture(progressTexture, x, y, 0.0F, 0.0F, progress, 5, width, 5);
		}
		RenderSystem.disableBlend();
	}

	@Inject(method = "renderStatusBars", at = @At("HEAD"))
	private void sprintinghorses$pushStatusBarsUp(DrawContext context, CallbackInfo ci) {
		if (shouldRenderHorseSprintHud()) {
			context.getMatrices().push();
			context.getMatrices().translate(0.0F, HUD_SHIFT, 0.0F);
		}
	}

	@Inject(method = "renderStatusBars", at = @At("RETURN"))
	private void sprintinghorses$popStatusBars(DrawContext context, CallbackInfo ci) {
		if (shouldRenderHorseSprintHud()) {
			context.getMatrices().pop();
		}
	}

	@Inject(method = "renderMountHealth", at = @At("HEAD"))
	private void sprintinghorses$pushMountHealthUp(DrawContext context, CallbackInfo ci) {
		if (shouldRenderHorseSprintHud()) {
			context.getMatrices().push();
			context.getMatrices().translate(0.0F, HUD_SHIFT, 0.0F);
		}
	}

	@Inject(method = "renderMountHealth", at = @At("RETURN"))
	private void sprintinghorses$popMountHealth(DrawContext context, CallbackInfo ci) {
		if (shouldRenderHorseSprintHud()) {
			context.getMatrices().pop();
		}
	}

	@Unique
	private boolean shouldRenderHorseSprintHud() {
		return HorseSprintConfig.isStaminaEnabled()
			&& client.player != null
			&& HorseSprintLogic.isSprintMount(client.player.getVehicle());
	}
}
