package com.laryisland.screenfx.mixin;

import com.laryisland.screenfx.config.ScreenFXConfig;
import com.laryisland.screenfx.config.ScreenFXConfig.distortionModeEnum;
import java.awt.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	private float distortionStrength = 1f;

	@Shadow
	@Final
	MinecraftClient client;

	@ModifyArgs(
			method = "renderNausea(F)V",
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V",
					ordinal = 0
			)
	)
	private void renderDistortionOverlay(Args args) {
		float[] rgbArray = new float[3];
		try {
			Color.decode(ScreenFXConfig.distortionColour).getRGBColorComponents(rgbArray);
		} catch (NumberFormatException e) {
			rgbArray[0] = args.get(0);
			rgbArray[1] = args.get(1);
			rgbArray[2] = args.get(2);
		}
		if (ScreenFXConfig.distortionMode == distortionModeEnum.DYNAMIC) {
			distortionStrength = (1f - client.options.getDistortionEffectScale().getValue().floatValue())
					* ScreenFXConfig.distortionStrength;
			args.set(0, rgbArray[0] * distortionStrength * ScreenFXConfig.distortionOpacity);
			args.set(1, rgbArray[1] * distortionStrength * ScreenFXConfig.distortionOpacity);
			args.set(2, rgbArray[2] * distortionStrength * ScreenFXConfig.distortionOpacity);
		} else if (ScreenFXConfig.distortionMode == distortionModeEnum.FIXED) {
			distortionStrength = ScreenFXConfig.distortionStrength;
			args.set(0, rgbArray[0] * ScreenFXConfig.distortionOpacity);
			args.set(1, rgbArray[1] * ScreenFXConfig.distortionOpacity);
			args.set(2, rgbArray[2] * ScreenFXConfig.distortionOpacity);
		}
	}

	@ModifyVariable(
			method = "renderNausea(F)V",
			at = @At("STORE"),
			ordinal = 0
	)
	private double fixDistortionGrowth(double d) {
		return MathHelper.lerp(distortionStrength, 2.0, 1.0);
	}
}