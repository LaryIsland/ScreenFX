package com.laryisland.screenfx.mixin;

import static com.laryisland.screenfx.ScreenFX.validColour;

import com.laryisland.screenfx.config.ScreenFXConfig;
import com.laryisland.screenfx.config.ScreenFXConfig.effectModeEnum;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(InGameHud.class)
public class InGameHudMixin {

	private static float opacity = 1f;
	@Shadow
	public float vignetteDarkness;

	@ModifyArg(
			method = "renderPortalOverlay(Lnet/minecraft/client/util/math/MatrixStack;F)V",
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V",
					ordinal = 0
			),
			index = 3
	)
	private float renderPortalOverlay(float alpha) {
		return ScreenFXConfig.portalOpacity;
	}

	@ModifyArg(
			method = "renderSpyglassOverlay(Lnet/minecraft/client/util/math/MatrixStack;F)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/hud/InGameHud;fill(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V"
			),
			index = 6
	)
	private int renderSpyglassOverlay_opacity(int color) {
		if (ScreenFXConfig.spyglassOverlayColour.length() == 7
				&& validColour.matcher(ScreenFXConfig.spyglassOverlayColour).matches()) {
			return Long.valueOf(Integer.toHexString(ScreenFXConfig.spyglassOverlayOpacity)
					+ ScreenFXConfig.spyglassOverlayColour.substring(1), 16).intValue();
		}
		return Long.valueOf(Integer.toHexString(ScreenFXConfig.spyglassOverlayOpacity) + "000000", 16).intValue();
	}

	@Inject(
			method = "renderSpyglassOverlay(Lnet/minecraft/client/util/math/MatrixStack;F)V",
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/util/Identifier;)V",
					shift = Shift.BEFORE,
					remap = false
			)
	)
	private void renderSpyglassOverlay_textureOpacity(CallbackInfo ci) {
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, ScreenFXConfig.spyglassTextureOpacity);
	}

	@Inject(
			method = "renderSpyglassOverlay(Lnet/minecraft/client/util/math/MatrixStack;F)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIFFIIII)V",
					shift = Shift.AFTER,
					remap = false
			)
	)
	private void renderSpyglassOverlay_textureOpacityReset(CallbackInfo ci) {
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@ModifyArgs(
			method = "renderVignetteOverlay(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/entity/Entity;)V",
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V",
					ordinal = 1
			)
	)
	private void renderVignetteOverlay(Args args) {
		float[] rgbArray = new float[3];
		if (validColour.matcher(ScreenFXConfig.vignetteColour).matches()) {
			Color.decode(ScreenFXConfig.vignetteColour).getRGBColorComponents(rgbArray);
		} else {
			rgbArray[0] = 0f;
			rgbArray[1] = 0f;
			rgbArray[2] = 0f;
		}
		opacity = ScreenFXConfig.vignetteOpacity;
		if (ScreenFXConfig.vignetteMode == effectModeEnum.DYNAMIC) {
			opacity *= MathHelper.clamp(this.vignetteDarkness, 0.0F, 1.0F);
		}
		args.set(0, (1f - rgbArray[0]) * opacity);
		args.set(1, (1f - rgbArray[1]) * opacity);
		args.set(2, (1f - rgbArray[2]) * opacity);
	}

	@ModifyArgs(
			method = "renderVignetteOverlay(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/entity/Entity;)V",
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V",
					ordinal = 0
			)
	)
	private void renderVignetteOverlay_worldBorder(Args args) {
		if (!ScreenFXConfig.vignetteWorldBorderDisable) {
			float[] rgbArray = new float[3];
			if (validColour.matcher(ScreenFXConfig.vignetteWorldBorderColour).matches()) {
				Color.decode(ScreenFXConfig.vignetteWorldBorderColour).getRGBColorComponents(rgbArray);
			} else {
				rgbArray[0] = 0f;
				rgbArray[1] = 1f;
				rgbArray[2] = 1f;
			}
			opacity = ScreenFXConfig.vignetteOpacity;
			if (ScreenFXConfig.vignetteMode == effectModeEnum.DYNAMIC) {
				float worldBorderStrength = args.get(1);
				opacity *= worldBorderStrength;
			}
			args.set(0, (1f - rgbArray[0]) * opacity);
			args.set(1, (1f - rgbArray[1]) * opacity);
			args.set(2, (1f - rgbArray[2]) * opacity);
		} else {
			renderVignetteOverlay(args);
		}
	}

	@ModifyArg(
			method = "render(Lnet/minecraft/client/util/math/MatrixStack;F)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/hud/InGameHud;renderOverlay(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/Identifier;F)V",
					ordinal = 0
			)
	)
	private float renderPumpkinBlurOverlay(float opacity) {
		return ScreenFXConfig.pumpkinOpacity;
	}

	@ModifyArg(
			method = "render(Lnet/minecraft/client/util/math/MatrixStack;F)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/hud/InGameHud;renderOverlay(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/Identifier;F)V",
					ordinal = 1
			)
	)
	private float renderPowderSnowOverlay(float freezingScale) {
		return ScreenFXConfig.powderSnowOpacity * freezingScale;
	}
}