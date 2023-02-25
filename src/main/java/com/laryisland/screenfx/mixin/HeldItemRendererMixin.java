package com.laryisland.screenfx.mixin;

import com.laryisland.screenfx.config.ScreenFXConfig;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
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
		float transX, transY, transZ, scale, rotX, rotY, rotZ;
		if (Item.BLOCK_ITEMS.containsValue(item.getItem())) {
			transY = ScreenFXConfig.heldBlockMainHandTranslationAxisY;
			transZ = ScreenFXConfig.heldBlockMainHandTranslationAxisZ;
			scale = ScreenFXConfig.heldBlockMainHandScale;
			rotX = ScreenFXConfig.heldBlockMainHandRotationAxisX;
			if (hand == Hand.MAIN_HAND) {
				transX = ScreenFXConfig.heldBlockMainHandTranslationAxisX;
				rotY = ScreenFXConfig.heldBlockMainHandRotationAxisY;
				rotZ = ScreenFXConfig.heldBlockMainHandRotationAxisZ;
			} else if (ScreenFXConfig.heldBlockOffhandMirrorsMainHand) {
				transX = -ScreenFXConfig.heldBlockMainHandTranslationAxisX;
				rotY = -ScreenFXConfig.heldBlockMainHandRotationAxisY;
				rotZ = -ScreenFXConfig.heldBlockMainHandRotationAxisZ;
			} else {
				transX = ScreenFXConfig.heldBlockOffhandTranslationAxisX;
				transY = ScreenFXConfig.heldBlockOffhandTranslationAxisY;
				transZ = ScreenFXConfig.heldBlockOffhandTranslationAxisZ;
				scale = ScreenFXConfig.heldBlockOffhandScale;
				rotX = ScreenFXConfig.heldBlockOffhandRotationAxisX;
				rotY = ScreenFXConfig.heldBlockOffhandRotationAxisY;
				rotZ = ScreenFXConfig.heldBlockOffhandRotationAxisZ;
			}
		} else {
			transY = ScreenFXConfig.heldItemMainHandTranslationAxisY;
			transZ = ScreenFXConfig.heldItemMainHandTranslationAxisZ;
			scale = ScreenFXConfig.heldItemMainHandScale;
			rotX = ScreenFXConfig.heldItemMainHandRotationAxisX;
			if (hand == Hand.MAIN_HAND) {
				transX = ScreenFXConfig.heldItemMainHandTranslationAxisX;
				rotY = ScreenFXConfig.heldItemMainHandRotationAxisY;
				rotZ = ScreenFXConfig.heldItemMainHandRotationAxisZ;
			} else if (ScreenFXConfig.heldItemOffhandMirrorsMainHand) {
				transX = -ScreenFXConfig.heldItemMainHandTranslationAxisX;
				rotY = -ScreenFXConfig.heldItemMainHandRotationAxisY;
				rotZ = -ScreenFXConfig.heldItemMainHandRotationAxisZ;
			} else {
				transX = ScreenFXConfig.heldItemOffhandTranslationAxisX;
				transY = ScreenFXConfig.heldItemOffhandTranslationAxisY;
				transZ = ScreenFXConfig.heldItemOffhandTranslationAxisZ;
				scale = ScreenFXConfig.heldItemOffhandScale;
				rotX = ScreenFXConfig.heldItemOffhandRotationAxisX;
				rotY = ScreenFXConfig.heldItemOffhandRotationAxisY;
				rotZ = ScreenFXConfig.heldItemOffhandRotationAxisZ;
			}
		}
		matrices.translate(transX, transY, transZ);
		matrices.scale(scale, scale, scale);
		matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotX));
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotY));
		matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotZ));
	}
}