package com.laryisland.screenfx.mixin;

import static com.laryisland.screenfx.ScreenFX.validColour;

import com.laryisland.screenfx.config.ScreenFXConfig;
import com.laryisland.screenfx.config.ScreenFXConfig.effectModeEnum;
import java.awt.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	@Shadow
	@Final
	MinecraftClient client;
	@Shadow
	private int floatingItemTimeLeft;

	@ModifyArgs(
			method = "renderNausea(Lnet/minecraft/client/gui/DrawContext;F)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/DrawContext;setShaderColor(FFFF)V",
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
		if (ScreenFXConfig.distortionMode == effectModeEnum.DYNAMIC) {
			float distortionStrength = (1f - client.options.getDistortionEffectScale().getValue().floatValue())
					* ScreenFXConfig.distortionStrength;
			args.set(0, rgbArray[0] * distortionStrength * ScreenFXConfig.distortionOpacity);
			args.set(1, rgbArray[1] * distortionStrength * ScreenFXConfig.distortionOpacity);
			args.set(2, rgbArray[2] * distortionStrength * ScreenFXConfig.distortionOpacity);
		} else if (ScreenFXConfig.distortionMode == effectModeEnum.FIXED) {
			args.set(0, rgbArray[0] * ScreenFXConfig.distortionOpacity);
			args.set(1, rgbArray[1] * ScreenFXConfig.distortionOpacity);
			args.set(2, rgbArray[2] * ScreenFXConfig.distortionOpacity);
		}
	}

	@ModifyVariable(
			method = "renderNausea(Lnet/minecraft/client/gui/DrawContext;F)V",
			at = @At("HEAD"),
			ordinal = 0,
			argsOnly = true
	)
	private float fixDistortionGrowth(float f) {
		return ScreenFXConfig.distortionStrength;
	}

	@Inject(
			method = "renderFloatingItem(IIF)V",
			at = @At("HEAD")
	)
	private void renderFloatingItem_disable(CallbackInfo ci) {
		if (ScreenFXConfig.totemOfUndyingDisable) {
			this.floatingItemTimeLeft = 0;
		}
	}
}