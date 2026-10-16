package net.thbtt.sprintinghorses.sprint;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.thbtt.sprintinghorses.compat.HorseSprintCompat;
import net.thbtt.sprintinghorses.config.HorseSprintConfig;
import net.thbtt.sprintinghorses.network.HorseSprintStaminaPayload;

public final class HorseSprintState {
	private int staminaTicks = HorseSprintLogic.MAX_STAMINA_TICKS;
	private int exhaustionRecoveryTicks;
	private int lastMaxStaminaTicks = HorseSprintLogic.MAX_STAMINA_TICKS;
	private long lastExhaustedJumpFeedbackTick = Long.MIN_VALUE;
	private int depletedRechargeDelayTicks;
	private float rechargeRemainder;
	private boolean active;
	private boolean exhausted;
	private boolean depletedSprintLock;
	private boolean suppressJumpOnNextMount;
	private boolean hadRider;
	private int mountJumpSuppressTicks;

	public void tick(AbstractHorse horse) {
		int maxStaminaTicks = HorseSprintLogic.getMaxStaminaTicks(horse);
		boolean exhaustionEnabled = HorseSprintConfig.isExhaustionEnabled();
		boolean staminaEnabled = HorseSprintConfig.isStaminaEnabled();
		if (!staminaEnabled) {
			staminaTicks = maxStaminaTicks;
			exhaustionRecoveryTicks = 0;
			depletedRechargeDelayTicks = 0;
			exhausted = false;
			depletedSprintLock = false;
		}

		if (!exhaustionEnabled && exhausted) {
			exhaustionRecoveryTicks = 0;
			exhausted = false;
		}

		updateMaxStamina(maxStaminaTicks);
		if (staminaTicks > maxStaminaTicks) {
			staminaTicks = maxStaminaTicks;
		}

		Player rider = horse.getControllingPassenger() instanceof Player player ? player : null;
		boolean hasRider = rider != null;
		if (suppressJumpOnNextMount && hasRider && !hadRider) {
			mountJumpSuppressTicks = HorseSprintLogic.POST_EXHAUSTION_MOUNT_JUMP_SUPPRESS_TICKS;
			suppressJumpOnNextMount = false;
		}
		if (mountJumpSuppressTicks > 0) {
			if (hasRider) {
				((HorseSprintStateAccess) horse).sprintinghorses$clearJumpState();
				mountJumpSuppressTicks--;
			} else {
				mountJumpSuppressTicks = 0;
			}
		}

		boolean requested = HorseSprintLogic.isSprintRequested(horse, rider);
		if (!staminaEnabled) {
			depletedSprintLock = false;
		} else if (!requested) {
			depletedSprintLock = false;
		} else if (staminaTicks <= 0) {
			depletedRechargeDelayTicks = 0;
		}

		active = canBoost(horse, rider);
		if (exhausted) {
			active = false;
			recoverFromExhaustion(maxStaminaTicks);
		} else if (active && staminaEnabled) {
			rechargeRemainder = 0.0F;
			staminaTicks--;
			if (staminaTicks <= 0) {
				staminaTicks = 0;
				active = false;
				if (exhaustionEnabled) {
					exhausted = true;
					exhaustHorse(horse);
				} else {
					depletedSprintLock = true;
					depletedRechargeDelayTicks = 0;
				}
			}
		} else if (staminaEnabled && !requested && staminaTicks < maxStaminaTicks) {
			rechargeWithDelay(maxStaminaTicks);
		}

		if (!HorseSprintCompat.getWorld(horse).isClientSide() && rider instanceof ServerPlayer serverPlayer) {
			ServerPlayNetworking.send(serverPlayer, new HorseSprintStaminaPayload(getStaminaFraction(maxStaminaTicks), exhausted));
		}

		hadRider = hasRider;
	}

	public boolean isActive() {
		return active;
	}

	public boolean hasStamina() {
		return !HorseSprintConfig.isStaminaEnabled() || staminaTicks > 0;
	}

	public boolean canBoost(AbstractHorse horse, Player player) {
		return (!exhausted || !HorseSprintConfig.isExhaustionEnabled())
			&& !depletedSprintLock
			&& hasStamina()
			&& HorseSprintLogic.shouldSprint(horse, player);
	}

	public boolean isExhausted() {
		return HorseSprintConfig.isExhaustionEnabled() && exhausted;
	}

	public boolean isJumpBlocked() {
		return isExhausted() || (HorseSprintConfig.isPartialExhaustion() && staminaTicks <= 0);
	}

	public boolean isJumpSuppressed() {
		return isJumpBlocked() || mountJumpSuppressTicks > 0;
	}

