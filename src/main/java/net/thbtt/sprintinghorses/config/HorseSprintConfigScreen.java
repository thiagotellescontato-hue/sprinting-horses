package net.thbtt.sprintinghorses.config;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class HorseSprintConfigScreen extends Screen {
	private final Screen parent;

	public HorseSprintConfigScreen(Screen parent) {
		super(Text.translatable("sprintinghorses.config.title"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		addDrawableChild(ButtonWidget.builder(getExhaustionButtonText(), button -> {
			HorseSprintConfig.cycleExhaustionMode();
			button.setMessage(getExhaustionButtonText());
		}).dimensions(width / 2 - 100, height / 2 - 10, 200, 20).build());

		addDrawableChild(ButtonWidget.builder(Text.translatable("gui.done"), button -> close())
			.dimensions(width / 2 - 100, height / 2 + 24, 200, 20)
			.build());
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		renderBackground(context, mouseX, mouseY, delta);
		super.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 20, 0xFFFFFF);
	}

	@Override
	public void close() {
		if (client != null) {
			client.setScreen(parent);
		}
	}

	private Text getExhaustionButtonText() {
		return Text.translatable(
			"sprintinghorses.config.exhaustion",
			Text.translatable("sprintinghorses.config.exhaustion." + HorseSprintConfig.getExhaustionMode().name().toLowerCase())
		);
	}
}
