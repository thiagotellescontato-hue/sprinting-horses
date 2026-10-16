package net.thbtt.sprintinghorses.sprint;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.world.entity.player.Player;

public final class HorseSprintInputs {
	private static final Map<UUID, Boolean> PRESSED = new ConcurrentHashMap<>();

	private HorseSprintInputs() {
	}

	public static void setPressed(Player player, boolean pressed) {
		if (pressed) {
			PRESSED.put(player.getUUID(), true);
		} else {
			PRESSED.remove(player.getUUID());
		}
	}

	public static boolean isPressed(Player player) {
		return PRESSED.getOrDefault(player.getUUID(), false);
	}
}
