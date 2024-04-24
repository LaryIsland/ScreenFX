package com.laryisland.screenfx.mixin;

import static com.laryisland.screenfx.ScreenFX.validColour;

import com.laryisland.screenfx.config.ScreenFXConfig;
import com.laryisland.screenfx.config.ScreenFXConfig.effectModeEnum;
import java.awt.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.MathHelper;
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
	MinecraftClient client;
	@Shadow
	private int floatingItemTimeLeft;
	@Unique
	private static boolean singleGuardian = true;


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
		assert client.player != null;
		float distortionStrength = (1f - client.options.getDistortionEffectScale().getValue().floatValue());
		if (ScreenFXConfig.distortionMode == effectModeEnum.DYNAMIC && ScreenFXConfig.distortionTesting == 0f) {
			distortionStrength *= MathHelper.lerp(client.getTickDelta(), client.player.prevNauseaIntensity,
					client.player.nauseaIntensity);
		}
		args.set(0, rgbArray[0] * distortionStrength * ScreenFXConfig.distortionOpacity);
		args.set(1, rgbArray[1] * distortionStrength * ScreenFXConfig.distortionOpacity);
		args.set(2, rgbArray[2] * distortionStrength * ScreenFXConfig.distortionOpacity);
	}

	@ModifyVariable(
			method = "renderNausea",
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
			method = "renderFloatingItem(IIF)V",
			at = @At("HEAD")
	)
	private void renderFloatingItem_disable(CallbackInfo ci) {
		if (ScreenFXConfig.totemOfUndyingDisable) {
			this.floatingItemTimeLeft = 0;
		}
	}

	@ModifyVariable(
			method = "render",
			at = @At("STORE"),
			ordinal = 2
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
					target = "Lnet/minecraft/client/network/ClientPlayerEntity;hasStatusEffect(Lnet/minecraft/registry/entry/RegistryEntry;)Z"
			)
	)
	private boolean renderDistortionTesting_NauseaCheck(ClientPlayerEntity instance, RegistryEntry<StatusEffect> statusEffect) {
		if (ScreenFXConfig.distortionTesting != 0) {
			return true;
		}
		return instance.hasStatusEffect(statusEffect);
	}

	@Inject(
			method = "tick",
			at = @At("TAIL")
	)
	private void renderElderGuardianTesting(CallbackInfo ci) {
		if (ScreenFXConfig.elderGuardianTesting && singleGuardian && MinecraftClient.getInstance().world != null
				&& MinecraftClient.getInstance().player != null) {
			ClientPlayerEntity playerEntity = MinecraftClient.getInstance().player;
			MinecraftClient.getInstance().world.addParticle(ParticleTypes.ELDER_GUARDIAN, playerEntity.getX(),
					playerEntity.getY(), playerEntity.getZ(), 0.0, 0.0, 0.0);
			singleGuardian = false;
		}
		if (!ScreenFXConfig.elderGuardianTesting) {
			singleGuardian = true;
		}
	}
}