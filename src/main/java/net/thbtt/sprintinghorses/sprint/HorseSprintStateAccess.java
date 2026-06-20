package net.thbtt.sprintinghorses.sprint;

import net.minecraft.entity.player.PlayerEntity;

public interface HorseSprintStateAccess {
	boolean sprintinghorses$canBoost(PlayerEntity player);

	boolean sprintinghorses$isJumpBlocked();

	boolean sprintinghorses$isJumpSuppressed();

	void sprintinghorses$clearJumpState();
}
