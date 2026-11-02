package com.multiplayergrid.mixin;

import com.multiplayergrid.CompactConfig;
import com.multiplayergrid.CompactServerLayoutA;
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
	private void mg$renderItems(final GuiGraphics graphics, final int mouseX, final int mouseY, final float delta, final CallbackInfo ci) {
		if ((Object)this instanceof ServerSelectionList list && CompactConfig.isEnabled()) {
			CompactServerLayoutA.renderItems(list, graphics, mouseX, mouseY, delta);
			ci.cancel();
		}
	}

	@Inject(method = "getEntryAtPosition", at = @At("HEAD"), cancellable = true)
	private void mg$entryAtPosition(final double mouseX, final double mouseY, final CallbackInfoReturnable<Object> cir) {
		if ((Object)this instanceof ServerSelectionList list && CompactConfig.isEnabled()) {
			cir.setReturnValue(CompactServerLayoutA.entryAt(list, mouseX, mouseY));
		}
	}

	@Inject(method = "contentHeight", at = @At("HEAD"), cancellable = true)
	private void mg$contentHeight(final CallbackInfoReturnable<Integer> cir) {
		if ((Object)this instanceof ServerSelectionList list && CompactConfig.isEnabled()) {
			cir.setReturnValue(CompactServerLayoutA.contentHeight(list));
		}
	}
}
