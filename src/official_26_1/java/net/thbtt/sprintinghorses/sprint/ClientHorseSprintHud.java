package net.thbtt.sprintinghorses.sprint;

import net.thbtt.sprintinghorses.config.HorseSprintConfig;

public final class ClientHorseSprintHud {
	private static float stamina = 1.0F;
	private static boolean exhausted;

	private ClientHorseSprintHud() {
	}

	public static float getStamina() {
		if (!HorseSprintConfig.isStaminaEnabled()) {
			return 1.0F;
		}

		return stamina;
	}

	public static boolean isExhausted() {
		return HorseSprintConfig.isExhaustionEnabled() && exhausted;
	}

	public static boolean isJumpBlocked() {
		return isExhausted() || (HorseSprintConfig.isPartialExhaustion() && stamina <= 0.0F);
	}

	public static void setStamina(float value, boolean isExhausted) {
		if (!HorseSprintConfig.isStaminaEnabled()) {
			stamina = 1.0F;
			exhausted = false;
			return;
		}

		stamina = Math.max(0.0F, Math.min(1.0F, value));
		exhausted = HorseSprintConfig.isExhaustionEnabled() && isExhausted;
	}

	public static void reset() {
		stamina = 1.0F;
		exhausted = false;
	}
}
