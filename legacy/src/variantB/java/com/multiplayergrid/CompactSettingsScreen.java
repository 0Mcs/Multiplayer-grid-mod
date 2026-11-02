package com.multiplayergrid;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class CompactSettingsScreen extends Screen {
	private static final int BUTTON_WIDTH = 160;
	private static final int PREVIEW_GAP = 12;
	private static final ResourceLocation ICON = ResourceLocation.withDefaultNamespace("textures/misc/unknown_server.png");
	private static final List<CompactCellRenderer.Cell> SAMPLES = List.of(
		new CompactCellRenderer.Cell(
			"Survival",
			ICON,
			ResourceLocation.withDefaultNamespace("server_list/ping_5"),
			null,
			"12/50",
			List.of(),
			Component.literal("A cozy survival world\nBuilt with friends")
		),
		new CompactCellRenderer.Cell(
			"Creative build server with a long name",
			ICON,
			ResourceLocation.withDefaultNamespace("server_list/ping_3"),
			null,
			"3/20",
			List.of(),
			Component.literal("Creative plots\nOpen 24/7")
		)
	);

	private final Screen lastScreen;
	private final boolean modMenu;
	private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 33, 33);
	private LinearLayout content;

	public CompactSettingsScreen(final Screen lastScreen, final boolean modMenu) {
		super(Component.literal("Multiplayer grid"));
		this.lastScreen = lastScreen;
		this.modMenu = modMenu;
	}

	@Override
	protected void init() {
		this.layout.addTitleHeader(this.title, this.font);
		this.content = this.layout.addToContents(LinearLayout.vertical().spacing(4));
		this.content.addChild(SpacerElement.height(CompactServerLayout.CELL_HEIGHT + PREVIEW_GAP));
		content.addChild(
			Button.builder(this.columnsLabel(), button -> {
				CompactConfig.setMaxColumns(CompactConfig.getMaxColumns() % CompactConfig.MAX_COLUMNS + 1);
				button.setMessage(this.columnsLabel());
			}).width(BUTTON_WIDTH).build()
		);
		content.addChild(toggle("Grid", CompactConfig::isEnabled, CompactConfig::setEnabled));
		content.addChild(toggle("Name", CompactConfig::isShowName, CompactConfig::setShowName));
		content.addChild(toggle("Ping", CompactConfig::isShowPing, CompactConfig::setShowPing));
		content.addChild(toggle("Players", CompactConfig::isShowPlayers, CompactConfig::setShowPlayers));
		content.addChild(toggle("Motd", CompactConfig::isShowMotd, CompactConfig::setShowMotd));
		if (this.modMenu) {
			content.addChild(toggle("Grid button", CompactConfig::isShowGridButton, CompactConfig::setShowGridButton));
		}
		this.layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, button -> this.onClose()).width(BUTTON_WIDTH).build());
		this.layout.visitWidgets(widget -> this.addRenderableWidget(widget));
		this.repositionElements();
	}

	@Override
	protected void repositionElements() {
		this.layout.arrangeElements();
	}

	@Override
	public void render(final GuiGraphics graphics, final int mouseX, final int mouseY, final float a) {
		super.render(graphics, mouseX, mouseY, a);
		int gap = 6;
		int total = SAMPLES.size() * CompactServerLayout.CELL_WIDTH + (SAMPLES.size() - 1) * gap;
		int x = this.width / 2 - total / 2;
		int top = this.content.getY();
		for (CompactCellRenderer.Cell sample : SAMPLES) {
			CompactCellRenderer.draw(graphics, this.font, x, top, CompactServerLayout.CELL_WIDTH, sample, -1, -1);
			x += CompactServerLayout.CELL_WIDTH + gap;
		}
	}

	@Override
	public void onClose() {
		CompactConfig.save();
		this.minecraft.setScreen(this.lastScreen);
	}

	private Component columnsLabel() {
		return Component.literal("Columns: " + CompactConfig.getMaxColumns());
	}

	private static Button toggle(final String name, final BooleanSupplier getter, final Consumer<Boolean> setter) {
		return Button.builder(toggleLabel(name, getter.getAsBoolean()), button -> {
			boolean next = !getter.getAsBoolean();
			setter.accept(next);
			button.setMessage(toggleLabel(name, next));
		}).width(BUTTON_WIDTH).build();
	}

	private static Component toggleLabel(final String name, final boolean value) {
		return Component.literal(name + ": " + (value ? "On" : "Off"));
	}
}
