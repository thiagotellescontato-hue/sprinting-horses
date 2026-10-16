package net.thbtt.sprintinghorses.compat;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class HorseSprintCompat {
	private HorseSprintCompat() {
	}

	public static StreamCodec<ByteBuf, Boolean> booleanCodec() {
		return ByteBufCodecs.BOOL;
	}

	public static Level getWorld(Entity entity) {
		return entity.level();
	}

	public static void playClientSound(Minecraft client, SoundEvent sound, float volume, float pitch) {
		client.getSoundManager().play(SimpleSoundInstance.forUI(sound, volume, pitch));
	}

	public static boolean isJumpPressed(LocalPlayer player) {
		return player.input.keyPresses.jump();
	}

	public static void clearJumpPressed(LocalPlayer player) {
		Input input = player.input.keyPresses;
		player.input.keyPresses = new Input(
			input.forward(),
			input.backward(),
			input.left(),
			input.right(),
			false,
			input.shift(),
			input.sprint()
		);
	}

	public static void pushHudOffset(GuiGraphicsExtractor context, int offset) {
		context.pose().pushMatrix();
		context.pose().translate(0.0F, offset);
	}

	public static void popHudOffset(GuiGraphicsExtractor context) {
		context.pose().popMatrix();
	}

	public static ItemStack getHorseArmorStack(AbstractHorse horse, int slot) {
		return slot == 0 ? horse.getItemBySlot(EquipmentSlot.BODY) : ItemStack.EMPTY;
	}

	public static void drawTexture(GuiGraphicsExtractor context, Identifier texture, int x, int y, int width, int height) {
		drawTexture(context, texture, x, y, width, height, width, height);
	}

	public static void drawTexture(GuiGraphicsExtractor context, Identifier texture, int x, int y, int width, int height, int textureWidth, int textureHeight) {
		RenderPipeline pipeline = RenderPipelines.GUI_TEXTURED;
		context.blit(pipeline, texture, x, y, 0.0F, 0.0F, width, height, textureWidth, textureHeight);
	}
}
