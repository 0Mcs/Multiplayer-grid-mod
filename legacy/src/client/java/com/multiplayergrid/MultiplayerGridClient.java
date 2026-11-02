package com.multiplayergrid;

import net.fabricmc.api.ClientModInitializer;

public class MultiplayerGridClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		CompactConfig.load();
	}
}
