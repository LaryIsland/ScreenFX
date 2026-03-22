package com.laryisland.screenfx.mixin;

import static com.laryisland.screenfx.ScreenFX.validColour;

import com.laryisland.screenfx.config.ScreenFXConfig;
import com.laryisland.screenfx.config.ScreenFXConfig.effectModeEnum;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import java.awt.Color;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
//? if >= 1.21.6 {
import net.minecraft.util.ARGB;
import net.minecraft.core.registries.BuiltInRegistries;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.injection.Redirect;
//?}
//? if <= 1.21.5 {
/*import com.mojang.blaze3d.systems.RenderSystem;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
*///?}
//? if <= 1.21.1 {
/*import org.spongepowered.asm.mixin.injection.At.Shift;
*///?} else {
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import java.util.Optional;
//?}

@Mixin(Gui.class)
public class GuiMixin {

	@ModifyArg(
		method = "extractPortalOverlay(Lnet/minecraft/client/gui/GuiGraphicsExtractor;F)V",
		at = @At(
			value = "INVOKE",
//? if <=1.21.1 {
			/*target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;setColor(FFFF)V",
			ordinal = 0
		),
		index = 3
*///?} else {
			target = "Lnet/minecraft/util/ARGB;white(F)I"
		)
//?}
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
//? if <= 1.21.5 {
			/*target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;fill(Lnet/minecraft/client/renderer/RenderType;IIIIII)V"
*///?} else
			target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;fill(Lcom/mojang/blaze3d/pipeline/RenderPipeline;IIIII)V"
		),
		index = /*? if <= 1.21.5 {*/ /*6 *//*?} else */ 5
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

//? if <= 1.21.5 {
	/*@Inject(
		method = "extractSpyglassOverlay(Lnet/minecraft/client/gui/GuiGraphicsExtractor;F)V",
		at = @At(
			value = "INVOKE",
//? if <=1.21.2 {
			/^target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blit(Lnet/minecraft/resources/Identifier;IIIFFIIII)V"
^///?} else
			target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blit(Ljava/util/function/Function;Lnet/minecraft/resources/Identifier;IIFFIIII)V"
		)
	)
	private void spyglassOverlay_textureOpacity(CallbackInfo ci) {
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, ScreenFXConfig.spyglassTextureOpacity);
	}

	@Inject(
		method = "extractSpyglassOverlay(Lnet/minecraft/client/gui/GuiGraphicsExtractor;F)V",
//? if <=1.21.2 {
		/^at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blit(Lnet/minecraft/resources/Identifier;IIIFFIIII)V",
			shift = Shift.AFTER
		)
^///?} else
		at = @At("TAIL")
	)
	private void spyglassOverlay_textureOpacityReset(CallbackInfo ci) {
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	}
*///?}

//? if >= 1.21.6 {
	@Redirect(
		method = "extractSpyglassOverlay",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIFFIIII)V"
		)
	)
	private void spyglassOverlay_textureOpacity(GuiGraphicsExtractor gui, RenderPipeline pipeline, Identifier spyglassScope, int i, int j, float f, float g, int l, int m, int n, int o) {
		gui.blit(pipeline, spyglassScope, i, j, f, g, l, m, n, o, ARGB.colorFromFloat(ScreenFXConfig.spyglassTextureOpacity, 1.f, 1.f, 1.f));
	}
//?}

	@ModifyArgs(
		method = "extractVignette(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/world/entity/Entity;)V",
		at = @At(
			value = "INVOKE",
//? if <=1.21.1 {
			/*target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;setColor(FFFF)V",
*///?} else
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
				i /*? if >= 1.21.6 {*/ +1 /*?}*/,
				(1f - rgbArray[i]) * opacity
			);
		}
	}

	@ModifyArgs(
		method = "extractVignette(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/world/entity/Entity;)V",
		at = @At(
			value = "INVOKE",
//? if <=1.21.1 {
			/*target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;setColor(FFFF)V",
*///?} else
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
					i /*? if >= 1.21.6 {*/ +1 /*?}*/,
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
			target = "Lnet/minecraft/client/gui/Gui;extractTextureOverlay(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/resources/Identifier;F)V",
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
			target = "Lnet/minecraft/client/gui/Gui;extractTextureOverlay(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/resources/Identifier;F)V",
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
		index = /*? if <= 1.21.4 {*/ /*4 *//*?} else */ 6
	)
	private float portalEffectTesting(float f) {
//? if >=1.21.2 <= 1.21.4 {
		/*if (ScreenFXConfig.distortionTesting != 0f) {
			return ScreenFXConfig.distortionTesting;
		}
*///?}
		if (ScreenFXConfig.portalTesting != 0f) {
			return ScreenFXConfig.portalTesting;
		}
		return f;
	}

//? if <=1.21.1 {
	/*@ModifyExpressionValue(
		method = "extractCameraOverlays",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"
		)
	)
	private boolean pumpkinBlurTesting(boolean original) {
		return original || ScreenFXConfig.pumpkinTesting;
	}
*///?} else {
	@ModifyVariable(
		method = "extractCameraOverlays",
		at = @At("STORE")
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
				false
				//? if >= 1.21.5
				,false
				//? if >= 1.21.6 {
				, false
				, BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.SHEARS_SNIP)
				//?}
			);
		}
		return equippable;
	}
//?}

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

//? if >=1.21.2 {
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
		ordinal = 1
	)
	private float fixDistortionRadius(float f) {
		if (ScreenFXConfig.distortionMode == effectModeEnum.FIXED) {
			return 2f - ScreenFXConfig.distortionRadius;
		} else {
			return 2f - (2f - f) * ScreenFXConfig.distortionRadius;
		}
	}

//? if <= 1.21.4 {
	/*@ModifyExpressionValue(
		method = "extractCameraOverlays",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/player/LocalPlayer;hasEffect(Lnet/minecraft/core/Holder;)Z"
		)
	)
	private boolean distortionTesting_NauseaCheck(boolean original) {
		return ScreenFXConfig.distortionTesting != 0f || original;
	}

	@ModifyArg(
		method = "extractCameraOverlays",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/Gui;extractConfusionOverlay(Lnet/minecraft/client/gui/GuiGraphicsExtractor;F)V"
		)
	)
*///?}
//?}
//? if >= 1.21.5 {
	@ModifyVariable(
		method = "extractCameraOverlays",
		at = @At("STORE"),
		index = 7
	)
/*?}*//*? if >= 1.21.2 {*/
	private float distortionTesting_NauseaIntensity(float f) {
		if (ScreenFXConfig.distortionTesting != 0) {
			if (ScreenFXConfig.distortionMode == effectModeEnum.DYNAMIC) {
				return ScreenFXConfig.distortionTesting;
			} else {
				return 1f;
			}
		}
		return f;
	}
//?}
}