package net.thbtt.sprintinghorses.compat;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.PlayerInput;
import net.minecraft.world.World;

public final class HorseSprintCompat {
	private HorseSprintCompat() {
	}

	public static PacketCodec<ByteBuf, Boolean> booleanCodec() {
		return PacketCodecs.BOOLEAN;
	}

	public static World getWorld(Entity entity) {
		return entity.getWorld();
	}

	public static void playClientSound(MinecraftClient client, SoundEvent sound, float volume, float pitch) {
		client.getSoundManager().play(PositionedSoundInstance.master(sound, volume, pitch));
	}

	public static boolean isJumpPressed(ClientPlayerEntity player) {
		return player.input.playerInput.jump();
	}

	public static void clearJumpPressed(ClientPlayerEntity player) {
		PlayerInput input = player.input.playerInput;
		player.input.playerInput = new PlayerInput(
			input.forward(),
			input.backward(),
			input.left(),
			input.right(),
			false,
			input.sneak(),
			input.sprint()
		);
	}

	public static void pushHudOffset(DrawContext context, int offset) {
		context.getMatrices().push();
		context.getMatrices().translate(0.0F, offset, 0.0F);
	}

	public static void popHudOffset(DrawContext context) {
		context.getMatrices().pop();
	}

	public static ItemStack getHorseArmorStack(AbstractHorseEntity horse, int slot) {
		return slot == 0 ? horse.getEquippedStack(EquipmentSlot.BODY) : ItemStack.EMPTY;
	}

	public static void drawTexture(DrawContext context, Identifier texture, int x, int y, int width, int height) {
		drawTexture(context, texture, x, y, width, height, width, height);
	}

	public static void drawTexture(DrawContext context, Identifier texture, int x, int y, int width, int height, int textureWidth, int textureHeight) {
		context.drawTexture(RenderLayer::getGuiTextured, texture, x, y, 0.0F, 0.0F, width, height, textureWidth, textureHeight);
	}
}
