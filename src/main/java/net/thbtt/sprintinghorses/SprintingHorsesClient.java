package net.thbtt.sprintinghorses;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.CamelEntity;
import net.thbtt.sprintinghorses.config.HorseSprintConfig;
import net.thbtt.sprintinghorses.network.HorseSprintInputPayload;
import net.thbtt.sprintinghorses.network.HorseSprintStaminaPayload;
import net.thbtt.sprintinghorses.sprint.ClientHorseSprintHud;
import net.thbtt.sprintinghorses.sprint.ClientHorseSprintSounds;
import net.thbtt.sprintinghorses.sprint.HorseSprintInputs;
import net.thbtt.sprintinghorses.sprint.HorseSprintLogic;

public class SprintingHorsesClient implements ClientModInitializer {
	private boolean lastPressed;
	private boolean lastJumpPressed;
	private boolean depletedSprintLock;

	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(HorseSprintStaminaPayload.ID, (payload, context) ->
			context.client().execute(() -> ClientHorseSprintHud.setStamina(payload.stamina(), payload.exhausted()))
		);

		ClientTickEvents.END_CLIENT_TICK.register(this::tick);
	}

	private void tick(MinecraftClient client) {
		if (client.player == null || client.world == null) {
			lastPressed = false;
			lastJumpPressed = false;
			depletedSprintLock = false;
			return;
		}

		Entity vehicle = client.player.getVehicle();
		boolean mounted = HorseSprintLogic.isSprintMount(vehicle);
		boolean exhausted = mounted && ClientHorseSprintHud.isExhausted();
		boolean sprintKeyPressed = client.options.sprintKey.isPressed();
		if (!mounted || !sprintKeyPressed) {
			depletedSprintLock = false;
		} else if (HorseSprintConfig.isPartialExhaustion() && ClientHorseSprintHud.getStamina() <= 0.0F) {
			depletedSprintLock = true;
		}

		boolean requested = mounted && !exhausted && sprintKeyPressed;
		boolean pressed = requested && !depletedSprintLock;
		boolean jumpPressed = mounted && client.options.jumpKey.isPressed();

		HorseSprintInputs.setPressed(client.player, pressed);
		if (vehicle instanceof CamelEntity && !pressed && client.player.isSprinting()) {
			client.player.setSprinting(false);
		}
		if (exhausted && jumpPressed && !lastJumpPressed && vehicle instanceof AbstractHorseEntity horse) {
			ClientHorseSprintSounds.playExhaustedJumpSound(client, horse);
		}
		lastJumpPressed = jumpPressed;

		if (pressed != lastPressed) {
			ClientPlayNetworking.send(new HorseSprintInputPayload(pressed));
			lastPressed = pressed;
		}

		if (!mounted) {
			if (lastPressed) {
				ClientPlayNetworking.send(new HorseSprintInputPayload(false));
				lastPressed = false;
			}
			ClientHorseSprintHud.reset();
			lastJumpPressed = false;
			depletedSprintLock = false;
		}
	}
}
