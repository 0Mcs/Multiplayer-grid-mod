package com.multiplayergrid;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import net.fabricmc.loader.api.FabricLoader;

public final class CompactConfig {
	private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("multiplayer-grid.properties");
	public static final int MIN_COLUMNS = 1;
	public static final int MAX_COLUMNS = 4;

	private static int maxColumns = 2;
	private static boolean showName = true;
	private static boolean showPing = true;
	private static boolean showPlayers = true;
	private static boolean showMotd = true;
	private static boolean enabled = true;
	private static boolean showGridButton = true;

	private CompactConfig() {
	}

	public static void load() {
		if (!Files.exists(PATH)) {
			return;
		}

		Properties properties = new Properties();
		try (InputStream in = Files.newInputStream(PATH)) {
			properties.load(in);
		} catch (IOException ignored) {
			return;
		}

		maxColumns = clamp(parseInt(properties.getProperty("maxColumns"), maxColumns));
		showName = Boolean.parseBoolean(properties.getProperty("showName", String.valueOf(showName)));
		showPing = Boolean.parseBoolean(properties.getProperty("showPing", String.valueOf(showPing)));
		showPlayers = Boolean.parseBoolean(properties.getProperty("showPlayers", String.valueOf(showPlayers)));
		showMotd = Boolean.parseBoolean(properties.getProperty("showMotd", String.valueOf(showMotd)));
		enabled = Boolean.parseBoolean(properties.getProperty("enabled", String.valueOf(enabled)));
		showGridButton = Boolean.parseBoolean(properties.getProperty("showGridButton", String.valueOf(showGridButton)));
	}

	public static void save() {
		Properties properties = new Properties();
		properties.setProperty("maxColumns", String.valueOf(maxColumns));
		properties.setProperty("showName", String.valueOf(showName));
		properties.setProperty("showPing", String.valueOf(showPing));
		properties.setProperty("showPlayers", String.valueOf(showPlayers));
		properties.setProperty("showMotd", String.valueOf(showMotd));
		properties.setProperty("enabled", String.valueOf(enabled));
		properties.setProperty("showGridButton", String.valueOf(showGridButton));

		try {
			Files.createDirectories(PATH.getParent());
			try (OutputStream out = Files.newOutputStream(PATH)) {
				properties.store(out, "Multiplayer grid");
			}
		} catch (IOException ignored) {
		}
	}

	public static int getMaxColumns() {
		return maxColumns;
	}

	public static void setMaxColumns(final int value) {
		maxColumns = clamp(value);
	}

	public static boolean isShowName() {
		return showName;
	}

	public static void setShowName(final boolean value) {
		showName = value;
	}

	public static boolean isShowPing() {
		return showPing;
	}

	public static void setShowPing(final boolean value) {
		showPing = value;
	}

	public static boolean isShowPlayers() {
		return showPlayers;
	}

	public static void setShowPlayers(final boolean value) {
		showPlayers = value;
	}

	public static boolean isShowMotd() {
		return showMotd;
	}

	public static void setShowMotd(final boolean value) {
		showMotd = value;
	}

	public static boolean isEnabled() {
		return enabled;
	}

	public static void setEnabled(final boolean value) {
		enabled = value;
	}

	public static boolean isShowGridButton() {
		return showGridButton;
	}

	public static void setShowGridButton(final boolean value) {
		showGridButton = value;
	}

	private static int clamp(final int value) {
		return Math.max(MIN_COLUMNS, Math.min(MAX_COLUMNS, value));
	}

	private static int parseInt(final String raw, final int fallback) {
		try {
			return Integer.parseInt(raw);
		} catch (NumberFormatException | NullPointerException e) {
			return fallback;
		}
	}
}
