package net.thbtt.sprintinghorses.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.thbtt.sprintinghorses.SprintingHorses;

public record HorseSprintInputPayload(boolean pressed) implements CustomPayload {
	public static final CustomPayload.Id<HorseSprintInputPayload> ID = new CustomPayload.Id<>(Identifier.of(SprintingHorses.MOD_ID, "horse_sprint_input"));
	public static final PacketCodec<RegistryByteBuf, HorseSprintInputPayload> CODEC = PacketCodec.tuple(
		PacketCodecs.BOOL,
		HorseSprintInputPayload::pressed,
		HorseSprintInputPayload::new
	);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
