package com.multiplayergrid;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;

public final class CompactServerLayout {
	public static final int CELL_WIDTH = 80;
	public static final int CELL_HEIGHT = 64;
	private static final int ROW_HEIGHT = 36;
	private static final int OUTLINE_FOCUSED = -1;
	private static final int OUTLINE_IDLE = -8355712;
	private static final int BLACK = -16777216;

	private CompactServerLayout() {
	}

	public static int contentHeight(final ServerSelectionList list) {
		List<ServerSelectionList.OnlineServerEntry> online = new ArrayList<>();
		List<ServerSelectionList.Entry> others = new ArrayList<>();
		split(list, online, others);

		int height = online.isEmpty() ? 0 : GridSize.fit(online.size(), CompactConfig.getMaxColumns()).rows() * CELL_HEIGHT;
		return height + others.size() * ROW_HEIGHT + 4;
	}

	public static void extract(final ServerSelectionList list, final GuiGraphics graphics, final int mouseX, final int mouseY, final float a) {
		List<ServerSelectionList.OnlineServerEntry> online = new ArrayList<>();
		List<ServerSelectionList.Entry> others = new ArrayList<>();
		split(list, online, others);

		GridSize grid = GridSize.fit(online.size(), CompactConfig.getMaxColumns());
		int gridLeft = list.getX() + (list.getWidth() - grid.columns() * CELL_WIDTH) / 2;
		int top = list.getY() + 2 - (int)list.scrollAmount();

		for (int i = 0; i < online.size(); i++) {
			int column = i % grid.columns();
			int row = i / grid.columns();
			place(online.get(i), gridLeft + column * CELL_WIDTH, top + row * CELL_HEIGHT, CELL_WIDTH, CELL_HEIGHT);
		}

		int y = online.isEmpty() ? top : top + grid.rows() * CELL_HEIGHT;
		for (ServerSelectionList.Entry entry : others) {
			place(entry, list.getRowLeft(), y, list.getRowWidth(), ROW_HEIGHT);
			y += ROW_HEIGHT;
		}

		for (ServerSelectionList.Entry entry : list.children()) {
			if (entry instanceof ServerSelectionList.LANHeader || entry.getHeight() == 0) {
				continue;
			}

			if (entry.getY() + entry.getHeight() < list.getY() || entry.getY() > list.getBottom()) {
				continue;
			}

			boolean hovered = list.isMouseOver(mouseX, mouseY) && entry.isMouseOver(mouseX, mouseY);
			if (list.getSelected() == entry) {
				int outline = list.isFocused() ? OUTLINE_FOCUSED : OUTLINE_IDLE;
				graphics.fill(entry.getX(), entry.getY(), entry.getX() + entry.getWidth(), entry.getY() + entry.getHeight(), outline);
				graphics.fill(entry.getX() + 1, entry.getY() + 1, entry.getX() + entry.getWidth() - 1, entry.getY() + entry.getHeight() - 1, BLACK);
			}

			entry.renderContent(graphics, mouseX, mouseY, hovered, a);
		}
	}

	private static void split(
		final ServerSelectionList list, final List<ServerSelectionList.OnlineServerEntry> online, final List<ServerSelectionList.Entry> others
	) {
		for (ServerSelectionList.Entry entry : list.children()) {
			if (entry instanceof ServerSelectionList.OnlineServerEntry onlineEntry) {
				online.add(onlineEntry);
			} else if (entry instanceof ServerSelectionList.LANHeader) {
				place(entry, 0, 0, 0, 0);
			} else {
				others.add(entry);
			}
		}
	}

	private static void place(final ServerSelectionList.Entry entry, final int x, final int y, final int width, final int height) {
		entry.setX(x);
		entry.setY(y);
		entry.setWidth(width);
		entry.setHeight(height);
	}
}
