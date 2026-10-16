package net.thbtt.sprintinghorses;

import java.util.UUID;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.thbtt.sprintinghorses.config.HorseSprintConfig;
import net.thbtt.sprintinghorses.network.HorseSprintInputPayload;
import net.thbtt.sprintinghorses.network.HorseSprintStaminaPayload;
import net.thbtt.sprintinghorses.sprint.ClientHorseSprintHud;
import net.thbtt.sprintinghorses.sprint.ClientHorseSprintHudRenderer;
import net.thbtt.sprintinghorses.sprint.ClientHorseSprintSounds;
import net.thbtt.sprintinghorses.sprint.HorseSprintInputs;
import net.thbtt.sprintinghorses.sprint.HorseSprintLogic;
import net.thbtt.sprintinghorses.sprint.SprintActivationController;

public class SprintingHorsesClient implements ClientModInitializer {
	private final SprintActivationController activationController = new SprintActivationController();
	private boolean lastPressed;
	private boolean lastJumpPressed;
	private ClientWorld lastWorld;
	private UUID lastPlayerId;

	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(HorseSprintStaminaPayload.ID, (payload, context) ->
			context.client().execute(() -> ClientHorseSprintHud.setStamina(payload.stamina(), payload.exhausted()))
		);

		ClientHorseSprintHudRenderer.register();
		ClientTickEvents.END_CLIENT_TICK.register(this::tick);
	}

	private void tick(MinecraftClient client) {
		if (client.player == null || client.world == null) {
			resetState(false);
			lastWorld = null;
			lastPlayerId = null;
			return;
		}

		boolean sprintKeyPressed = client.options.sprintKey.isPressed();
		UUID playerId = client.player.getUuid();
		if (client.world != lastWorld || !playerId.equals(lastPlayerId) || !client.player.isAlive()) {
			clearInput(client, sprintKeyPressed);
			lastWorld = client.world;
			lastPlayerId = playerId;
			if (!client.player.isAlive()) {
				return;
			}
		}

		Entity vehicle = client.player.getVehicle();
		boolean mounted = HorseSprintLogic.isSprintMount(vehicle);
		boolean exhausted = mounted && ClientHorseSprintHud.isExhausted();
		boolean forwardKeyPressed = client.options.forwardKey.isPressed();
		boolean sprintBlocked = mounted && ClientHorseSprintHud.isJumpBlocked();
		boolean staminaDepleted = mounted
			&& HorseSprintConfig.isPartialExhaustion()
			&& ClientHorseSprintHud.getStamina() <= 0.0F;
		boolean pressed = activationController.update(
			HorseSprintConfig.getSprintActivation(),
			mounted ? vehicle.getUuid() : null,
			sprintKeyPressed,
			forwardKeyPressed,
			sprintBlocked,
			staminaDepleted
		);
		boolean jumpPressed = mounted && client.options.jumpKey.isPressed();

		HorseSprintInputs.setPressed(client.player, pressed);
		if (exhausted && jumpPressed && !lastJumpPressed && vehicle instanceof AbstractHorseEntity horse) {
			ClientHorseSprintSounds.playExhaustedJumpSound(client, horse);
		}
		lastJumpPressed = jumpPressed;

		if (pressed != lastPressed) {
			ClientPlayNetworking.send(new HorseSprintInputPayload(pressed));
			lastPressed = pressed;
		}

		if (!mounted) {
			ClientHorseSprintHud.reset();
			lastJumpPressed = false;
		}
	}

	private void clearInput(MinecraftClient client, boolean sprintKeyPressed) {
		if (lastPressed) {
			ClientPlayNetworking.send(new HorseSprintInputPayload(false));
		}
		HorseSprintInputs.setPressed(client.player, false);
		resetState(sprintKeyPressed);
	}

	private void resetState(boolean sprintKeyPressed) {
		activationController.reset(sprintKeyPressed);
		ClientHorseSprintHud.reset();
		lastPressed = false;
		lastJumpPressed = false;
	}
}
