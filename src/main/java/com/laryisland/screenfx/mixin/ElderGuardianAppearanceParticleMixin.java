package com.laryisland.screenfx.mixin;

import static net.minecraft.entity.effect.StatusEffects.MINING_FATIGUE;

import com.laryisland.screenfx.config.ScreenFXConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.particle.ElderGuardianAppearanceParticle;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ElderGuardianAppearanceParticle.class)
public abstract class ElderGuardianAppearanceParticleMixin {

	@ModifyArgs(
			method = "buildGeometry(Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/client/render/Camera;F)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/util/math/MatrixStack;scale(FFF)V"
			)
	)
	private void elderGuardianScale(Args args) {
		if (ScreenFXConfig.elderGuardianFixClipping) {
			final float CAMERA_PROXIMITY_MULTIPLIER = 0.12f;
			args.set(0, -ScreenFXConfig.elderGuardianScale * CAMERA_PROXIMITY_MULTIPLIER);
			args.set(1, -ScreenFXConfig.elderGuardianScale * CAMERA_PROXIMITY_MULTIPLIER);
			args.set(2, CAMERA_PROXIMITY_MULTIPLIER);
		} else {
			args.set(0, -ScreenFXConfig.elderGuardianScale);
			args.set(1, -ScreenFXConfig.elderGuardianScale);
		}
	}

	@ModifyConstant(
			method = "<init>(Lnet/minecraft/client/world/ClientWorld;DDD)V",
			constant = @Constant(intValue = 30)
	)
	private int elderGuardianAnimationDuration(int value) {
		if (ScreenFXConfig.elderGuardianMiningFatigueHide) {
			ClientPlayerEntity player = MinecraftClient.getInstance().player;
			if (player != null && player.hasStatusEffect(MINING_FATIGUE)) {
				return 0;
			}
		}
		return ScreenFXConfig.elderGuardianAnimationDuration;
	}

	@ModifyConstant(
			method = "buildGeometry(Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/client/render/Camera;F)V",
			constant = @Constant(floatValue = 0.05f)
	)
	private float elderGuardianFadeInFadeOutOpacity(float value) {
		return ScreenFXConfig.elderGuardianFadeInFadeOutOpacity;
	}

	@ModifyConstant(
			method = "buildGeometry(Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/client/render/Camera;F)V",
			constant = @Constant(floatValue = 0.5f)
	)
	private float elderGuardianOpacity(float g) {
		return ScreenFXConfig.elderGuardianOpacity - ScreenFXConfig.elderGuardianFadeInFadeOutOpacity;
	}

	@ModifyVariable(
			method = "buildGeometry(Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/client/render/Camera;F)V",
			at = @At("STORE"),
			ordinal = 1
	)
	private float elderGuardianClampOpacity(float f) {
		return MathHelper.clamp(f, 0f, 1f);
	}
}