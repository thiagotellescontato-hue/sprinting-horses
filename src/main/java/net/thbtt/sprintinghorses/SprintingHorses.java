package net.thbtt.sprintinghorses;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.thbtt.sprintinghorses.config.HorseSprintConfig;
import net.thbtt.sprintinghorses.item.ModItems;
import net.thbtt.sprintinghorses.network.HorseSprintInputPayload;
import net.thbtt.sprintinghorses.network.HorseSprintStaminaPayload;
import net.thbtt.sprintinghorses.sprint.HorseSprintInputs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SprintingHorses implements ModInitializer {
	public static final String MOD_ID = "sprintinghorses";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		HorseSprintConfig.load();
		ModItems.register();

		PayloadTypeRegistry.playC2S().register(HorseSprintInputPayload.ID, HorseSprintInputPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(HorseSprintStaminaPayload.ID, HorseSprintStaminaPayload.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(HorseSprintInputPayload.ID, (payload, context) ->
			context.server().execute(() -> HorseSprintInputs.setPressed(context.player(), payload.pressed()))
		);
	}
}
