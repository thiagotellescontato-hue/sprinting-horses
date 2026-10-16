package net.thbtt.sprintinghorses.sprint;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import net.thbtt.sprintinghorses.SprintingHorses;
import net.thbtt.sprintinghorses.compat.HorseSprintCompat;
import net.thbtt.sprintinghorses.config.HorseSprintConfig;

public final class ClientHorseSprintHudRenderer {
	private static final Identifier STAMINA_BAR_BACKGROUND = Identifier.of(SprintingHorses.MOD_ID, "textures/stamina_bar_background.png");
	private static final Identifier STAMINA_BAR_PROGRESS = Identifier.of(SprintingHorses.MOD_ID, "textures/stamina_bar_progress.png");
	private static final Identifier STAMINA_BAR_PROGRESS_RED = Identifier.of(SprintingHorses.MOD_ID, "textures/stamina_bar_progress_red.png");

	private ClientHorseSprintHudRenderer() {
	}

	public static void register() {
		HudRenderCallback.EVENT.register((context, tickCounter) -> render(context));
	}

	private static void render(DrawContext context) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (!shouldRender(client)) {
			return;
		}

		int x = context.getScaledWindowWidth() / 2 - 91;
		int y = context.getScaledWindowHeight() - 29;
		int width = 182;
		int progress = Math.round(ClientHorseSprintHud.getStamina() * width);
		Identifier progressTexture = ClientHorseSprintHud.isExhausted() ? STAMINA_BAR_PROGRESS_RED : STAMINA_BAR_PROGRESS;

		HorseSprintCompat.drawTexture(context, STAMINA_BAR_BACKGROUND, x, y, width, 5);
		if (progress > 0) {
			HorseSprintCompat.drawTexture(context, progressTexture, x, y, progress, 5, width, 5);
		}
	}

	private static boolean shouldRender(MinecraftClient client) {
		return ClientHorseSprintHudVisibility.shouldRender(
			client.options.hudHidden,
			HorseSprintConfig.isStaminaEnabled(),
			client.player != null && HorseSprintLogic.isSprintMount(client.player.getVehicle())
		);
	}
}
