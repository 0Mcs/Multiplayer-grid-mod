package com.multiplayergrid.mixin;

import com.multiplayergrid.CompactConfig;
import com.multiplayergrid.CompactServerLayout;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSelectionList.class)
public abstract class AbstractSelectionListMixin {
	@Inject(method = "renderListItems", at = @At("HEAD"), cancellable = true)
	private void mg$extractCompactItems(final GuiGraphics graphics, final int mouseX, final int mouseY, final float a, final CallbackInfo ci) {
		if ((Object)this instanceof ServerSelectionList list) {
			if (CompactConfig.isEnabled()) {
				CompactServerLayout.extract(list, graphics, mouseX, mouseY, a);
				ci.cancel();
			} else {
				list.setScrollAmount(list.scrollAmount());
			}
		}
	}

	@Inject(method = "contentHeight", at = @At("HEAD"), cancellable = true)
	private void mg$compactContentHeight(final CallbackInfoReturnable<Integer> cir) {
		if ((Object)this instanceof ServerSelectionList list && CompactConfig.isEnabled()) {
			cir.setReturnValue(CompactServerLayout.contentHeight(list));
		}
	}
}
