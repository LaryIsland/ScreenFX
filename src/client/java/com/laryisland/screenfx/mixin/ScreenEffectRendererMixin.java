package com.laryisland.screenfx.mixin;

import static net.minecraft.world.effect.MobEffects.FIRE_RESISTANCE;

import com.laryisland.screenfx.config.ScreenFXConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ScreenEffectRenderer.class)
public abstract class ScreenEffectRendererMixin {

	@ModifyArg(
		method = "renderFire(Lnet/minecraft/client/Minecraft;Lcom/mojang/blaze3d/vertex/PoseStack;)V",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;setColor(FFFF)Lcom/mojang/blaze3d/vertex/VertexConsumer;"
		),
		index = 3
	)
	private static float renderFireOverlay_opacity(float alpha) {
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
		method = "renderFire(Lnet/minecraft/client/Minecraft;Lcom/mojang/blaze3d/vertex/PoseStack;)V",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"
		),
		index = 1
	)
	private static float renderFireOverlay_translate(float y) {
		return ScreenFXConfig.firePosition - 0.8f;
	}

	@ModifyArg(
		method = "renderWater(Lnet/minecraft/client/Minecraft;Lcom/mojang/blaze3d/vertex/PoseStack;)V",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V",
			ordinal = 0
		),
		index = 3
	)
	private static float renderUnderwaterOverlay(float alpha) {
		return ScreenFXConfig.underwaterOpacity;
	}

	@ModifyArgs(
		method = "renderTex(Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;Lcom/mojang/blaze3d/vertex/PoseStack;)V",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;setColor(FFFF)Lcom/mojang/blaze3d/vertex/VertexConsumer;"
		)
	)
	private static void renderInWallOverlay(Args args) {
		args.setAll(
			ScreenFXConfig.inWallBrightness,
			ScreenFXConfig.inWallBrightness,
			ScreenFXConfig.inWallBrightness,
			1f);
	}

	@Inject(
		method = "renderTex(Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;Lcom/mojang/blaze3d/vertex/PoseStack;)V",
		at = @At("HEAD")
	)
	private static void renderInWallOverlay_opacityBegin(TextureAtlasSprite textureAtlasSprite, PoseStack poseStack, CallbackInfo ci){
		RenderSystem.enableBlend();
		RenderSystem.setShaderColor(1f, 1f, 1f, ScreenFXConfig.inWallOpacity);
	}

	@Inject(
		method = "renderTex(Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;Lcom/mojang/blaze3d/vertex/PoseStack;)V",
		at = @At("TAIL")
	)
	private static void renderInWallOverlay_opacityEnd(TextureAtlasSprite textureAtlasSprite, PoseStack poseStack, CallbackInfo ci){
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		RenderSystem.disableBlend();
	}

	@Redirect(
		method = "renderScreenEffect",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/player/LocalPlayer;isOnFire()Z"
		)
	)
	private static boolean renderFireTest(LocalPlayer instance) {
		return instance.isOnFire() || ScreenFXConfig.fireTesting;
	}

	@Redirect(
		method = "renderScreenEffect",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/player/LocalPlayer;isEyeInFluid(Lnet/minecraft/tags/TagKey;)Z"
		)
	)
	private static boolean renderUnderwaterTest(LocalPlayer instance, TagKey<Fluid> tagKey) {
		return instance.isEyeInFluid(FluidTags.WATER) || ScreenFXConfig.underwaterTesting;
	}

	@Inject(
		method = "getViewBlockingState",
		at = @At(
			value = "RETURN"
		),
		cancellable = true
	)
	private static void renderInWallTest(Player player, CallbackInfoReturnable<BlockState> cir) {
		if (cir.getReturnValue() == null && ScreenFXConfig.inWallTesting) {
			cir.setReturnValue(Blocks.COBBLESTONE.defaultBlockState());
		}
	}
}