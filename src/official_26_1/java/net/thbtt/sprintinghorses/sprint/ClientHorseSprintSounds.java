package net.thbtt.sprintinghorses.sprint;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.thbtt.sprintinghorses.compat.HorseSprintCompat;

public final class ClientHorseSprintSounds {
	private static long lastExhaustedJumpSoundTick = Long.MIN_VALUE;

	private ClientHorseSprintSounds() {
	}

	public static void playExhaustedJumpSound(Minecraft client, AbstractHorse horse) {
		if (client.level == null) {
			return;
		}

		long time = client.level.getGameTime();
		if (time - lastExhaustedJumpSoundTick < HorseSprintLogic.EXHAUSTED_JUMP_SOUND_COOLDOWN_TICKS) {
			return;
		}

		lastExhaustedJumpSoundTick = time;
		HorseSprintCompat.playClientSound(client, HorseSprintLogic.getAngrySound(horse), 1.0F, 1.0F);
	}
}
