package net.thbtt.sprintinghorses.sprint;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ClientHorseSprintHudVisibilityTest {
	@Test
	void f1HidesTheBarAtFullStamina() {
		assertFalse(ClientHorseSprintHudVisibility.shouldRender(true, true, true));
	}

	@Test
	void f1HidesTheBarAtPartialStamina() {
		assertFalse(ClientHorseSprintHudVisibility.shouldRender(true, true, true));
	}

	@Test
	void f1HidesTheBarDuringExhaustion() {
		assertFalse(ClientHorseSprintHudVisibility.shouldRender(true, true, true));
	}

	@Test
	void restoringTheHudShowsTheBarAgain() {
		assertTrue(ClientHorseSprintHudVisibility.shouldRender(false, true, true));
	}

	@Test
	void staminaDisabledOrUnsupportedMountKeepsTheBarHidden() {
		assertFalse(ClientHorseSprintHudVisibility.shouldRender(false, false, true));
		assertFalse(ClientHorseSprintHudVisibility.shouldRender(false, true, false));
	}
}
