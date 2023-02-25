package com.laryisland.screenfx.mixin;

import com.laryisland.screenfx.config.ScreenFXConfig;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {

	@Inject(
			method = "renderFirstPersonItem",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
					shift = Shift.BEFORE
			),
			locals = LocalCapture.CAPTURE_FAILSOFT
	)
	private void renderHeldItem_matrixManipulation(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand,
			float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices,
			VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
		if (hand == Hand.MAIN_HAND) {
			matrices.translate(ScreenFXConfig.heldItemMainHandTranslationAxisX, ScreenFXConfig.heldItemMainHandTranslationAxisY, ScreenFXConfig.heldItemMainHandTranslationAxisZ);
			matrices.scale(ScreenFXConfig.heldItemMainHandScale, ScreenFXConfig.heldItemMainHandScale, ScreenFXConfig.heldItemMainHandScale);
			matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(ScreenFXConfig.heldItemMainHandRotationAxisX));
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(ScreenFXConfig.heldItemMainHandRotationAxisY));
			matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(ScreenFXConfig.heldItemMainHandRotationAxisZ));
		} else if (ScreenFXConfig.heldItemOffhandMirrorsMainHand) {
			matrices.translate(-ScreenFXConfig.heldItemMainHandTranslationAxisX, ScreenFXConfig.heldItemMainHandTranslationAxisY, ScreenFXConfig.heldItemMainHandTranslationAxisZ);
			matrices.scale(ScreenFXConfig.heldItemMainHandScale, ScreenFXConfig.heldItemMainHandScale, ScreenFXConfig.heldItemMainHandScale);
			matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(ScreenFXConfig.heldItemMainHandRotationAxisX));
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-ScreenFXConfig.heldItemMainHandRotationAxisY));
			matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-ScreenFXConfig.heldItemMainHandRotationAxisZ));
		} else {
			matrices.translate(ScreenFXConfig.heldItemOffhandTranslationAxisX, ScreenFXConfig.heldItemOffhandTranslationAxisY, ScreenFXConfig.heldItemOffhandTranslationAxisZ);
			matrices.scale(ScreenFXConfig.heldItemOffhandScale, ScreenFXConfig.heldItemOffhandScale, ScreenFXConfig.heldItemOffhandScale);
			matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(ScreenFXConfig.heldItemOffhandRotationAxisX));
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(ScreenFXConfig.heldItemOffhandRotationAxisY));
			matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(ScreenFXConfig.heldItemOffhandRotationAxisZ));
		}
	}
}