//? if >= 1.21.9 {
package com.laryisland.screenfx.mixin;

import static net.minecraft.world.effect.MobEffects.MINING_FATIGUE;

import com.laryisland.screenfx.config.ScreenFXConfig;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ElderGuardianParticle;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(ElderGuardianParticle.class)
public abstract class ElderGuardianParticleMixin {

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
			if (player != null && player.hasEffect(MINING_FATIGUE)) {
				return 0;
			}
		}
		return (int) (value * ScreenFXConfig.elderGuardianAnimationDuration);
	}
}
//?}