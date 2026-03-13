package com.laryisland.screenfx.mixin;

import static com.laryisland.screenfx.ScreenFX.validColour;

import com.laryisland.screenfx.config.ScreenFXConfig;
import com.laryisland.screenfx.config.ScreenFXConfig.effectModeEnum;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	@Shadow
	@Final
	Minecraft minecraft;
	@Shadow
	private int itemActivationTicks;
	@Unique
	private static boolean singleGuardian = true;


	@ModifyArgs(
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
		assert minecraft.player != null;
		float distortionStrength = (1f - minecraft.options.screenEffectScale().get().floatValue());
		if (ScreenFXConfig.distortionMode == effectModeEnum.DYNAMIC && ScreenFXConfig.distortionTesting == 0f) {
			distortionStrength *= Mth.lerp(
				minecraft.getTimer().getGameTimeDeltaPartialTick(false), minecraft.player.oSpinningEffectIntensity,
					minecraft.player.spinningEffectIntensity);
		}
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

	@Inject(
			method = "renderItemActivationAnimation",
			at = @At("HEAD")
	)
	private void renderFloatingItem_disable(CallbackInfo ci) {
		if (ScreenFXConfig.totemOfUndyingDisable) {
			this.itemActivationTicks = 0;
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

	@Redirect(
			method = "render",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/player/LocalPlayer;hasEffect(Lnet/minecraft/core/Holder;)Z"
			)
	)
	private boolean renderDistortionTesting_NauseaCheck(LocalPlayer instance, Holder<MobEffect> statusEffect) {
		if (ScreenFXConfig.distortionTesting != 0) {
			return true;
		}
		return instance.hasEffect(statusEffect);
	}

	@Inject(
			method = "tick",
			at = @At("TAIL")
	)
	private void renderElderGuardianTesting(CallbackInfo ci) {
		if (ScreenFXConfig.elderGuardianTesting && singleGuardian && Minecraft.getInstance().level != null
				&& Minecraft.getInstance().player != null) {
			LocalPlayer playerEntity = Minecraft.getInstance().player;
			Minecraft.getInstance().level.addParticle(ParticleTypes.ELDER_GUARDIAN, playerEntity.getX(),
					playerEntity.getY(), playerEntity.getZ(), 0.0, 0.0, 0.0);
			singleGuardian = false;
		}
		if (!ScreenFXConfig.elderGuardianTesting) {
			singleGuardian = true;
		}
	}
}