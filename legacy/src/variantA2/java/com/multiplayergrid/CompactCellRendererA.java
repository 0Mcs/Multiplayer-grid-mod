package com.multiplayergrid;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.Util;
import org.jetbrains.annotations.Nullable;

public final class CompactCellRendererA {
	private static final int ICON_SIZE = 32;
	private static final int NAME_HEIGHT = 11;
	private static final int BAR_WIDTH = 10;
	private static final int BAR_HEIGHT = 8;
	private static final int BAR_GAP = 3;
	private static final int HOVER_TINT = -1601138544;
	private static final int PLAYERS_COLOR = -5592406;
	private static final int NAME_COLOR = -1;
	private static final int MOTD_WRAP_WIDTH = 180;
	private static final long MARQUEE_STEP_MS = 2500L;

	private CompactCellRendererA() {
	}

	public record Cell(
		String name,
		ResourceLocation icon,
		@Nullable ResourceLocation statusSprite,
		@Nullable Component statusTooltip,
		String players,
		List<Component> playerList,
		@Nullable Component motd
	) {
	}

	public static void draw(
		final GuiGraphics graphics,
		final Font font,
		final int x,
		final int y,
		final int width,
		final Cell cell,
		final int mouseX,
		final int mouseY,
		final Consumer<List<FormattedCharSequence>> tooltip
	) {
		int centerX = x + width / 2;
		int top = y + 2;

		if (CompactConfig.isShowName()) {
			drawName(graphics, font, cell.name(), x, top, width);
			top += NAME_HEIGHT;
		}

		int iconX = centerX - ICON_SIZE / 2;
		graphics.blit(RenderType::guiTextured, cell.icon(), iconX, top, 0.0F, 0.0F, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
		if (isInside(mouseX, mouseY, iconX, top, ICON_SIZE, ICON_SIZE)) {
			graphics.fill(iconX, top, iconX + ICON_SIZE, top + ICON_SIZE, HOVER_TINT);
			if (CompactConfig.isShowMotd() && cell.motd() != null) {
				tooltip.accept(font.split(cell.motd(), MOTD_WRAP_WIDTH));
			}
		}

		boolean showPing = CompactConfig.isShowPing();
		boolean showPlayers = CompactConfig.isShowPlayers();
		if (!showPing && !showPlayers) {
			return;
		}

		int row = top + ICON_SIZE + 2;
		int playersWidth = showPlayers ? font.width(cell.players()) : 0;
		int totalWidth = (showPing ? BAR_WIDTH : 0) + (showPing && showPlayers ? BAR_GAP : 0) + playersWidth;
		int x0 = centerX - totalWidth / 2;

		if (showPing && cell.statusSprite() != null) {
			graphics.blitSprite(RenderType::guiTextured, cell.statusSprite(), x0, row, BAR_WIDTH, BAR_HEIGHT);
			if (cell.statusTooltip() != null && isInside(mouseX, mouseY, x0, row, BAR_WIDTH, BAR_HEIGHT)) {
				tooltip.accept(font.split(cell.statusTooltip(), MOTD_WRAP_WIDTH));
			}
		}

		int playersX = x0 + (showPing ? BAR_WIDTH + BAR_GAP : 0);
		if (showPlayers) {
			graphics.drawString(font, cell.players(), playersX, row, PLAYERS_COLOR);
			if (!cell.playerList().isEmpty() && isInside(mouseX, mouseY, playersX, row, playersWidth, BAR_HEIGHT)) {
				Component list = ComponentUtils.formatList(cell.playerList(), Component.literal(", "));
				tooltip.accept(font.split(list, MOTD_WRAP_WIDTH));
			}
		}
	}

	private static void drawName(final GuiGraphics graphics, final Font font, final String name, final int x, final int y, final int width) {
		int maxWidth = width - 6;
		int nameWidth = font.width(name);
		if (nameWidth <= maxWidth) {
			graphics.drawString(font, name, x + (width - nameWidth) / 2, y, NAME_COLOR);
			return;
		}

		int overflow = nameWidth - maxWidth;
		long period = MARQUEE_STEP_MS * 2;
		long time = Util.getMillis() % period;
		double phase = time < MARQUEE_STEP_MS ? time / (double)MARQUEE_STEP_MS : (period - time) / (double)MARQUEE_STEP_MS;
		int offset = (int)Math.round(overflow * phase);
		graphics.enableScissor(x + 3, y, x + width - 3, y + 9);
		graphics.drawString(font, name, x + 3 - offset, y, NAME_COLOR);
		graphics.disableScissor();
	}

	private static boolean isInside(final int mouseX, final int mouseY, final int x, final int y, final int width, final int height) {
		return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
	}
}
