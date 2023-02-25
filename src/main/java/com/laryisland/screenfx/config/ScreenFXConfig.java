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
	public static Comment heldItemSettings;
	@Entry(isSlider = true, min = 0f, max = 360f, precision = 1)
	public static float heldItemRotationAxisX = 0f;
	@Entry(isSlider = true, min = 0f, max = 360f, precision = 1)
	public static float heldItemRotationAxisY = 0f;
	@Entry(isSlider = true, min = 0f, max = 360f, precision = 1)
	public static float heldItemRotationAxisZ = 0f;
	@Entry(isSlider = true, min = 0f, max = 3f, precision = 10)
	public static float heldItemScaleAxisX = 0f;
	@Entry(isSlider = true, min = 0f, max = 3f, precision = 10)
	public static float heldItemScaleAxisY = 0f;
	@Entry(isSlider = true, min = 0f, max = 3f, precision = 10)
	public static float heldItemScaleAxisZ = 0f;
	@Entry(isSlider = true, min = 0f, max = 1f, precision = 100)
	public static float heldItemTranslationAxisX = 0f;
	@Entry(isSlider = true, min = 0f, max = 1f, precision = 100)
	public static float heldItemTranslationAxisY = 0f;
	@Entry(isSlider = true, min = 0f, max = 1f, precision = 100)
	public static float heldItemTranslationAxisZ = 0f;
}
