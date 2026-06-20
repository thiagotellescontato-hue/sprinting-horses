package net.thbtt.sprintinghorses.sprint;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.entity.player.PlayerEntity;

public final class HorseSprintInputs {
	private static final Map<UUID, Boolean> PRESSED = new ConcurrentHashMap<>();

	private HorseSprintInputs() {
	}

	public static void setPressed(PlayerEntity player, boolean pressed) {
		if (pressed) {
			PRESSED.put(player.getUuid(), true);
		} else {
			PRESSED.remove(player.getUuid());
		}
	}

	public static boolean isPressed(PlayerEntity player) {
		return PRESSED.getOrDefault(player.getUuid(), false);
	}
}
