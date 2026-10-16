package net.thbtt.sprintinghorses.sprint;

import net.minecraft.world.entity.player.Player;

public interface HorseSprintStateAccess {
	boolean sprintinghorses$canBoost(Player player);

	boolean sprintinghorses$isJumpBlocked();

	boolean sprintinghorses$isJumpSuppressed();

	void sprintinghorses$clearJumpState();
}