	public void playExhaustedJumpFeedback(AbstractHorse horse) {
		if (!HorseSprintConfig.isExhaustionEnabled()) {
			return;
		}

		Level world = HorseSprintCompat.getWorld(horse);
		if (world.isClientSide()) {
			return;
		}

		long time = world.getGameTime();
		if (time - lastExhaustedJumpFeedbackTick < HorseSprintLogic.EXHAUSTED_JUMP_SOUND_COOLDOWN_TICKS) {
			return;
		}

		lastExhaustedJumpFeedbackTick = time;
		playAngrySound(horse);
	}

	public boolean spendJumpStamina(AbstractHorse horse) {
		if (!HorseSprintConfig.isStaminaEnabled()) {
			return true;
		}

		if (isExhausted() || !hasStamina()) {
			return false;
		}

		staminaTicks -= HorseSprintLogic.JUMP_STAMINA_COST_TICKS;
		rechargeRemainder = 0.0F;
		if (staminaTicks <= 0) {
			staminaTicks = 0;
			active = false;
			if (HorseSprintConfig.isExhaustionEnabled()) {
				exhausted = true;
				exhaustHorse(horse);
			} else {
				depletedSprintLock = true;
				depletedRechargeDelayTicks = 0;
			}
			return false;
		}

		return true;
	}

	private void rechargeWithDelay(int maxStaminaTicks) {
		if (staminaTicks <= 0 && depletedRechargeDelayTicks < HorseSprintLogic.DEPLETED_RECHARGE_DELAY_TICKS) {
			depletedRechargeDelayTicks++;
			return;
		}

		recharge(maxStaminaTicks);
	}

	private void recharge(int maxStaminaTicks) {
		if (staminaTicks < maxStaminaTicks) {
			rechargeRemainder += maxStaminaTicks / (float) HorseSprintLogic.STAMINA_RECHARGE_TICKS;
			int rechargeTicks = Math.max(1, (int) rechargeRemainder);
			staminaTicks += rechargeTicks;
			rechargeRemainder -= rechargeTicks;
		}

		if (staminaTicks >= maxStaminaTicks) {
			staminaTicks = maxStaminaTicks;
			exhausted = false;
			rechargeRemainder = 0.0F;
		}

		if (staminaTicks > 0) {
			depletedRechargeDelayTicks = 0;
		}
	}

	private void recoverFromExhaustion(int maxStaminaTicks) {
		if (exhaustionRecoveryTicks < HorseSprintLogic.EXHAUSTION_RECOVERY_TICKS) {
			exhaustionRecoveryTicks++;
		}

		staminaTicks = Math.round((exhaustionRecoveryTicks / (float) HorseSprintLogic.EXHAUSTION_RECOVERY_TICKS) * maxStaminaTicks);
		if (exhaustionRecoveryTicks >= HorseSprintLogic.EXHAUSTION_RECOVERY_TICKS) {
			staminaTicks = maxStaminaTicks;
			exhaustionRecoveryTicks = 0;
			exhausted = false;
		}
	}

	private void updateMaxStamina(int maxStaminaTicks) {
		if (maxStaminaTicks == lastMaxStaminaTicks) {
			return;
		}

		if (exhausted) {
			lastMaxStaminaTicks = maxStaminaTicks;
			return;
		}

		if (!exhausted && staminaTicks >= lastMaxStaminaTicks) {
			staminaTicks = maxStaminaTicks;
		} else {
			float fraction = staminaTicks / (float) lastMaxStaminaTicks;
			staminaTicks = Math.round(fraction * maxStaminaTicks);
		}

		lastMaxStaminaTicks = maxStaminaTicks;
	}

	private void exhaustHorse(AbstractHorse horse) {
		Level world = HorseSprintCompat.getWorld(horse);
		if (world.isClientSide()) {
			return;
		}

		exhaustionRecoveryTicks = 0;
		suppressJumpOnNextMount = true;
		mountJumpSuppressTicks = 0;
		((HorseSprintStateAccess) horse).sprintinghorses$clearJumpState();
		horse.makeMad();
		horse.ejectPassengers();
	}

	private void playAngrySound(AbstractHorse horse) {
		horse.playSound(HorseSprintLogic.getAngrySound(horse), 1.0F, 1.0F);
	}

	private float getStaminaFraction(int maxStaminaTicks) {
		if (isExhausted()) {
			return exhaustionRecoveryTicks / (float) HorseSprintLogic.EXHAUSTION_RECOVERY_TICKS;
		}

		return staminaTicks / (float) maxStaminaTicks;
	}
}
