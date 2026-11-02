package com.multiplayergrid.mixin;

import com.multiplayergrid.CompactConfig;
import com.multiplayergrid.CompactSettingsScreen;
import java.util.List;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JoinMultiplayerScreen.class)
public abstract class JoinMultiplayerScreenMixin extends Screen {
	@Unique private static final int BUTTON_WIDTH = 74;
	@Unique private static final int BUTTON_HEIGHT = 20;
	@Unique private static final int BUTTON_GAP = 4;
	@Unique private Button mg$grid;

	protected JoinMultiplayerScreenMixin() {
		super(CommonComponents.EMPTY);
	}

	@Inject(method = "init", at = @At("TAIL"))
	private void mg$addPencil(final CallbackInfo ci) {
		this.mg$grid = this.addRenderableWidget(
			Button.builder(Component.translatableWithFallback("multiplayer_grid.button", "Grid"), button -> this.minecraft.setScreen(new CompactSettingsScreen(this, false)))
				.size(BUTTON_WIDTH, BUTTON_HEIGHT)
				.build()
		);
		this.mg$placeGrid();
	}

	@Inject(method = "repositionElements", at = @At("TAIL"))
	private void mg$repositionPencil(final CallbackInfo ci) {
		if (this.mg$grid != null) {
			this.mg$placeGrid();
		}
	}

	@Unique
	private void mg$placeGrid() {
		this.mg$grid.visible = CompactConfig.isShowGridButton();
		if (!this.mg$grid.visible) {
			return;
		}

		Button edit = null;
		Button delete = null;
		Button refresh = null;
		Button back = null;
		for (GuiEventListener child : this.children()) {
			if (!(child instanceof Button button)) {
				continue;
			}

			String key = mg$translationKey(button.getMessage());
			if ("selectServer.edit".equals(key)) {
				edit = button;
			} else if ("selectServer.delete".equals(key)) {
				delete = button;
			} else if ("selectServer.refresh".equals(key)) {
				refresh = button;
			} else if ("gui.back".equals(key)) {
				back = button;
			}
		}

		if (edit != null && delete != null && refresh != null && back != null) {
			int shift = (BUTTON_WIDTH + BUTTON_GAP) / 2;
			edit.setX(edit.getX() - shift);
			for (Button button : List.of(delete, refresh, back)) {
				button.setX(button.getX() + shift);
			}

			this.mg$grid.setX(edit.getX() + edit.getWidth() + BUTTON_GAP);
			this.mg$grid.setY(edit.getY());
			return;
		}

		if (refresh != null) {
			this.mg$grid.setX(refresh.getX() + refresh.getWidth() + BUTTON_GAP);
			this.mg$grid.setY(refresh.getY());
		}
	}

	@Unique
	private static String mg$translationKey(final Component message) {
		return message.getContents() instanceof TranslatableContents translatable ? translatable.getKey() : "";
	}
}
