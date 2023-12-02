package com.laryisland.screenfx.config;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ScreenFXConfig extends MidnightConfig {

	public enum effectModeEnum { FIXED, DYNAMIC }

	@Comment(category = "overlay", centered = true)
	public static Comment portalSettings;
	@Entry(category = "overlay", isSlider = true, min = 0f, max = 1f)
	public static float portalOpacity = 1f;
	@Entry(category = "overlay")
	public static boolean portalRemoveFadeIn = false;

	@Comment(category = "overlay", centered = true)
	public static Comment fireSettings;
	@Entry(category = "overlay", isSlider = true, min = 0f, max = 1f)
	public static float fireOpacity = 0.9f;
	@Entry(category = "overlay", isSlider = true, min = 0f, max = 1f)
	public static float firePosition = 0.5f;
	@Entry(category = "overlay")
	public static boolean fireCreativeHide = false;
	@Entry(category = "overlay")
	public static boolean fireResistanceHide = false;

	@Comment(category = "overlay", centered = true)
	public static Comment pumpkinSettings;
	@Entry(category = "overlay", isSlider = true, min = 0f, max = 1f)
	public static float pumpkinOpacity = 1f;

	@Comment(category = "overlay", centered = true)
	public static Comment powderSnowSettings;
	@Entry(category = "overlay", isSlider = true, min = 0f, max = 1f)
	public static float powderSnowOpacity = 1f;

	@Comment(category = "overlay", centered = true)
	public static Comment spyglassSettings;
	@Entry(category = "overlay", isSlider = true, min = 0f, max = 1f)
	public static float spyglassTextureOpacity = 1f;
	@Entry(category = "overlay", isSlider = true, min = 0f, max = 1f)
	public static float spyglassOverlayOpacity = 1;
	@Entry(category = "overlay", isColor = true)
	public static String spyglassOverlayColour = "#000000";

	@Comment(category = "overlay", centered = true)
	public static Comment vignetteSettings;
	@Entry(category = "overlay")
	public static effectModeEnum vignetteMode = effectModeEnum.DYNAMIC;
	@Entry(category = "overlay", isColor = true)
	public static String vignetteColour = "#000000";
	@Entry(category = "overlay", isSlider = true, min = 0f, max = 1f)
	public static float vignetteOpacity = 1f;
	@Entry(category = "overlay", isColor = true)
	public static String vignetteWorldBorderColour = "#FF0000";
	@Entry(category = "overlay")
	public static boolean vignetteWorldBorderDisable = false;

	@Comment(category = "overlay", centered = true)
	public static Comment distortionSettings;
	@Entry(category = "overlay")
	public static effectModeEnum distortionMode = effectModeEnum.DYNAMIC;
	@Entry(category = "overlay", isColor = true)
	public static String distortionColour = "#336633";
	@Entry(category = "overlay", isSlider = true, min = 0f, max = 1f)
	public static float distortionOpacity = 1f;
	@Entry(category = "overlay", isSlider = true, min = 0f, max = 1f)
	public static float distortionRadius = 1f;

	@Comment(category = "overlay", centered = true)
	public static Comment inWallSettings;
	@Entry(category = "overlay", isSlider = true, min = 0f, max = 1f)
	public static float inWallBrightness = 0.1f;

	@Comment(category = "overlay", centered = true)
	public static Comment underwaterSettings;
	@Entry(category = "overlay", isSlider = true, min = 0f, max = 1f)
	public static float underwaterOpacity = 0.1f;

	@Comment(category = "entity", centered = true)
	public static Comment elderGuardianSettings;
	@Entry(category = "entity", isSlider = true, min = 0f, max = 1f)
	public static float elderGuardianFadeInFadeOutOpacity = 0.05f;
	@Entry(category = "entity", isSlider = true, min = 0f, max = 1f)
	public static float elderGuardianOpacity = 0.5f;
	@Entry(category = "entity", isSlider = true, min = 0f, max = 2f)
	public static float elderGuardianScale = 1f;
	@Entry(category = "entity", isSlider = true, min = 1, max = 120)
	public static int elderGuardianAnimationDuration = 30;
	@Entry(category = "entity")
	public static boolean elderGuardianFixClipping = false;
	@Entry(category = "entity")
	public static boolean elderGuardianMiningFatigueHide = false;

	@Comment(category = "entity", centered = true)
	public static Comment totemOfUndyingSettings;
	@Entry(category = "entity")
	public static boolean totemOfUndyingDisable = false;

	@Comment(category = "heldItem", centered = true)
	public static Comment heldBlockMainHandSettings;
	@Entry(category = "heldItem", isSlider = true, min = -1f, max = 1f, name = "screenfx.heldItemTransX")
	public static float heldBlockMainHandTranslationAxisX = 0f;
	@Entry(category = "heldItem", isSlider = true, min = -1f, max = 1f, name = "screenfx.heldItemTransY")
	public static float heldBlockMainHandTranslationAxisY = 0f;
	@Entry(category = "heldItem", isSlider = true, min = -1f, max = 1f, name = "screenfx.heldItemTransZ")
	public static float heldBlockMainHandTranslationAxisZ = 0f;
	@Entry(category = "heldItem", isSlider = true, min = 0f, max = 2f, name = "screenfx.heldItemScale")
	public static float heldBlockMainHandScale = 1f;
	@Entry(category = "heldItem", isSlider = true, min = -180f, max = 180f, precision = 1, name = "screenfx.heldItemRotX")
	public static float heldBlockMainHandRotationAxisX = 0f;
	@Entry(category = "heldItem", isSlider = true, min = -180f, max = 180f, precision = 1, name = "screenfx.heldItemRotY")
	public static float heldBlockMainHandRotationAxisY = 0f;
	@Entry(category = "heldItem", isSlider = true, min = -180f, max = 180f, precision = 1, name = "screenfx.heldItemRotZ")
	public static float heldBlockMainHandRotationAxisZ = 0f;
	@Comment(category = "heldItem", centered = true)
	public static Comment heldBlockOffhandSettings;
	@Entry(category = "heldItem")
	public static boolean heldBlockOffhandMirrorsMainHand = true;
	@Entry(category = "heldItem", isSlider = true, min = -1f, max = 1f, name = "screenfx.heldItemTransX")
	public static float heldBlockOffhandTranslationAxisX = 0f;
	@Entry(category = "heldItem", isSlider = true, min = -1f, max = 1f, name = "screenfx.heldItemTransY")
	public static float heldBlockOffhandTranslationAxisY = 0f;
	@Entry(category = "heldItem", isSlider = true, min = -1f, max = 1f, name = "screenfx.heldItemTransZ")
	public static float heldBlockOffhandTranslationAxisZ = 0f;
	@Entry(category = "heldItem", isSlider = true, min = 0f, max = 2f, name = "screenfx.heldItemScale")
	public static float heldBlockOffhandScale = 1f;
	@Entry(category = "heldItem", isSlider = true, min = -180f, max = 180f, precision = 1, name = "screenfx.heldItemRotX")
	public static float heldBlockOffhandRotationAxisX = 0f;
	@Entry(category = "heldItem", isSlider = true, min = -180f, max = 180f, precision = 1, name = "screenfx.heldItemRotY")
	public static float heldBlockOffhandRotationAxisY = 0f;
	@Entry(category = "heldItem", isSlider = true, min = -180f, max = 180f, precision = 1, name = "screenfx.heldItemRotZ")
	public static float heldBlockOffhandRotationAxisZ = 0f;

	@Comment(category = "heldItem", centered = true)
	public static Comment heldItemMainHandSettings;
	@Entry(category = "heldItem", isSlider = true, min = -1f, max = 1f, name = "screenfx.heldItemTransX")
	public static float heldItemMainHandTranslationAxisX = 0f;
	@Entry(category = "heldItem", isSlider = true, min = -1f, max = 1f, name = "screenfx.heldItemTransY")
	public static float heldItemMainHandTranslationAxisY = 0f;
	@Entry(category = "heldItem", isSlider = true, min = -1f, max = 1f, name = "screenfx.heldItemTransZ")
	public static float heldItemMainHandTranslationAxisZ = 0f;
	@Entry(category = "heldItem", isSlider = true, min = 0f, max = 2f, name = "screenfx.heldItemScale")
	public static float heldItemMainHandScale = 1f;
	@Entry(category = "heldItem", isSlider = true, min = -180f, max = 180f, precision = 1, name = "screenfx.heldItemRotX")
	public static float heldItemMainHandRotationAxisX = 0f;
	@Entry(category = "heldItem", isSlider = true, min = -180f, max = 180f, precision = 1, name = "screenfx.heldItemRotY")
	public static float heldItemMainHandRotationAxisY = 0f;
	@Entry(category = "heldItem", isSlider = true, min = -180f, max = 180f, precision = 1, name = "screenfx.heldItemRotZ")
	public static float heldItemMainHandRotationAxisZ = 0f;
	@Comment(category = "heldItem", centered = true)
	public static Comment heldItemOffhandSettings;
	@Entry(category = "heldItem")
	public static boolean heldItemOffhandMirrorsMainHand = true;
	@Entry(category = "heldItem", isSlider = true, min = -1f, max = 1f, name = "screenfx.heldItemTransX")
	public static float heldItemOffhandTranslationAxisX = 0f;
	@Entry(category = "heldItem", isSlider = true, min = -1f, max = 1f, name = "screenfx.heldItemTransY")
	public static float heldItemOffhandTranslationAxisY = 0f;
	@Entry(category = "heldItem", isSlider = true, min = -1f, max = 1f, name = "screenfx.heldItemTransZ")
	public static float heldItemOffhandTranslationAxisZ = 0f;
	@Entry(category = "heldItem", isSlider = true, min = 0f, max = 2f, name = "screenfx.heldItemScale")
	public static float heldItemOffhandScale = 1f;
	@Entry(category = "heldItem", isSlider = true, min = -180f, max = 180f, precision = 1, name = "screenfx.heldItemRotX")
	public static float heldItemOffhandRotationAxisX = 0f;
	@Entry(category = "heldItem", isSlider = true, min = -180f, max = 180f, precision = 1, name = "screenfx.heldItemRotY")
	public static float heldItemOffhandRotationAxisY = 0f;
	@Entry(category = "heldItem", isSlider = true, min = -180f, max = 180f, precision = 1, name = "screenfx.heldItemRotZ")
	public static float heldItemOffhandRotationAxisZ = 0f;

	@Comment(category = "uniqueHeldItem", centered = true)
	public static Comment uniqueHeldItemSettings;
	@Entry(category = "uniqueHeldItem", map = "uniqueHeldItem", mapPosition = 0)
	public static Map<String, List<Float>> uniqueHeldItemMap = new LinkedHashMap<>();
	@Entry(category = "uniqueHeldItem", isSlider = true, min = -1f, max = 1f, map = "uniqueHeldItem", mapPosition = 1, name = "screenfx.heldItemTransX")
	public static float uniqueHeldItemTranslationAxisX = 0f;
	@Entry(category = "uniqueHeldItem", isSlider = true, min = -1f, max = 1f, map = "uniqueHeldItem", mapPosition = 2, name = "screenfx.heldItemTransY")
	public static float uniqueHeldItemTranslationAxisY = 0f;
	@Entry(category = "uniqueHeldItem", isSlider = true, min = -1f, max = 1f, map = "uniqueHeldItem", mapPosition = 3, name = "screenfx.heldItemTransZ")
	public static float uniqueHeldItemTranslationAxisZ = 0f;
	@Entry(category = "uniqueHeldItem", isSlider = true, min = 0f, max = 2f, map = "uniqueHeldItem", mapPosition = 4, name = "screenfx.heldItemScale")
	public static float uniqueHeldItemScale = 1f;
	@Entry(category = "uniqueHeldItem", isSlider = true, min = -180f, max = 180f, precision = 1, map = "uniqueHeldItem", mapPosition = 5, name = "screenfx.heldItemRotX")
	public static float uniqueHeldItemRotationAxisX = 0f;
	@Entry(category = "uniqueHeldItem", isSlider = true, min = -180f, max = 180f, precision = 1, map = "uniqueHeldItem", mapPosition = 6, name = "screenfx.heldItemRotY")
	public static float uniqueHeldItemRotationAxisY = 0f;
	@Entry(category = "uniqueHeldItem", isSlider = true, min = -180f, max = 180f, precision = 1, map = "uniqueHeldItem", mapPosition = 7, name = "screenfx.heldItemRotZ")
	public static float uniqueHeldItemRotationAxisZ = 0f;

	@Comment(category = "testing", centered = true)
	public static Comment testingSettings;
	@Entry(category = "testing", isSlider = true, min = 0f, max = 1f)
	public static float portalTesting = 0f;
	@Entry(category = "testing")
	public static boolean fireTesting = false;
	@Entry(category = "testing")
	public static boolean pumpkinTesting = false;
	@Entry(category = "testing", isSlider = true, min = 0f, max = 1f)
	public static float powerSnowTesting = 0f;
	@Entry(category = "testing")
	public static boolean spyglassTesting = false;
	@Entry(category = "testing", isSlider = true, min = 0f, max = 1f)
	public static float distortionTesting = 0f;
	@Entry(category = "testing")
	public static boolean inWallTesting = false;
	@Entry(category = "testing")
	public static boolean underwaterTesting = false;
	@Entry(category = "testing")
	public static boolean elderGuardianTesting = false;
}
