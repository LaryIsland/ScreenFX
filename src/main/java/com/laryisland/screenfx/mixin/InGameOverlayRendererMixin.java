package com.laryisland.screenfx.mixin;

import static net.minecraft.entity.effect.StatusEffects.FIRE_RESISTANCE;

import com.laryisland.screenfx.config.ScreenFXConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(InGameOverlayRenderer.class)
public abstract class InGameOverlayRendererMixin {

	@ModifyArg(
			method = "renderFireOverlay(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/util/math/MatrixStack;)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/render/VertexConsumer;color(FFFF)Lnet/minecraft/client/render/VertexConsumer;"
			),
			index = 3
	)
	private static float renderFireOverlay_opacity(float alpha) {
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		if (player != null) {
			if ((ScreenFXConfig.fireCreativeHide && player.isCreative()) || (
					ScreenFXConfig.fireResistanceHide && player.hasStatusEffect(FIRE_RESISTANCE))) {
				return 0f;
			}
		}
		return ScreenFXConfig.fireOpacity;
	}

	@ModifyArg(
			method = "renderFireOverlay(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/util/math/MatrixStack;)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V"
			),
			index = 1
	)
	private static float renderFireOverlay_translate(float y) {
		return ScreenFXConfig.firePosition - 0.8f;
	}

	@ModifyArg(
			method = "renderUnderwaterOverlay(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/util/math/MatrixStack;)V",
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V"
			),
			index = 3
	)
	private static float renderUnderwaterOverlay(float alpha) {
		return ScreenFXConfig.underwaterOpacity;
	}

	@ModifyArgs(
			method = "renderInWallOverlay(Lnet/minecraft/client/texture/Sprite;Lnet/minecraft/client/util/math/MatrixStack;)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/render/VertexConsumer;color(FFFF)Lnet/minecraft/client/render/VertexConsumer;"
			)
	)
	private static void renderInWallOverlay(Args args) {
		args.setAll(
				ScreenFXConfig.inWallBrightness,
				ScreenFXConfig.inWallBrightness,
				ScreenFXConfig.inWallBrightness,
				1f);
	}

	@Redirect(
			method = "renderOverlays",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/network/ClientPlayerEntity;isOnFire()Z"
			)
	)
	private static boolean renderFireTest(ClientPlayerEntity instance) {
		return instance.isOnFire() || ScreenFXConfig.fireTesting;
	}

	@Redirect(
			method = "renderOverlays",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSubmergedIn(Lnet/minecraft/registry/tag/TagKey;)Z"
			)
	)
	private static boolean renderUnderwaterTest(ClientPlayerEntity instance, TagKey tagKey) {
		return instance.isSubmergedIn(FluidTags.WATER) || ScreenFXConfig.underwaterTesting;
	}

	@Inject(
			method = "getInWallBlockState",
			at = @At(
					value = "RETURN"
			),
			cancellable = true
	)
	private static void renderInWallTest(PlayerEntity player, CallbackInfoReturnable<BlockState> cir) {
		if (cir.getReturnValue() == null && ScreenFXConfig.inWallTesting) {
			cir.setReturnValue(Blocks.COBBLESTONE.getDefaultState());
		}
	}
}