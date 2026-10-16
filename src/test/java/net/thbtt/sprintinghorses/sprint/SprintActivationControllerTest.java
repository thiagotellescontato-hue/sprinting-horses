package net.thbtt.sprintinghorses.sprint;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import net.thbtt.sprintinghorses.config.SprintActivationMode;
import org.junit.jupiter.api.Test;

class SprintActivationControllerTest {
	private static final UUID HORSE = UUID.fromString("00000000-0000-0000-0000-000000000001");
	private static final UUID DONKEY = UUID.fromString("00000000-0000-0000-0000-000000000002");

	@Test
	void holdRequiresTheConfiguredSprintKeyToRemainPressed() {
		SprintActivationController controller = new SprintActivationController();

		assertFalse(update(controller, SprintActivationMode.HOLD, HORSE, false, true, false, false));
		assertTrue(update(controller, SprintActivationMode.HOLD, HORSE, true, true, false, false));
		assertFalse(update(controller, SprintActivationMode.HOLD, HORSE, false, true, false, false));
	}

	@Test
	void toggleChangesOnlyOnKeyPressEdges() {
		SprintActivationController controller = toggleController();

		assertTrue(update(controller, SprintActivationMode.TOGGLE, HORSE, true, true, false, false));
		assertTrue(update(controller, SprintActivationMode.TOGGLE, HORSE, true, true, false, false));
		assertTrue(update(controller, SprintActivationMode.TOGGLE, HORSE, false, true, false, false));
		assertFalse(update(controller, SprintActivationMode.TOGGLE, HORSE, true, true, false, false));
	}

	@Test
	void toggleRemainsEnabledWhileForwardMovementIsReleased() {
		SprintActivationController controller = toggleController();

		assertTrue(update(controller, SprintActivationMode.TOGGLE, HORSE, true, true, false, false));
		assertFalse(update(controller, SprintActivationMode.TOGGLE, HORSE, false, false, false, false));
		assertTrue(update(controller, SprintActivationMode.TOGGLE, HORSE, false, true, false, false));
	}

	@Test
	void toggleClearsOnDismountAndMountChange() {
		SprintActivationController controller = toggleController();

		assertTrue(update(controller, SprintActivationMode.TOGGLE, HORSE, true, true, false, false));
		assertFalse(update(controller, SprintActivationMode.TOGGLE, null, false, true, false, false));
		assertFalse(update(controller, SprintActivationMode.TOGGLE, HORSE, false, true, false, false));
		assertTrue(update(controller, SprintActivationMode.TOGGLE, HORSE, true, true, false, false));
		assertFalse(update(controller, SprintActivationMode.TOGGLE, DONKEY, false, true, false, false));
	}

	@Test
	void depletionRequiresRecoveryAndANewActivation() {
		SprintActivationController controller = toggleController();

		assertTrue(update(controller, SprintActivationMode.TOGGLE, HORSE, true, true, false, false));
		assertFalse(update(controller, SprintActivationMode.TOGGLE, HORSE, true, true, true, true));
		assertFalse(update(controller, SprintActivationMode.TOGGLE, HORSE, true, true, false, false));
		assertFalse(update(controller, SprintActivationMode.TOGGLE, HORSE, false, true, false, false));
		assertTrue(update(controller, SprintActivationMode.TOGGLE, HORSE, true, true, false, false));
	}

	@Test
	void holdDepletionLockClearsOnlyAfterRelease() {
		SprintActivationController controller = new SprintActivationController();

		assertTrue(update(controller, SprintActivationMode.HOLD, HORSE, true, true, false, false));
		assertFalse(update(controller, SprintActivationMode.HOLD, HORSE, true, true, true, true));
		assertFalse(update(controller, SprintActivationMode.HOLD, HORSE, true, true, false, false));
		assertFalse(update(controller, SprintActivationMode.HOLD, HORSE, false, true, false, false));
		assertTrue(update(controller, SprintActivationMode.HOLD, HORSE, true, true, false, false));
	}

	@Test
	void sessionResetDoesNotPreserveToggleState() {
		SprintActivationController controller = toggleController();

		assertTrue(update(controller, SprintActivationMode.TOGGLE, HORSE, true, true, false, false));
		controller.reset(true);
		assertFalse(update(controller, SprintActivationMode.TOGGLE, HORSE, true, true, false, false));
		assertFalse(update(controller, SprintActivationMode.TOGGLE, HORSE, false, true, false, false));
		assertTrue(update(controller, SprintActivationMode.TOGGLE, HORSE, true, true, false, false));
	}

	private static SprintActivationController toggleController() {
		SprintActivationController controller = new SprintActivationController();
		update(controller, SprintActivationMode.TOGGLE, HORSE, false, true, false, false);
		return controller;
	}

	private static boolean update(
		SprintActivationController controller,
		SprintActivationMode mode,
		UUID mountId,
		boolean keyPressed,
		boolean forwardPressed,
		boolean sprintBlocked,
		boolean staminaDepleted
	) {
		return controller.update(mode, mountId, keyPressed, forwardPressed, sprintBlocked, staminaDepleted);
	}
}
