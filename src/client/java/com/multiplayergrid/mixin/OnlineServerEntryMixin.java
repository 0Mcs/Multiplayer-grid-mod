package com.multiplayergrid.mixin;

import com.multiplayergrid.CompactCellRenderer;
import com.multiplayergrid.CompactConfig;
import com.multiplayergrid.CompactPinger;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.FaviconTexture;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.network.EventLoopGroupHolder;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerSelectionList.OnlineServerEntry.class)
public abstract class OnlineServerEntryMixin {

	@Shadow @Final private JoinMultiplayerScreen screen;
	@Shadow @Final private Minecraft minecraft;
	@Shadow @Final private ServerData serverData;
	@Shadow @Final private FaviconTexture icon;
	@Shadow private byte @Nullable [] lastIconBytes;
	@Shadow private @Nullable List<Component> onlinePlayersTooltip;
	@Shadow private @Nullable Identifier statusIcon;
	@Shadow private @Nullable Component statusIconTooltip;

	@Shadow public abstract void join();
	@Shadow public abstract void updateServerList();
	@Shadow private void refreshStatus() {
		throw new AssertionError();
	}
	@Shadow private boolean uploadServerIcon(final byte @Nullable [] serverIconBytes) {
		throw new AssertionError();
	}

	@Inject(method = "extractContent", at = @At("HEAD"), cancellable = true)
	private void mg$extractCompactContent(
		final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final boolean hovered, final float a, final CallbackInfo ci
	) {
		if (!CompactConfig.isEnabled()) {
			return;
		}

		this.mg$pingIfNeeded();
		this.mg$syncIcon();
		this.mg$updateStatusSprite();
		this.mg$drawCell(graphics, mouseX, mouseY);
		ci.cancel();
	}

	@Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
	private void mg$compactClick(final MouseButtonEvent event, final boolean doubleClick, final CallbackInfoReturnable<Boolean> cir) {
		if (!CompactConfig.isEnabled()) {
			return;
		}

		if (doubleClick) {
			this.join();
		}

		cir.setReturnValue(true);
	}

	@Unique
	private void mg$drawCell(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY) {
		ServerSelectionList.OnlineServerEntry self = (ServerSelectionList.OnlineServerEntry)(Object)this;
		String players = this.serverData.players != null
			? this.serverData.players.online() + "/" + this.serverData.players.max()
			: "?/?";
		List<Component> playerList = this.onlinePlayersTooltip != null ? this.onlinePlayersTooltip : List.of();
		CompactCellRenderer.Cell cell = new CompactCellRenderer.Cell(
			this.serverData.name,
			this.icon.textureLocation(),
			this.statusIcon,
			this.statusIconTooltip,
			players,
			playerList,
			this.serverData.motd
		);
		CompactCellRenderer.draw(graphics, this.minecraft.font, self.getX(), self.getY(), self.getWidth(), cell, mouseX, mouseY);
	}

	@Unique
	private void mg$pingIfNeeded() {
		if (this.serverData.state() != ServerData.State.INITIAL) {
			return;
		}

		this.serverData.setState(ServerData.State.PINGING);
		this.serverData.motd = CommonComponents.EMPTY;
		this.serverData.status = CommonComponents.EMPTY;
		CompactPinger.submit(() -> {
			try {
				this.screen
					.getPinger()
					.pingServer(
						this.serverData,
						() -> this.minecraft.execute(this::updateServerList),
						() -> {
							this.serverData
								.setState(
									this.serverData.protocol == SharedConstants.getCurrentVersion().protocolVersion()
										? ServerData.State.SUCCESSFUL
										: ServerData.State.INCOMPATIBLE
								);
							this.minecraft.execute(this::refreshStatus);
						},
						EventLoopGroupHolder.remote(this.minecraft.options.useNativeTransport())
					);
			} catch (UnknownHostException ignored) {
				this.serverData.setState(ServerData.State.UNREACHABLE);
				this.serverData.motd = Component.translatable("multiplayer.status.cannot_resolve").withColor(-65536);
				this.minecraft.execute(this::refreshStatus);
			} catch (Exception ignored) {
				this.serverData.setState(ServerData.State.UNREACHABLE);
				this.serverData.motd = Component.translatable("multiplayer.status.cannot_connect").withColor(-65536);
				this.minecraft.execute(this::refreshStatus);
			}
		});
	}

	@Unique
	private void mg$syncIcon() {
		byte[] currentIconBytes = this.serverData.getIconBytes();
		if (!Arrays.equals(currentIconBytes, this.lastIconBytes)) {
			if (this.uploadServerIcon(currentIconBytes)) {
				this.lastIconBytes = currentIconBytes;
			} else {
				this.serverData.setIconBytes(null);
				this.updateServerList();
			}
		}
	}

	@Unique
	private void mg$updateStatusSprite() {
		if (this.serverData.state() != ServerData.State.PINGING) {
			return;
		}

		int iconIndex = (int)(Util.getMillis() / 100L & 7L);
		if (iconIndex > 4) {
			iconIndex = 8 - iconIndex;
		}

		this.statusIcon = switch (iconIndex) {
			case 1 -> Identifier.withDefaultNamespace("server_list/pinging_2");
			case 2 -> Identifier.withDefaultNamespace("server_list/pinging_3");
			case 3 -> Identifier.withDefaultNamespace("server_list/pinging_4");
			case 4 -> Identifier.withDefaultNamespace("server_list/pinging_5");
			default -> Identifier.withDefaultNamespace("server_list/pinging_1");
		};
	}
}
