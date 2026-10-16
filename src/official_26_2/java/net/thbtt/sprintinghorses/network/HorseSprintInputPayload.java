package net.thbtt.sprintinghorses.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.thbtt.sprintinghorses.SprintingHorses;
import net.thbtt.sprintinghorses.compat.HorseSprintCompat;

public record HorseSprintInputPayload(boolean pressed) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<HorseSprintInputPayload> ID = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(SprintingHorses.MOD_ID, "horse_sprint_input"));
	public static final StreamCodec<RegistryFriendlyByteBuf, HorseSprintInputPayload> CODEC = StreamCodec.composite(
		HorseSprintCompat.booleanCodec(),
		HorseSprintInputPayload::pressed,
		HorseSprintInputPayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
