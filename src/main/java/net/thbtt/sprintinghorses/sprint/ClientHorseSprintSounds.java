package net.thbtt.sprintinghorses.sprint;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.thbtt.sprintinghorses.compat.HorseSprintCompat;

public final class ClientHorseSprintSounds {
	private static long lastExhaustedJumpSoundTick = Long.MIN_VALUE;

	private ClientHorseSprintSounds() {
	}

	public static void playExhaustedJumpSound(MinecraftClient client, AbstractHorseEntity horse) {
		if (client.world == null) {
			return;
		}

		long time = client.world.getTime();
		if (time - lastExhaustedJumpSoundTick < HorseSprintLogic.EXHAUSTED_JUMP_SOUND_COOLDOWN_TICKS) {
			return;
		}

		lastExhaustedJumpSoundTick = time;
		HorseSprintCompat.playClientSound(client, HorseSprintLogic.getAngrySound(horse), 1.0F, 1.0F);
	}
}
