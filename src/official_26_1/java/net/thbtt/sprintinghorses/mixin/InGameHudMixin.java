package net.thbtt.sprintinghorses.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.thbtt.sprintinghorses.SprintingHorses;
import net.thbtt.sprintinghorses.compat.HorseSprintCompat;
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

@Mixin(Gui.class)
public class InGameHudMixin {
	@Unique
	private static final Identifier STAMINA_BAR_BACKGROUND = Identifier.fromNamespaceAndPath(SprintingHorses.MOD_ID, "textures/stamina_bar_background.png");
	@Unique
	private static final Identifier STAMINA_BAR_PROGRESS = Identifier.fromNamespaceAndPath(SprintingHorses.MOD_ID, "textures/stamina_bar_progress.png");
	@Unique
	private static final Identifier STAMINA_BAR_PROGRESS_RED = Identifier.fromNamespaceAndPath(SprintingHorses.MOD_ID, "textures/stamina_bar_progress_red.png");
	@Unique
	private static final int HUD_SHIFT = -10;

	@Shadow
	@Final
	private Minecraft minecraft;

	@Unique
	private void sprintinghorses$renderStaminaBar(GuiGraphicsExtractor context) {
		if (!shouldRenderHorseSprintHud()) {
			return;
		}

		int x = context.guiWidth() / 2 - 91;
		int y = context.guiHeight() - 37;
		int width = 182;
		int progress = Math.round(ClientHorseSprintHud.getStamina() * width);
		Identifier progressTexture = ClientHorseSprintHud.isExhausted() ? STAMINA_BAR_PROGRESS_RED : STAMINA_BAR_PROGRESS;

		HorseSprintCompat.drawTexture(context, STAMINA_BAR_BACKGROUND, x, y, width, 5);
		if (progress > 0) {
			HorseSprintCompat.drawTexture(context, progressTexture, x, y, progress, 5, width, 5);
		}
	}

	@Inject(method = "extractPlayerHealth", at = @At("HEAD"))
	private void sprintinghorses$pushStatusBarsUp(GuiGraphicsExtractor context, CallbackInfo ci) {
		if (shouldRenderHorseSprintHud()) {
			HorseSprintCompat.pushHudOffset(context, HUD_SHIFT);
		}
	}

	@Inject(method = "extractPlayerHealth", at = @At("RETURN"))
	private void sprintinghorses$popStatusBars(GuiGraphicsExtractor context, CallbackInfo ci) {
		if (shouldRenderHorseSprintHud()) {
			HorseSprintCompat.popHudOffset(context);
		}
		sprintinghorses$renderStaminaBar(context);
	}

	@Inject(method = "extractVehicleHealth", at = @At("HEAD"))
	private void sprintinghorses$pushMountHealthUp(GuiGraphicsExtractor context, CallbackInfo ci) {
		if (shouldRenderHorseSprintHud()) {
			HorseSprintCompat.pushHudOffset(context, HUD_SHIFT);
		}
	}

	@Inject(method = "extractVehicleHealth", at = @At("RETURN"))
	private void sprintinghorses$popMountHealth(GuiGraphicsExtractor context, CallbackInfo ci) {
		if (shouldRenderHorseSprintHud()) {
			HorseSprintCompat.popHudOffset(context);
		}
	}

	@Unique
	private boolean shouldRenderHorseSprintHud() {
		return HorseSprintConfig.isStaminaEnabled()
			&& minecraft.player != null
			&& HorseSprintLogic.isSprintMount(minecraft.player.getVehicle());
	}
}
