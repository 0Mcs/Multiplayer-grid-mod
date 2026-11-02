package com.multiplayergrid;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;

public final class CompactServerLayoutA {
	public static final int CELL_WIDTH = 80;
	public static final int CELL_HEIGHT = 64;
	private static final int OUTLINE_FOCUSED = -1;
	private static final int OUTLINE_IDLE = -8355712;
	private static final int BLACK = -16777216;

	private CompactServerLayoutA() {
	}

	public static int contentHeight(final ServerSelectionList list) {
		int online = online(list).size();
		return (online == 0 ? 0 : GridSize.fit(online, CompactConfig.getMaxColumns()).rows() * CELL_HEIGHT) + 4;
	}

	public static void renderItems(final ServerSelectionList list, final GuiGraphics graphics, final int mouseX, final int mouseY, final float delta) {
		List<ServerSelectionList.OnlineServerEntry> online = online(list);
		GridSize grid = GridSize.fit(online.size(), CompactConfig.getMaxColumns());
		int left = list.getX() + (list.getWidth() - grid.columns() * CELL_WIDTH) / 2;
		int top = top(list);

		for (int i = 0; i < online.size(); i++) {
			int column = i % grid.columns();
			int row = i / grid.columns();
			int x = left + column * CELL_WIDTH;
			int y = top + row * CELL_HEIGHT;
			if (y + CELL_HEIGHT < list.getY() || y > list.getBottom()) {
				continue;
			}

			ServerSelectionList.OnlineServerEntry entry = online.get(i);
			boolean hovered = list.isMouseOver(mouseX, mouseY) && inside(mouseX, mouseY, x, y, CELL_WIDTH, CELL_HEIGHT);
			if (list.getSelected() == entry) {
				int outline = list.isFocused() ? OUTLINE_FOCUSED : OUTLINE_IDLE;
				graphics.fill(x, y, x + CELL_WIDTH, y + CELL_HEIGHT, outline);
				graphics.fill(x + 1, y + 1, x + CELL_WIDTH - 1, y + CELL_HEIGHT - 1, BLACK);
			}

			entry.render(graphics, i, y, x, CELL_WIDTH, CELL_HEIGHT, mouseX, mouseY, hovered, delta);
		}
	}

	public static Object entryAt(final ServerSelectionList list, final double mouseX, final double mouseY) {
		List<ServerSelectionList.OnlineServerEntry> online = online(list);
		GridSize grid = GridSize.fit(online.size(), CompactConfig.getMaxColumns());
		int left = list.getX() + (list.getWidth() - grid.columns() * CELL_WIDTH) / 2;
		int top = top(list);

		for (int i = 0; i < online.size(); i++) {
			int column = i % grid.columns();
			int row = i / grid.columns();
			int x = left + column * CELL_WIDTH;
			int y = top + row * CELL_HEIGHT;
			if (mouseX >= x && mouseX < x + CELL_WIDTH && mouseY >= y && mouseY < y + CELL_HEIGHT) {
				return online.get(i);
			}
		}

		return null;
	}

	private static List<ServerSelectionList.OnlineServerEntry> online(final ServerSelectionList list) {
		List<ServerSelectionList.OnlineServerEntry> result = new ArrayList<>();
		for (ServerSelectionList.Entry entry : list.children()) {
			if (entry instanceof ServerSelectionList.OnlineServerEntry onlineEntry) {
				result.add(onlineEntry);
			}
		}

		return result;
	}

	private static int top(final ServerSelectionList list) {
		return list.children().isEmpty() ? list.getY() + 4 : ((com.multiplayergrid.mixin.AbstractSelectionListInvoker)(Object)list).mg$rowTop(0);
	}

	private static boolean inside(final int mouseX, final int mouseY, final int x, final int y, final int width, final int height) {
		return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
	}
}
