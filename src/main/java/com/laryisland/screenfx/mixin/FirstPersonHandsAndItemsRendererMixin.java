//? if > 26.2 {
package com.laryisland.screenfx.mixin;

import com.laryisland.screenfx.config.ScreenFXConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.List;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FirstPersonHandsAndItemsRenderer.class)
public class FirstPersonHandsAndItemsRendererMixin {

	@Inject(
		method = "submitArmWithItem",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V",
			ordinal = 1
		)
	)

	private void heldItem_matrixManipulation(
		PlayerRenderState playerState,
		FirstPersonHandsAndItemsRenderState state,
		float tickDelta,
		float pitch,
		InteractionHand hand,
		float swingProgress,
		ItemStack itemStack,
		float equipProgress,
		PoseStack matrices,
		SubmitNodeCollector submitNodeCollector,
		int lightCoords,
		CallbackInfo ci
	) {
		float transX, transY, transZ, scale, rotX, rotY, rotZ;
		if (ScreenFXConfig.uniqueHeldItemMap.containsKey(itemStack.getItem().toString().substring(10))) {
			List<Float> specificItemConfig = ScreenFXConfig.uniqueHeldItemMap.get(itemStack.getItem().toString().substring(10));
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
		matrices.rotate(Axis.XP.rotationDegrees(rotX));
		matrices.rotate(Axis.YP.rotationDegrees(rotY));
		matrices.rotate(Axis.ZP.rotationDegrees(rotZ));
	}
}
//?}