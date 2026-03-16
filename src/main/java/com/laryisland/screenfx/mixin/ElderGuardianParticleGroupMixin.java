//? if >= 1.21.9 {
package com.laryisland.screenfx.mixin;

import com.laryisland.screenfx.config.ScreenFXConfig;
import net.minecraft.client.particle.ElderGuardianParticleGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ElderGuardianParticleGroup.class)
public abstract class ElderGuardianParticleGroupMixin {

	@Mixin(targets = "net.minecraft.client.particle.ElderGuardianParticleGroup$ElderGuardianParticleRenderState")
	public static class ElderGuardianRenderStateMixin {
		@ModifyConstant(
			method = "fromParticle",
			constant = @Constant(floatValue = 0.05f)
		)
		private static float elderGuardianFadeInFadeOutOpacity(float value) {
			return ScreenFXConfig.elderGuardianFadeInFadeOutOpacity;
		}

		@ModifyConstant(
			method = "fromParticle",
			constant = @Constant(floatValue = 0.5f)
		)
		private static float elderGuardianOpacity(float g) {
			return ScreenFXConfig.elderGuardianOpacity - ScreenFXConfig.elderGuardianFadeInFadeOutOpacity;
		}

		@ModifyArgs(
			method = "fromParticle",
			at = @At(
				value = "INVOKE",
				target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V"
			)
		)
		private static void elderGuardianScale(Args args) {
			args.set(0, (float) args.get(0) * ScreenFXConfig.elderGuardianScale); // Width
			args.set(1, (float) args.get(1) * ScreenFXConfig.elderGuardianScale); // Height
			//args.set(2, (float) args.get(2) * ScreenFXConfig.elderGuardianScale); // Depth
		}
	}
}
//?}