package com.laryisland.screenfx.mixin;

import static net.minecraft.world.effect.MobEffects.FIRE_RESISTANCE;

import com.laryisland.screenfx.config.ScreenFXConfig;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
//? if != 1.21.4 != 1.21.5
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? if >= 1.21.6
import org.spongepowered.asm.mixin.Shadow;
//? if <=1.21.3 {
/*import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
*///?}

@Mixin(ScreenEffectRenderer.class)
public abstract class ScreenEffectRendererMixin {

//? if >= 1.21.6 {
	@Shadow
	private int itemActivationTicks;
//?}

	@ModifyArg(
		method = "renderFire",
		at = @At(
			value = "INVOKE",
//? if >= 1.21 {
			target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;setColor(FFFF)Lcom/mojang/blaze3d/vertex/VertexConsumer;"
//?} else
			//target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;color(FFFF)Lcom/mojang/blaze3d/vertex/VertexConsumer;"
		),
		index = 3
	)
	private static float fireOverlay_opacity(float alpha) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player != null) {
			if ((ScreenFXConfig.fireCreativeHide && player.isCreative()) || (
				ScreenFXConfig.fireResistanceHide && player.hasEffect(FIRE_RESISTANCE))) {
				return 0f;
			}
		}
		return ScreenFXConfig.fireOpacity;
	}

	@ModifyArg(
		method = "renderFire",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"
		),
		index = 1
	)
	private static float fireOverlay_translate(float f) {
		return f + ScreenFXConfig.firePosition - 0.5f; // default value is -0.3f
	}

	@ModifyArg(
		method = "renderWater",
		at = @At(
			value = "INVOKE",
//? if <=1.21.3 {
			/*target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V"
*///?} else
			target = "Lnet/minecraft/util/ARGB;colorFromFloat(FFFF)I"
		),
		index = /*? if <=1.21.3 {*/ /*3 *//*?} else*/ 0
	)
	private static float underwaterOverlay(float alpha) {
		return ScreenFXConfig.underwaterOpacity;
	}

	@ModifyArgs(
		method = "renderTex",
		at = @At(
			value = "INVOKE",
//? if <1.21.1 {
			/*target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;color(FFFF)Lcom/mojang/blaze3d/vertex/VertexConsumer;"
*///?} elif <=1.21.3 {
			/*target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;setColor(FFFF)Lcom/mojang/blaze3d/vertex/VertexConsumer;"
*///?} else
			target = "Lnet/minecraft/util/ARGB;colorFromFloat(FFFF)I"
		)
	)
	private static void inWallOverlay(Args args) {
		args.setAll(
//? if >=1.21.4
			ScreenFXConfig.inWallOpacity,
			ScreenFXConfig.inWallBrightness,
			ScreenFXConfig.inWallBrightness,
			ScreenFXConfig.inWallBrightness
//? if <=1.21.3
			//,ScreenFXConfig.inWallOpacity
		);
	}

//? if <=1.21.3 {
	/*@Inject(
		method = "renderTex",
		at = @At("HEAD")
	)
	private static void inWallOverlay_opacityBegin(TextureAtlasSprite atlas, PoseStack pose, CallbackInfo ci) {
		RenderSystem.enableBlend();
	}

	@Inject(
		method = "renderTex",
		at = @At("TAIL")
	)
	private static void inWallOverlay_opacityEnd(TextureAtlasSprite atlas, PoseStack pose, CallbackInfo ci) {
		RenderSystem.disableBlend();
	}
*///?}

	@ModifyExpressionValue(
		method = "renderScreenEffect",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/player/LocalPlayer;isOnFire()Z"
		)
	)
	private static boolean fireTest(boolean original) {
		return original || ScreenFXConfig.fireTesting;
	}

	@ModifyExpressionValue(
		method = "renderScreenEffect",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/player/LocalPlayer;isEyeInFluid(Lnet/minecraft/tags/TagKey;)Z"
		)
	)
	private static boolean underwaterTest(boolean original) {
		return original || ScreenFXConfig.underwaterTesting;
	}

	@Inject(
		method = "getViewBlockingState",
		at = @At("RETURN"),
		cancellable = true
	)
	private static void inWallTest(Player player, CallbackInfoReturnable<BlockState> cir) {
		if (cir.getReturnValue() == null && ScreenFXConfig.inWallTesting) {
			cir.setReturnValue(Blocks.COBBLESTONE.defaultBlockState());
		}
	}

//? if >= 1.21.6 {
	@Inject(
		method = "renderItemActivationAnimation",
		at = @At("HEAD")
	)
	private void floatingItem_disable(CallbackInfo ci) {
		if (ScreenFXConfig.totemOfUndyingDisable) {
			this.itemActivationTicks = 0;
		}
	}
//?}
}