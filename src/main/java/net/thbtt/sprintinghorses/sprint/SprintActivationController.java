package net.thbtt.sprintinghorses.sprint;

import java.util.UUID;

import net.thbtt.sprintinghorses.config.SprintActivationMode;

public final class SprintActivationController {
	private SprintActivationMode mode = SprintActivationMode.HOLD;
	private UUID mountId;
	private boolean toggleActive;
	private boolean lastKeyPressed;
	private boolean depletedHoldLock;

	public boolean update(
		SprintActivationMode requestedMode,
		UUID currentMountId,
		boolean keyPressed,
		boolean forwardPressed,
		boolean sprintBlocked,
		boolean staminaDepleted
	) {
		if (requestedMode != mode) {
			mode = requestedMode;
			toggleActive = false;
			depletedHoldLock = false;
			lastKeyPressed = keyPressed;
		}

		if (currentMountId == null) {
			mountId = null;
			toggleActive = false;
			depletedHoldLock = false;
			lastKeyPressed = keyPressed;
			return false;
		}

		if (!currentMountId.equals(mountId)) {
			mountId = currentMountId;
			toggleActive = false;
			depletedHoldLock = false;
		}

		boolean pressed;
		if (mode == SprintActivationMode.TOGGLE) {
			if (sprintBlocked) {
				toggleActive = false;
			} else if (keyPressed && !lastKeyPressed) {
				toggleActive = !toggleActive;
			}
			pressed = toggleActive && forwardPressed && !sprintBlocked;
		} else {
			if (!keyPressed || !forwardPressed) {
				depletedHoldLock = false;
			} else if (staminaDepleted) {
				depletedHoldLock = true;
			}
			pressed = keyPressed && forwardPressed && !sprintBlocked && !depletedHoldLock;
		}

		lastKeyPressed = keyPressed;
		return pressed;
	}

	public void reset(boolean keyPressed) {
		mountId = null;
		toggleActive = false;
		depletedHoldLock = false;
		lastKeyPressed = keyPressed;
	}
}
