package com.multiplayergrid;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class CompactPinger {
	private static final ExecutorService POOL = Executors.newFixedThreadPool(5, runnable -> {
		Thread thread = new Thread(runnable, "Multiplayer Grid Pinger");
		thread.setDaemon(true);
		return thread;
	});

	private CompactPinger() {
	}

	public static void submit(final Runnable task) {
		POOL.submit(task);
	}
}
