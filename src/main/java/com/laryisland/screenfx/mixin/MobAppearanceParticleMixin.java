//? if <=1.21.8 {
/*package com.laryisland.screenfx.mixin;

//? if <=1.21.4 {
/^import static net.minecraft.world.effect.MobEffects.DIG_SLOWDOWN;
^///?} else
import static net.minecraft.world.effect.MobEffects.MINING_FATIGUE;

import com.laryisland.screenfx.config.ScreenFXConfig;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.MobAppearanceParticle;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(MobAppearanceParticle.class)
public abstract class MobAppearanceParticleMixin {

	@ModifyArgs(
//? if <=1.21.3 {
		/^method = "extractRenderState(Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/client/Camera;F)V",
^///?} else
		method = "renderCustom",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V"
		)
	)
	private void elderGuardianScale(Args args) {
		args.set(0, (float) args.get(0) * ScreenFXConfig.elderGuardianScale); // Width
		args.set(1, (float) args.get(1) * ScreenFXConfig.elderGuardianScale); // Height
		//args.set(2, (float) args.get(2) * ScreenFXConfig.elderGuardianScale); // Depth
	}

	@ModifyExpressionValue(
		method = "<init>",
		at = @At(
			value = "CONSTANT",
			args = "intValue=30"
		)
	)
	private int elderGuardianAnimationDuration(int value) {
		if (ScreenFXConfig.elderGuardianMiningFatigueHide) {
			LocalPlayer player = Minecraft.getInstance().player;
			if (player != null && player.hasEffect(/^? <= 1.21.4 {^/ /^DIG_SLOWDOWN ^//^?} else {^/ MINING_FATIGUE /^?}^/)) {
				return 0;
			}
		}
		return (int) (value * ScreenFXConfig.elderGuardianAnimationDuration);
	}

	@ModifyConstant(
//? if <=1.21.3 {
		/^method = "extractRenderState(Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/client/Camera;F)V",
^///?} else
		method = "renderCustom",
		constant = @Constant(floatValue = 0.05f)
	)
	private float elderGuardianFadeInFadeOutOpacity(float value) {
		return ScreenFXConfig.elderGuardianFadeInFadeOutOpacity;
	}

	@ModifyConstant(
//? if <=1.21.3 {
		/^method = "extractRenderState(Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/client/Camera;F)V",
^///?} else
		method = "renderCustom",
		constant = @Constant(floatValue = 0.5f)
	)
	private float elderGuardianOpacity(float g) {
		return ScreenFXConfig.elderGuardianOpacity - ScreenFXConfig.elderGuardianFadeInFadeOutOpacity;
	}
}
*///?}