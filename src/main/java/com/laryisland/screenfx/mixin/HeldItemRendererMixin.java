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
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void renderHeldItem_matrixManipulation(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand,
			float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices,
			VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
		matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(ScreenFXConfig.heldItemRotationAxisX));
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(ScreenFXConfig.heldItemRotationAxisY));
		matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(ScreenFXConfig.heldItemRotationAxisZ));
		matrices.scale(ScreenFXConfig.heldItemScaleAxisX, ScreenFXConfig.heldItemScaleAxisY, ScreenFXConfig.heldItemScaleAxisZ);
		matrices.translate(ScreenFXConfig.heldItemTranslationAxisX, ScreenFXConfig.heldItemTranslationAxisY, ScreenFXConfig.heldItemTranslationAxisZ);
	}
}