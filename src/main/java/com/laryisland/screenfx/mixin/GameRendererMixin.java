//? if <= 1.21.5 {
/*package com.laryisland.screenfx.mixin;

//? if <=1.21.1 {
/^import static com.laryisland.screenfx.ScreenFX.validColour;

import com.laryisland.screenfx.config.ScreenFXConfig.effectModeEnum;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import java.awt.Color;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
^///?}
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import com.laryisland.screenfx.config.ScreenFXConfig;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	@Shadow
	private int itemActivationTicks;
//? if <=1.21.1 {

	/^@ModifyArgs(
		method = "renderConfusionOverlay(Lnet/minecraft/client/gui/GuiGraphics;F)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/GuiGraphics;setColor(FFFF)V",
			ordinal = 0
		)
	)
	private void renderDistortionOverlay(Args args) {
		float[] rgbArray = new float[3];
		if (validColour.matcher(ScreenFXConfig.distortionColour).matches()) {
			Color.decode(ScreenFXConfig.distortionColour).getRGBColorComponents(rgbArray);
		} else {
			rgbArray[0] = args.get(0);
			rgbArray[1] = args.get(1);
			rgbArray[2] = args.get(2);
		}
		float distortionStrength = (float) args.get(1) / 0.2f; // inverting mojang (0.2F * f) to get back f;
		args.set(0, rgbArray[0] * distortionStrength * ScreenFXConfig.distortionOpacity);
		args.set(1, rgbArray[1] * distortionStrength * ScreenFXConfig.distortionOpacity);
		args.set(2, rgbArray[2] * distortionStrength * ScreenFXConfig.distortionOpacity);
	}

	@ModifyVariable(
		method = "renderConfusionOverlay",
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

	@ModifyVariable(
		method = "render",
		at = @At("STORE"),
		index = 10
	)
	private float renderDistortionTesting_NauseaIntensity(float f) {
		if (ScreenFXConfig.distortionTesting != 0) {
			if (ScreenFXConfig.distortionMode == effectModeEnum.DYNAMIC) {
				return ScreenFXConfig.distortionTesting;
			} else {
				return 1f;
			}
		}
		return f;
	}

	@ModifyExpressionValue(
		method = "render",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/player/LocalPlayer;hasEffect(Lnet/minecraft/core/Holder;)Z"
		)
	)
	private boolean renderDistortionTesting_NauseaCheck(boolean original) {
		return ScreenFXConfig.distortionTesting != 0f || original;
	}
^///?}

	@Inject(
		method = "renderItemActivationAnimation",
		at = @At("HEAD")
	)
	private void renderFloatingItem_disable(CallbackInfo ci) {
		if (ScreenFXConfig.totemOfUndyingDisable) {
			this.itemActivationTicks = 0;
		}
	}
}
*///?}