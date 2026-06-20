package net.thbtt.sprintinghorses.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.thbtt.sprintinghorses.SprintingHorses;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class HorseSprintConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve(SprintingHorses.MOD_ID + ".json");
	private static Data data = new Data();

	private HorseSprintConfig() {
	}

	public static void load() {
		if (!Files.exists(CONFIG_PATH)) {
			save();
			return;
		}

		try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
			Data loaded = GSON.fromJson(reader, Data.class);
			data = loaded != null ? loaded : new Data();
		} catch (IOException exception) {
			SprintingHorses.LOGGER.warn("Failed to load Sprinting Horses config, using defaults", exception);
			data = new Data();
		}
	}

	public static void save() {
		try {
			Files.createDirectories(CONFIG_PATH.getParent());
			try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
				GSON.toJson(data, writer);
			}
		} catch (IOException exception) {
			SprintingHorses.LOGGER.warn("Failed to save Sprinting Horses config", exception);
		}
	}

	public static boolean isExhaustionEnabled() {
		return getExhaustionMode() == ExhaustionMode.ON;
	}

	public static boolean isPartialExhaustion() {
		return getExhaustionMode() == ExhaustionMode.PARTIAL;
	}

	public static boolean isStaminaEnabled() {
		return getExhaustionMode() != ExhaustionMode.OFF;
	}

	public static ExhaustionMode getExhaustionMode() {
		if (data.exhaustionMode == null) {
			data.exhaustionMode = data.exhaustionEnabled ? ExhaustionMode.ON : ExhaustionMode.PARTIAL;
		}

		return data.exhaustionMode;
	}

	public static void setExhaustionMode(ExhaustionMode mode) {
		data.exhaustionMode = mode;
		save();
	}

	public static void cycleExhaustionMode() {
		setExhaustionMode(getExhaustionMode().next());
	}

	public enum ExhaustionMode {
		ON,
		PARTIAL,
		OFF;

		private ExhaustionMode next() {
			return switch (this) {
				case ON -> PARTIAL;
				case PARTIAL -> OFF;
				case OFF -> ON;
			};
		}
	}

	private static final class Data {
		private ExhaustionMode exhaustionMode;
		private boolean exhaustionEnabled = true;
	}
}
