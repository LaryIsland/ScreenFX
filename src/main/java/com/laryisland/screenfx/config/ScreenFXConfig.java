package com.laryisland.screenfx.config;

public class ScreenFXConfig extends MidnightConfig {

	public enum effectModeEnum { FIXED, DYNAMIC }

	@Comment(centered = true)
	public static Comment fireSettings;
	@Entry(isSlider = true, min = 0f, max = 1f)
	public static float fireOpacity = 0.9f;
	@Entry(isSlider = true, min = -0.7f, max = 0.1f)
	public static float firePosition = -0.3f;
	@Entry
	public static boolean fireCreativeHide = false;
	@Entry
	public static boolean fireResistanceHide = false;

	@Comment(centered = true)
	public static Comment underwaterSettings;
	@Entry(isSlider = true, min = 0f, max = 1f)
	public static float underwaterOpacity = 0.1f;

	@Comment(centered = true)
	public static Comment portalSettings;
	@Entry(isSlider = true, min = 0f, max = 1f)
	public static float portalOpacity = 1f;

	@Comment(centered = true)
	public static Comment spyglassSettings;
	@Entry(isSlider = true, min = 0f, max = 1f)
	public static float spyglassTextureOpacity = 1f;
	@Entry(isSlider = true, min = 0, max = 255)
	public static int spyglassOverlayOpacity = 255;
	@Entry(isColor = true)
	public static String spyglassOverlayColour = "#000000";

	@Comment(centered = true)
	public static Comment vignetteSettings;
	@Entry
	public static effectModeEnum vignetteMode = effectModeEnum.DYNAMIC;
	@Entry(isColor = true)
	public static String vignetteColour = "#000000";
	@Entry(isSlider = true, min = 0f, max = 1f)
	public static float vignetteOpacity = 1f;
	@Entry(isColor = true)
	public static String vignetteWorldBorderColour = "#FF0000";
	@Entry
	public static boolean vignetteWorldBorderDisable = false;

	@Comment(centered = true)
	public static Comment distortionSettings;
	@Entry
	public static effectModeEnum distortionMode = effectModeEnum.DYNAMIC;
	@Entry(isColor = true)
	public static String distortionColour = "#336633";
	@Entry(isSlider = true, min = 0f, max = 1f)
	public static float distortionOpacity = 1f;
	@Entry(isSlider = true, min = 0f, max = 1f)
	public static float distortionStrength = 1f;

	@Comment(centered = true)
	public static Comment pumpkinSettings;
	@Entry(isSlider = true, min = 0f, max = 1f)
	public static float pumpkinOpacity = 1f;

	@Comment(centered = true)
	public static Comment powderSnowSettings;
	@Entry(isSlider = true, min = 0f, max = 1f)
	public static float powderSnowOpacity = 1f;

	@Comment(centered = true)
	public static Comment inWallSettings;
	@Entry(isSlider = true, min = 0f, max = 1f)
	public static float inWallBrightness = 0.1f;

	@Comment(centered = true)
	public static Comment elderGuardianSettings;
	@Entry(isSlider = true, min = 0f, max = 1f)
	public static float elderGuardianFadeInFadeOutOpacity = 0.05f;
	@Entry(isSlider = true, min = 0f, max = 1f)
	public static float elderGuardianOpacity = 0.5f;
	@Entry(isSlider = true, min = 0f, max = 2f)
	public static float elderGuardianScale = 1f;
	@Entry(isSlider = true, min = 1, max = 120)
	public static int elderGuardianAnimationDuration = 30;
	@Entry
	public static boolean elderGuardianFixClipping = false;
	@Entry
	public static boolean elderGuardianMiningFatigueHide = false;

	@Comment(centered = true)
	public static Comment totemOfUndyingSettings;
	@Entry
	public static boolean totemOfUndyingDisable = false;

