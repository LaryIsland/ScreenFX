package com.laryisland.screenfx.config;

public class ScreenFXConfig extends MidnightConfig {

	public enum effectModeEnum { FIXED, DYNAMIC }

	@Comment(category = "overlay", centered = true)
	public static Comment portalSettings;
	@Entry(category = "overlay", isSlider = true, min = 0f, max = 1f)
	public static float portalOpacity = 1f;

	@Comment(category = "overlay", centered = true)
	public static Comment fireSettings;
	@Entry(category = "overlay", isSlider = true, min = 0f, max = 1f)
	public static float fireOpacity = 0.9f;
	@Entry(category = "overlay", isSlider = true, min = -0.7f, max = 0.1f)
	public static float firePosition = -0.3f;
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
	@Entry(category = "overlay", isSlider = true, min = 0, max = 255)
	public static int spyglassOverlayOpacity = 255;
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
	public static float distortionStrength = 1f;

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
	@Entry(category = "heldItem", isSlider = true, min = -180f, max = 180f, precision = 1)
	public static float heldBlockMainHandRotationAxisX = 0f;
	@Entry(category = "heldItem", isSlider = true, min = -180f, max = 180f, precision = 1)
	public static float heldBlockMainHandRotationAxisY = 0f;
	@Entry(category = "heldItem", isSlider = true, min = -180f, max = 180f, precision = 1)
	public static float heldBlockMainHandRotationAxisZ = 0f;
	@Entry(category = "heldItem", isSlider = true, min = 0f, max = 2f)
	public static float heldBlockMainHandScale = 1f;
	@Entry(category = "heldItem", isSlider = true, min = -1f, max = 1f)
	public static float heldBlockMainHandTranslationAxisX = 0f;
	@Entry(category = "heldItem", isSlider = true, min = -1f, max = 1f)
	public static float heldBlockMainHandTranslationAxisY = 0f;
	@Entry(category = "heldItem", isSlider = true, min = -1f, max = 1f)
	public static float heldBlockMainHandTranslationAxisZ = 0f;
	@Comment(category = "heldItem", centered = true)
	public static Comment heldBlockOffhandSettings;
	@Entry(category = "heldItem")
	public static boolean heldBlockOffhandMirrorsMainHand = true;
	@Entry(category = "heldItem", isSlider = true, min = -180f, max = 180f, precision = 1)
	public static float heldBlockOffhandRotationAxisX = 0f;
	@Entry(category = "heldItem", isSlider = true, min = -180f, max = 180f, precision = 1)
	public static float heldBlockOffhandRotationAxisY = 0f;
	@Entry(category = "heldItem", isSlider = true, min = -180f, max = 180f, precision = 1)
	public static float heldBlockOffhandRotationAxisZ = 0f;
	@Entry(category = "heldItem", isSlider = true, min = 0f, max = 2f)
	public static float heldBlockOffhandScale = 1f;
	@Entry(category = "heldItem", isSlider = true, min = -1f, max = 1f)
	public static float heldBlockOffhandTranslationAxisX = 0f;
	@Entry(category = "heldItem", isSlider = true, min = -1f, max = 1f)
	public static float heldBlockOffhandTranslationAxisY = 0f;
	@Entry(category = "heldItem", isSlider = true, min = -1f, max = 1f)
	public static float heldBlockOffhandTranslationAxisZ = 0f;

	@Comment(category = "heldItem", centered = true)
	public static Comment heldItemMainHandSettings;
	@Entry(category = "heldItem", isSlider = true, min = -180f, max = 180f, precision = 1)
	public static float heldItemMainHandRotationAxisX = 0f;
	@Entry(category = "heldItem", isSlider = true, min = -180f, max = 180f, precision = 1)
	public static float heldItemMainHandRotationAxisY = 0f;
	@Entry(category = "heldItem", isSlider = true, min = -180f, max = 180f, precision = 1)
	public static float heldItemMainHandRotationAxisZ = 0f;
	@Entry(category = "heldItem", isSlider = true, min = 0f, max = 2f)
	public static float heldItemMainHandScale = 1f;
	@Entry(category = "heldItem", isSlider = true, min = -1f, max = 1f)
	public static float heldItemMainHandTranslationAxisX = 0f;
	@Entry(category = "heldItem", isSlider = true, min = -1f, max = 1f)
	public static float heldItemMainHandTranslationAxisY = 0f;
	@Entry(category = "heldItem", isSlider = true, min = -1f, max = 1f)
	public static float heldItemMainHandTranslationAxisZ = 0f;
	@Comment(category = "heldItem", centered = true)
	public static Comment heldItemOffhandSettings;
	@Entry(category = "heldItem")
	public static boolean heldItemOffhandMirrorsMainHand = true;
	@Entry(category = "heldItem", isSlider = true, min = -180f, max = 180f, precision = 1)
	public static float heldItemOffhandRotationAxisX = 0f;
	@Entry(category = "heldItem", isSlider = true, min = -180f, max = 180f, precision = 1)
	public static float heldItemOffhandRotationAxisY = 0f;
	@Entry(category = "heldItem", isSlider = true, min = -180f, max = 180f, precision = 1)
	public static float heldItemOffhandRotationAxisZ = 0f;
	@Entry(category = "heldItem", isSlider = true, min = 0f, max = 2f)
	public static float heldItemOffhandScale = 1f;
	@Entry(category = "heldItem", isSlider = true, min = -1f, max = 1f)
	public static float heldItemOffhandTranslationAxisX = 0f;
	@Entry(category = "heldItem", isSlider = true, min = -1f, max = 1f)
	public static float heldItemOffhandTranslationAxisY = 0f;
	@Entry(category = "heldItem", isSlider = true, min = -1f, max = 1f)
	public static float heldItemOffhandTranslationAxisZ = 0f;
}
