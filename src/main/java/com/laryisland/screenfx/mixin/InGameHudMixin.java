package com.laryisland.screenfx.mixin;

import com.laryisland.screenfx.config.ScreenFXConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(InGameHud.class)
public class InGameHudMixin {

	@ModifyArg(
			method = "renderPortalOverlay(F)V",
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V"
			),
			index = 3
	)
	private float renderPortalOverlay_opacity(float alpha) {
		return ScreenFXConfig.portalOpacity;
	}

	@ModifyArg(
			method = "renderSpyglassOverlay(F)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/render/VertexConsumer;color(IIII)Lnet/minecraft/client/render/VertexConsumer;"
			),
			index = 3
	)
	private int renderSpyglassOverlay_opacity(int alpha) {
		return ScreenFXConfig.spyglassOverlayOpacity;
	}

	@Inject(
			method = "renderSpyglassOverlay(F)V",
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/blaze3d/systems/RenderSystem;defaultBlendFunc()V",
					shift = Shift.AFTER,
					remap = false
			)
	)
	private void renderSpyglassTexture_opacity(CallbackInfo ci) {
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, ScreenFXConfig.spyglassTextureOpacity);
	}

	@ModifyArgs(
			method = "renderVignetteOverlay(Lnet/minecraft/entity/Entity;)V",
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V"
			)
	)
	private void renderVignetteOverlay_opacity(Args args) {
		float[] rgbArray = new float[3];
		Color.decode(ScreenFXConfig.vignetteColour).getRGBColorComponents(rgbArray);
		args.set(0, (1f - rgbArray[0]) * ScreenFXConfig.vignetteOpacity);
		args.set(1, (1f - rgbArray[1]) * ScreenFXConfig.vignetteOpacity);
		args.set(2, (1f - rgbArray[2]) * ScreenFXConfig.vignetteOpacity);
	}
}