package com.laryisland.screenfx.mixin;

import static net.minecraft.world.effect.MobEffects.DIG_SLOWDOWN;

import com.laryisland.screenfx.config.ScreenFXConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.particle.MobAppearanceParticle;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(MobAppearanceParticle.class)
public abstract class MobAppearanceParticleMixin {

	@ModifyArgs(
			method = "render(Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/client/Camera;F)V",
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V"
			)
	)
	private void elderGuardianScale(Args args) {
			args.set(0, -ScreenFXConfig.elderGuardianScale);
			args.set(1, -ScreenFXConfig.elderGuardianScale);
	}

	@ModifyConstant(
			method = "<init>(Lnet/minecraft/client/multiplayer/ClientLevel;DDD)V",
			constant = @Constant(intValue = 30)
	)
	private int elderGuardianAnimationDuration(int value) {
		if (ScreenFXConfig.elderGuardianMiningFatigueHide) {
			LocalPlayer player = Minecraft.getInstance().player;
			if (player != null && player.hasEffect(DIG_SLOWDOWN)) {
				return 0;
			}
		}
		return ScreenFXConfig.elderGuardianAnimationDuration;
	}

	@ModifyConstant(
			method = "render(Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/client/Camera;F)V",
			constant = @Constant(floatValue = 0.05f)
	)
	private float elderGuardianFadeInFadeOutOpacity(float value) {
		return ScreenFXConfig.elderGuardianFadeInFadeOutOpacity;
	}

	@ModifyConstant(
			method = "render(Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/client/Camera;F)V",
			constant = @Constant(floatValue = 0.5f)
	)
	private float elderGuardianOpacity(float g) {
		return ScreenFXConfig.elderGuardianOpacity - ScreenFXConfig.elderGuardianFadeInFadeOutOpacity;
	}

	@ModifyVariable(
			method = "render(Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/client/Camera;F)V",
			at = @At("STORE"),
			index = 4
	)
	private float elderGuardianClampOpacity(float f) {
		return Mth.clamp(f, 0f, 1f);
	}
}