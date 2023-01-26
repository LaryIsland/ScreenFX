package com.laryisland.screenfx.mixin;

import static net.minecraft.entity.effect.StatusEffects.FIRE_RESISTANCE;

import com.laryisland.screenfx.config.ScreenFXConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(InGameOverlayRenderer.class)
public class InGameOverlayRendererMixin {

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
		return ScreenFXConfig.firePosition;
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
}