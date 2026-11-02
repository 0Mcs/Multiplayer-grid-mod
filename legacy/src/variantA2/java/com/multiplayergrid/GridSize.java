package com.multiplayergrid;

public record GridSize(int columns, int rows) {
	public static GridSize fit(final int serverCount, final int maxColumns) {
		if (serverCount <= 0) {
			return new GridSize(1, 1);
		}

		int columns = Math.min(serverCount, Math.max(1, maxColumns));
		int rows = (serverCount + columns - 1) / columns;
		return new GridSize(columns, rows);
	}

	@Override
	public String toString() {
		return columns + "x" + rows;
	}
}