	@Comment(centered = true)
	public static Comment heldBlockSettings;
	@Entry(isSlider = true, min = -180f, max = 180f, precision = 1)
	public static float heldBlockMainHandRotationAxisX = 0f;
	@Entry(isSlider = true, min = -180f, max = 180f, precision = 1)
	public static float heldBlockMainHandRotationAxisY = 0f;
	@Entry(isSlider = true, min = -180f, max = 180f, precision = 1)
	public static float heldBlockMainHandRotationAxisZ = 0f;
	@Entry(isSlider = true, min = 0f, max = 2f)
	public static float heldBlockMainHandScale = 1f;
	@Entry(isSlider = true, min = -1f, max = 1f)
	public static float heldBlockMainHandTranslationAxisX = 0f;
	@Entry(isSlider = true, min = -1f, max = 1f)
	public static float heldBlockMainHandTranslationAxisY = 0f;
	@Entry(isSlider = true, min = -1f, max = 1f)
	public static float heldBlockMainHandTranslationAxisZ = 0f;
	@Entry
	public static boolean heldBlockOffhandMirrorsMainHand = true;
	@Entry(isSlider = true, min = -180f, max = 180f, precision = 1)
	public static float heldBlockOffhandRotationAxisX = 0f;
	@Entry(isSlider = true, min = -180f, max = 180f, precision = 1)
	public static float heldBlockOffhandRotationAxisY = 0f;
	@Entry(isSlider = true, min = -180f, max = 180f, precision = 1)
	public static float heldBlockOffhandRotationAxisZ = 0f;
	@Entry(isSlider = true, min = 0f, max = 2f)
	public static float heldBlockOffhandScale = 1f;
	@Entry(isSlider = true, min = -1f, max = 1f)
	public static float heldBlockOffhandTranslationAxisX = 0f;
	@Entry(isSlider = true, min = -1f, max = 1f)
	public static float heldBlockOffhandTranslationAxisY = 0f;
	@Entry(isSlider = true, min = -1f, max = 1f)
	public static float heldBlockOffhandTranslationAxisZ = 0f;

	@Comment(centered = true)
	public static Comment heldItemSettings;
	@Entry(isSlider = true, min = -180f, max = 180f, precision = 1)
	public static float heldItemMainHandRotationAxisX = 0f;
	@Entry(isSlider = true, min = -180f, max = 180f, precision = 1)
	public static float heldItemMainHandRotationAxisY = 0f;
	@Entry(isSlider = true, min = -180f, max = 180f, precision = 1)
	public static float heldItemMainHandRotationAxisZ = 0f;
	@Entry(isSlider = true, min = 0f, max = 2f)
	public static float heldItemMainHandScale = 1f;
	@Entry(isSlider = true, min = -1f, max = 1f)
	public static float heldItemMainHandTranslationAxisX = 0f;
	@Entry(isSlider = true, min = -1f, max = 1f)
	public static float heldItemMainHandTranslationAxisY = 0f;
	@Entry(isSlider = true, min = -1f, max = 1f)
	public static float heldItemMainHandTranslationAxisZ = 0f;
	@Entry
	public static boolean heldItemOffhandMirrorsMainHand = true;
	@Entry(isSlider = true, min = -180f, max = 180f, precision = 1)
	public static float heldItemOffhandRotationAxisX = 0f;
	@Entry(isSlider = true, min = -180f, max = 180f, precision = 1)
	public static float heldItemOffhandRotationAxisY = 0f;
	@Entry(isSlider = true, min = -180f, max = 180f, precision = 1)
	public static float heldItemOffhandRotationAxisZ = 0f;
	@Entry(isSlider = true, min = 0f, max = 2f)
	public static float heldItemOffhandScale = 1f;
	@Entry(isSlider = true, min = -1f, max = 1f)
	public static float heldItemOffhandTranslationAxisX = 0f;
	@Entry(isSlider = true, min = -1f, max = 1f)
	public static float heldItemOffhandTranslationAxisY = 0f;
	@Entry(isSlider = true, min = -1f, max = 1f)
	public static float heldItemOffhandTranslationAxisZ = 0f;
}
