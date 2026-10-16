package net.thbtt.sprintinghorses.sprint;

public final class ClientHorseSprintHudVisibility {
	private ClientHorseSprintHudVisibility() {
	}

	public static boolean shouldRender(boolean hudHidden, boolean staminaEnabled, boolean mounted) {
		return !hudHidden && staminaEnabled && mounted;
	}
}
