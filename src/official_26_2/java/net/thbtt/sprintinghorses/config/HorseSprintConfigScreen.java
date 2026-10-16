package net.thbtt.sprintinghorses.config;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class HorseSprintConfigScreen extends Screen {
	private final Screen parent;

	public HorseSprintConfigScreen(Screen parent) {
		super(Component.translatable("sprintinghorses.config.title"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		addRenderableWidget(Button.builder(getExhaustionButtonText(), button -> {
			HorseSprintConfig.cycleExhaustionMode();
			button.setMessage(getExhaustionButtonText());
		}).bounds(width / 2 - 100, height / 2 - 10, 200, 20).build());

		addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> onClose())
			.bounds(width / 2 - 100, height / 2 + 24, 200, 20)
			.build());
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
		extractBackground(context, mouseX, mouseY, delta);
		super.extractRenderState(context, mouseX, mouseY, delta);
		context.centeredText(font, title, width / 2, 20, 0xFFFFFF);
	}

	@Override
	public void onClose() {
		if (minecraft != null) {
			minecraft.gui.setScreen(parent);
		}
	}

	private Component getExhaustionButtonText() {
		return Component.translatable(
			"sprintinghorses.config.exhaustion",
			Component.translatable("sprintinghorses.config.exhaustion." + HorseSprintConfig.getExhaustionMode().name().toLowerCase())
		);
	}
}
