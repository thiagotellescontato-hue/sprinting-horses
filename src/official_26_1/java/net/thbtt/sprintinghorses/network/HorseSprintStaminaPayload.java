package net.thbtt.sprintinghorses.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.thbtt.sprintinghorses.SprintingHorses;
import net.thbtt.sprintinghorses.compat.HorseSprintCompat;

public record HorseSprintStaminaPayload(float stamina, boolean exhausted) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<HorseSprintStaminaPayload> ID = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(SprintingHorses.MOD_ID, "horse_sprint_stamina"));
	public static final StreamCodec<RegistryFriendlyByteBuf, HorseSprintStaminaPayload> CODEC = StreamCodec.composite(
		ByteBufCodecs.FLOAT,
		HorseSprintStaminaPayload::stamina,
		HorseSprintCompat.booleanCodec(),
		HorseSprintStaminaPayload::exhausted,
		HorseSprintStaminaPayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
