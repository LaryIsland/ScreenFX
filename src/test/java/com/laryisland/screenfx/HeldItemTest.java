package com.laryisland.screenfx;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.laryisland.screenfx.config.ScreenFXConfig;
import com.laryisland.screenfx.support.Handlers;
import com.laryisland.screenfx.support.ScreenFXTest;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Matrix4f;
import org.junit.jupiter.api.Test;

/** The held item transform applied for a given hand and item, compared as the resulting pose matrix. */
class HeldItemTest extends ScreenFXTest {

	@Test
	void defaultsLeaveEveryHandUntouched() {
		Matrix4f identity = new Matrix4f();
		for (InteractionHand hand : InteractionHand.values()) {
			assertPose(identity, hand, sword());
			assertPose(identity, hand, stone());
		}
	}

	@Test
	void mainHandItem() {
		ScreenFXConfig.heldItemMainHandTranslationAxisX = 0.1f;
		ScreenFXConfig.heldItemMainHandTranslationAxisY = 0.2f;
		ScreenFXConfig.heldItemMainHandTranslationAxisZ = 0.3f;
		ScreenFXConfig.heldItemMainHandScale = 1.5f;
		ScreenFXConfig.heldItemMainHandRotationAxisX = 10f;
		ScreenFXConfig.heldItemMainHandRotationAxisY = 20f;
		ScreenFXConfig.heldItemMainHandRotationAxisZ = 30f;
		assertPose(pose(0.1f, 0.2f, 0.3f, 1.5f, 10f, 20f, 30f), InteractionHand.MAIN_HAND, sword());
	}

	@Test
	void offhandItemMirrorsMainHand() {
		ScreenFXConfig.heldItemMainHandTranslationAxisX = 0.1f;
		ScreenFXConfig.heldItemMainHandTranslationAxisY = 0.2f;
		ScreenFXConfig.heldItemMainHandScale = 1.5f;
		ScreenFXConfig.heldItemMainHandRotationAxisX = 10f;
		ScreenFXConfig.heldItemMainHandRotationAxisY = 20f;
		ScreenFXConfig.heldItemMainHandRotationAxisZ = 30f;
		assertPose(pose(-0.1f, 0.2f, 0f, 1.5f, 10f, -20f, -30f), InteractionHand.OFF_HAND, sword());
	}

	@Test
	void offhandItemWithItsOwnSettings() {
		ScreenFXConfig.heldItemOffhandMirrorsMainHand = false;
		ScreenFXConfig.heldItemMainHandTranslationAxisX = 0.9f;
		ScreenFXConfig.heldItemOffhandTranslationAxisX = 0.1f;
		ScreenFXConfig.heldItemOffhandScale = 0.5f;
		ScreenFXConfig.heldItemOffhandRotationAxisZ = 45f;
		assertPose(pose(0.1f, 0f, 0f, 0.5f, 0f, 0f, 45f), InteractionHand.OFF_HAND, sword());
	}

	@Test
	void blocksUseTheBlockSettings() {
		ScreenFXConfig.heldItemMainHandTranslationAxisX = 0.9f;
		ScreenFXConfig.heldBlockMainHandTranslationAxisY = 0.25f;
		ScreenFXConfig.heldBlockMainHandScale = 0.8f;
		ScreenFXConfig.heldBlockMainHandRotationAxisY = 15f;
		assertPose(pose(0f, 0.25f, 0f, 0.8f, 0f, 15f, 0f), InteractionHand.MAIN_HAND, stone());
		assertPose(pose(0f, 0.25f, 0f, 0.8f, 0f, -15f, 0f), InteractionHand.OFF_HAND, stone());
	}

	@Test
	void offhandBlockWithItsOwnSettings() {
		ScreenFXConfig.heldBlockOffhandMirrorsMainHand = false;
		ScreenFXConfig.heldBlockMainHandTranslationAxisX = 0.9f;
		ScreenFXConfig.heldItemOffhandTranslationAxisX = 0.9f;
		ScreenFXConfig.heldBlockOffhandTranslationAxisZ = -0.2f;
		ScreenFXConfig.heldBlockOffhandRotationAxisX = 30f;
		assertPose(pose(0f, 0f, -0.2f, 1f, 30f, 0f, 0f), InteractionHand.OFF_HAND, stone());
	}

	@Test
	void uniqueItemSettingsApplyToEitherHandWithoutMirroring() {
		ScreenFXConfig.heldItemMainHandTranslationAxisX = 0.9f;
		ScreenFXConfig.uniqueHeldItemMap.put("diamond_sword", List.of(0.1f, -0.2f, 0.3f, 1.2f, 5f, 10f, 15f));
		Matrix4f expected = pose(0.1f, -0.2f, 0.3f, 1.2f, 5f, 10f, 15f);
		assertPose(expected, InteractionHand.MAIN_HAND, sword());
		assertPose(expected, InteractionHand.OFF_HAND, sword());
		assertPose(new Matrix4f(), InteractionHand.MAIN_HAND, stone());
	}

	private static ItemStack sword() {
		return new ItemStack(Items.DIAMOND_SWORD);
	}

	private static ItemStack stone() {
		return new ItemStack(Items.STONE);
	}

	private static Matrix4f pose(float x, float y, float z, float scale, float rotX, float rotY, float rotZ) {
		return new Matrix4f()
			.translate(x, y, z)
			.scale(scale)
			.rotateX((float) Math.toRadians(rotX))
			.rotateY((float) Math.toRadians(rotY))
			.rotateZ((float) Math.toRadians(rotZ));
	}

	private static void assertPose(Matrix4f expected, InteractionHand hand, ItemStack stack) {
		PoseStack poseStack = new PoseStack();
		Handlers.call("heldItem_matrixManipulation", poseStack, hand, stack);
		Matrix4f actual = poseStack.last().pose();
		assertTrue(expected.equals(actual, EPSILON), hand + " " + stack + ": expected\n" + expected + "but was\n" + actual);
	}
}
