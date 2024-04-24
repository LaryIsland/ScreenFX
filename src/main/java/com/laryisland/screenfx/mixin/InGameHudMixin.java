package com.laryisland.screenfx.mixin;

import static com.laryisland.screenfx.ScreenFX.validColour;

import com.laryisland.screenfx.config.ScreenFXConfig;
import com.laryisland.screenfx.config.ScreenFXConfig.effectModeEnum;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(InGameHud.class)
public class InGameHudMixin {

	@Unique
	private static float opacity = 1f;
	@Shadow
	public float vignetteDarkness;

	@ModifyArg(
			method = "renderPortalOverlay(Lnet/minecraft/client/gui/DrawContext;F)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/DrawContext;setShaderColor(FFFF)V",
					ordinal = 0
			),
			index = 3
	)
	private float renderPortalOverlay(float nauseaStrength) {
		if (ScreenFXConfig.portalRemoveFadeIn) {
			return ScreenFXConfig.portalOpacity;
		}
		return nauseaStrength * ScreenFXConfig.portalOpacity;
	}

	@ModifyArg(
			method = "renderSpyglassOverlay(Lnet/minecraft/client/gui/DrawContext;F)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/DrawContext;fill(Lnet/minecraft/client/render/RenderLayer;IIIIII)V"
			),
			index = 6
	)
	private int renderSpyglassOverlay_opacity(int color) {
		if (ScreenFXConfig.spyglassOverlayColour.length() == 7
				&& validColour.matcher(ScreenFXConfig.spyglassOverlayColour).matches()) {
			return Long.valueOf(Integer.toHexString((int) (ScreenFXConfig.spyglassOverlayOpacity * 255))
					+ ScreenFXConfig.spyglassOverlayColour.substring(1), 16).intValue();
		}
		return Long.valueOf(Integer.toHexString((int) (ScreenFXConfig.spyglassOverlayOpacity * 255)) + "000000", 16)
				.intValue();
	}

	@Inject(
			method = "renderSpyglassOverlay(Lnet/minecraft/client/gui/DrawContext;F)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIFFIIII)V",
					shift = Shift.BEFORE
			)
	)
	private void renderSpyglassOverlay_textureOpacity(CallbackInfo ci) {
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, ScreenFXConfig.spyglassTextureOpacity);
	}

	@Inject(
			method = "renderSpyglassOverlay(Lnet/minecraft/client/gui/DrawContext;F)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIFFIIII)V",
					shift = Shift.AFTER
			)
	)
	private void renderSpyglassOverlay_textureOpacityReset(CallbackInfo ci) {
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@ModifyArgs(
			method = "renderVignetteOverlay(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/entity/Entity;)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/DrawContext;setShaderColor(FFFF)V",
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
			method = "renderVignetteOverlay(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/entity/Entity;)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/DrawContext;setShaderColor(FFFF)V",
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
			method = "renderMiscOverlays(Lnet/minecraft/client/gui/DrawContext;F)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/hud/InGameHud;renderOverlay(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/util/Identifier;F)V",
					ordinal = 0
			)
	)
	private float renderPumpkinBlurOverlay(float opacity) {
		return ScreenFXConfig.pumpkinOpacity;
	}

	@ModifyArg(
			method = "renderMiscOverlays(Lnet/minecraft/client/gui/DrawContext;F)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/hud/InGameHud;renderOverlay(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/util/Identifier;F)V",
					ordinal = 1
			)
	)
	private float renderPowderSnowOverlay(float freezingScale) {
		if (ScreenFXConfig.powerSnowTesting != 0f) {
			return ScreenFXConfig.powderSnowOpacity * ScreenFXConfig.powerSnowTesting;
		}
		return ScreenFXConfig.powderSnowOpacity * freezingScale;
	}

	@ModifyVariable(
			method = "renderMiscOverlays(Lnet/minecraft/client/gui/DrawContext;F)V",
			at = @At("STORE"),
			ordinal = 1
	)
	private float renderPortalTesting(float f) {
		if (ScreenFXConfig.portalTesting != 0f) {
			return ScreenFXConfig.portalTesting;
		}
		return f;
	}

	@Redirect(
			method = "renderMiscOverlays(Lnet/minecraft/client/gui/DrawContext;F)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"
			)
	)
	private boolean renderPumpkinBlurTesting(ItemStack instance, Item item) {
		if (ScreenFXConfig.pumpkinTesting) {
			return true;
		}
		return instance.isOf(item);
	}

	@Redirect(
			method = "renderMiscOverlays(Lnet/minecraft/client/gui/DrawContext;F)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/network/ClientPlayerEntity;getFrozenTicks()I"
			)
	)
	private int renderPowerSnowTesting(ClientPlayerEntity instance) {
		if (ScreenFXConfig.powerSnowTesting != 0f) {
			return 1;
		}
		return instance.getFrozenTicks();
	}

	@Redirect(
			method = "renderMiscOverlays(Lnet/minecraft/client/gui/DrawContext;F)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingSpyglass()Z"
			)
	)
	private boolean renderSpyglassTesting(ClientPlayerEntity instance) {
		if (ScreenFXConfig.spyglassTesting) {
			return true;
		}
		return instance.isUsingSpyglass();
	}
}