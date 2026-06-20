package net.thbtt.sprintinghorses.sprint;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.passive.AbstractHorseEntity;

public final class ClientHorseSprintSounds {
	private static long lastExhaustedJumpSoundTick = Long.MIN_VALUE;

	private ClientHorseSprintSounds() {
	}

	public static void playExhaustedJumpSound(MinecraftClient client, AbstractHorseEntity horse) {
		if (client.world == null) {
			return;
		}

		long time = client.world.getTime();
		if (lastExhaustedJumpSoundTick == time) {
			return;
		}

		lastExhaustedJumpSoundTick = time;
		client.getSoundManager().play(PositionedSoundInstance.master(HorseSprintLogic.getAngrySound(horse), 1.0F, 1.0F));
	}
}
