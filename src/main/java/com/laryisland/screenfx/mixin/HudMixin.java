//? if > 26.1.2 {
package com.laryisland.screenfx.mixin;

import static com.laryisland.screenfx.ScreenFX.validColour;

import com.laryisland.screenfx.config.ScreenFXConfig;
import com.laryisland.screenfx.config.ScreenFXConfig.effectModeEnum;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import java.awt.Color;
import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import net.minecraft.util.ARGB;
import net.minecraft.core.registries.BuiltInRegistries;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import java.util.Optional;

@Mixin(Hud.class)
public class HudMixin {

	@ModifyArg(
		method = "extractPortalOverlay(Lnet/minecraft/client/gui/GuiGraphicsExtractor;F)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/util/ARGB;white(F)I"
		)
	)
	private float portalOverlay(float nauseaStrength) {
		if (ScreenFXConfig.portalRemoveFadeIn) {
			return ScreenFXConfig.portalOpacity;
		}
		return nauseaStrength * ScreenFXConfig.portalOpacity;
	}

	@ModifyArg(
		method = "extractSpyglassOverlay",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;fill(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;IIIII)V"
		),
		index = 5
	)
	private int spyglassOverlay_opacity(int color) {
		if (ScreenFXConfig.spyglassOverlayColour.length() == 7
			&& validColour.matcher(ScreenFXConfig.spyglassOverlayColour).matches()) {
			return Long.valueOf(Integer.toHexString((int) (ScreenFXConfig.spyglassOverlayOpacity * 255))
				+ ScreenFXConfig.spyglassOverlayColour.substring(1), 16).intValue();
		}
		return Long.valueOf(Integer.toHexString((int) (ScreenFXConfig.spyglassOverlayOpacity * 255)) + "000000", 16)
			.intValue();
	}

	@Redirect(
		method = "extractSpyglassOverlay",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blit(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIFFIIII)V"
		)
	)
	private void spyglassOverlay_textureOpacity(GuiGraphicsExtractor gui, RenderPipeline renderPipeline, Identifier texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
		gui.blit(renderPipeline, texture,
			x, y, u, v, width, height,
			textureWidth, textureHeight, ARGB.colorFromFloat(ScreenFXConfig.spyglassTextureOpacity, 1.f, 1.f, 1.f));
	}

	@ModifyArgs(
		method = "extractVignette(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/world/entity/Entity;)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/util/ARGB;colorFromFloat(FFFF)I",
			ordinal = 1
		)
	)
	private void vignetteOverlay(Args args) {
		float[] rgbArray = new float[3];
		if (validColour.matcher(ScreenFXConfig.vignetteColour).matches()) {
			Color.decode(ScreenFXConfig.vignetteColour).getRGBColorComponents(rgbArray);
		}
		float opacity = ScreenFXConfig.vignetteOpacity;
		if (ScreenFXConfig.vignetteMode == effectModeEnum.DYNAMIC) {
			float vignetteOpacity = args.get(1);
			opacity *= vignetteOpacity;
		}
		for (int i = 0; i < 3; ++i) {
			args.set(
				i + 1,
				(1f - rgbArray[i]) * opacity
			);
		}
	}

	@ModifyArgs(
		method = "extractVignette(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/world/entity/Entity;)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/util/ARGB;colorFromFloat(FFFF)I",
			ordinal = 0
		)
	)
	private void vignetteOverlay_worldBorder(Args args) {
		if (!ScreenFXConfig.vignetteWorldBorderDisable) {
			float[] rgbArray = new float[3];
			if (validColour.matcher(ScreenFXConfig.vignetteWorldBorderColour).matches()) {
				Color.decode(ScreenFXConfig.vignetteWorldBorderColour).getRGBColorComponents(rgbArray);
			}
			float opacity = ScreenFXConfig.vignetteOpacity;
			if (ScreenFXConfig.vignetteMode == effectModeEnum.DYNAMIC) {
				float worldBorderStrength = args.get(2);
				opacity *= worldBorderStrength;
			}
			for (int i = 0; i < 3; ++i) {
				args.set(
					i + 1,
					(1f - rgbArray[i]) * opacity
				);
			}
		} else {
			vignetteOverlay(args);
		}
	}

	@ModifyArg(
		method = "extractCameraOverlays",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/Hud;extractTextureOverlay(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/resources/Identifier;F)V",
			ordinal = 0
		)
	)
	private float pumpkinBlurOverlay(float opacity) {
		return ScreenFXConfig.pumpkinOpacity;
	}

	@ModifyArg(
		method = "extractCameraOverlays",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/Hud;extractTextureOverlay(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/resources/Identifier;F)V",
			ordinal = 1
		)
	)
	private float powderSnowOverlay(float freezingScale) {
		if (ScreenFXConfig.powerSnowTesting != 0f) {
			return ScreenFXConfig.powderSnowOpacity * ScreenFXConfig.powerSnowTesting;
		}
		return ScreenFXConfig.powderSnowOpacity * freezingScale;
	}

	@ModifyVariable(
		method = "extractCameraOverlays",
		at = @At("STORE"),
		name = "portalIntensity"
	)
	private float portalEffectTesting(float portalIntensity) {
		if (ScreenFXConfig.portalTesting != 0f) {
			return ScreenFXConfig.portalTesting;
		}
		return portalIntensity;
	}

	@ModifyVariable(
		method = "extractCameraOverlays",
		at = @At("STORE"),
		name = "equippable"
	)
	private Equippable pumpkinBlurTesting(Equippable equippable) {
		if (ScreenFXConfig.pumpkinTesting) {
			equippable = new Equippable(
				EquipmentSlot.HEAD,
				SoundEvents.ARMOR_EQUIP_GENERIC,
				Optional.empty(),
				Optional.of(Identifier.withDefaultNamespace("misc/pumpkinblur")),
				Optional.empty(),
				false,
				false,
				false,
				false,
				false,
				BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.SHEARS_SNIP)
			);
		}
		return equippable;
	}

	@ModifyExpressionValue(
		method = "extractCameraOverlays",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/player/LocalPlayer;getTicksFrozen()I"
		)
	)
	private int powderSnowTesting(int original) {
		return ScreenFXConfig.powerSnowTesting != 0f ? 1 : original;
	}

	@ModifyExpressionValue(
		method = "extractCameraOverlays",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/player/LocalPlayer;isScoping()Z"
		)
	)
	private boolean spyglassTesting(boolean original) {
		return original || ScreenFXConfig.spyglassTesting;
	}

	@ModifyArgs(
		method = "extractConfusionOverlay(Lnet/minecraft/client/gui/GuiGraphicsExtractor;F)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/util/ARGB;colorFromFloat(FFFF)I"
		)
	)
	private void distortionOverlay(Args args) {
		float[] rgbArray = new float[3];
		if (validColour.matcher(ScreenFXConfig.distortionColour).matches()) {
			Color.decode(ScreenFXConfig.distortionColour).getRGBColorComponents(rgbArray);
		} else {
			rgbArray[0] = args.get(0);
			rgbArray[1] = args.get(1);
			rgbArray[2] = args.get(2);
		}
		float distortionStrength = (float) args.get(1) / 0.2f; // inverting mojang (0.2F * f) to get back f;
		args.set(1, rgbArray[0] * distortionStrength * ScreenFXConfig.distortionOpacity);
		args.set(2, rgbArray[1] * distortionStrength * ScreenFXConfig.distortionOpacity);
		args.set(3, rgbArray[2] * distortionStrength * ScreenFXConfig.distortionOpacity);
	}

	@ModifyVariable(
		method = "extractConfusionOverlay",
		at = @At("STORE"),
		name = "size"
	)
	private float fixDistortionRadius(float size) {
		if (ScreenFXConfig.distortionMode == effectModeEnum.FIXED) {
			return 2f - ScreenFXConfig.distortionRadius;
		} else {
			return 2f - (2f - size) * ScreenFXConfig.distortionRadius;
		}
	}

	@ModifyVariable(
		method = "extractCameraOverlays",
		at = @At("STORE"),
		name = "nauseaIntensity"
	)
	private float distortionTesting_NauseaIntensity(float nauseaIntensity) {
		if (ScreenFXConfig.distortionTesting != 0) {
			if (ScreenFXConfig.distortionMode == effectModeEnum.DYNAMIC) {
				return ScreenFXConfig.distortionTesting;
			} else {
				return 1f;
			}
		}
		return nauseaIntensity;
	}
}
//?}