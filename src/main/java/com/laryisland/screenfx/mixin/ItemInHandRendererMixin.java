package com.laryisland.screenfx.mixin;

import com.laryisland.screenfx.config.ScreenFXConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.List;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? if <= 1.21.8 {
/*import net.minecraft.client.renderer.MultiBufferSource;
 *///?} else
import net.minecraft.client.renderer.SubmitNodeCollector;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {

	@Inject(
		method = "renderArmWithItem",
		at = @At(
			value = "INVOKE",
//? if <= 1.21.4 {
			/*target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
*///?} else if <= 1.21.8 {
			/*target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
*///?} else
			target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V",
			ordinal = 1
		)
	)

	private void heldItem_matrixManipulation(
		AbstractClientPlayer player,
		float tickDelta,
		float pitch,
		InteractionHand hand,
		float swingProgress,
		ItemStack itemStack,
		float equipProgress,
		PoseStack matrices,
//? if <= 1.21.8 {
		/*MultiBufferSource vertexConsumers,
*///?} else
		SubmitNodeCollector submitNodeCollector,
		int light,
		CallbackInfo ci
	) {
		float transX, transY, transZ, scale, rotX, rotY, rotZ;
		if (ScreenFXConfig.uniqueHeldItemMap.containsKey(itemStack.getItem().toString()/*? if >= 1.21 { */.substring(10) /*?}*/)) {
			List<Float> specificItemConfig = ScreenFXConfig.uniqueHeldItemMap.get(itemStack.getItem().toString()/*? if >= 1.21 { */.substring(10) /*?}*/);
			transX = specificItemConfig.get(0);
			transY = specificItemConfig.get(1);
			transZ = specificItemConfig.get(2);
			scale = specificItemConfig.get(3);
			rotX = specificItemConfig.get(4);
			rotY = specificItemConfig.get(5);
			rotZ = specificItemConfig.get(6);
		} else if (Item.BY_BLOCK.containsValue(itemStack.getItem())) {
			transY = ScreenFXConfig.heldBlockMainHandTranslationAxisY;
			transZ = ScreenFXConfig.heldBlockMainHandTranslationAxisZ;
			scale = ScreenFXConfig.heldBlockMainHandScale;
			rotX = ScreenFXConfig.heldBlockMainHandRotationAxisX;
			if (hand == InteractionHand.MAIN_HAND) {
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
			if (hand == InteractionHand.MAIN_HAND) {
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
		matrices.mulPose(Axis.XP.rotationDegrees(rotX));
		matrices.mulPose(Axis.YP.rotationDegrees(rotY));
		matrices.mulPose(Axis.ZP.rotationDegrees(rotZ));
	}
}