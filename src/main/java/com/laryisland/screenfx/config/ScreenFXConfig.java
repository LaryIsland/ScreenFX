package com.laryisland.screenfx.config;

import eu.midnightdust.lib.config.MidnightConfig;

public class ScreenFXConfig extends MidnightConfig {

	@Comment(centered = true)
	public static Comment configFireSettings;
	@Entry(name = "screenfx.midnightconfig.configFireOpacity", isSlider = true, min = 0f, max = 1f)
	public static float fireOpacity = 0.9f;
	@Entry(name = "screenfx.midnightconfig.configFirePosition", isSlider = true, min = -0.7f, max = 0.1f)
	public static float firePosition = -0.3f;
	@Entry(name = "screenfx.midnightconfig.configFireCreativeHide")
	public static boolean fireCreativeHide = false;
	@Entry(name = "screenfx.midnightconfig.configFireResistanceHide")
	public static boolean fireResistanceHide = false;
	@Comment(centered = true)
	public static Comment configUnderwaterSettings;
	@Entry(name = "screenfx.midnightconfig.configUnderwaterOpacity", isSlider = true, min = 0f, max = 1f)
	public static float underwaterOpacity = 0.1f;
	@Comment(centered = true)
	public static Comment configPortalSettings;
	@Entry(name = "screenfx.midnightconfig.configPortalOpacity", isSlider = true, min = 0f, max = 1f)
	public static float portalOpacity = 1f;
	@Comment(centered = true)
	public static Comment configSpyglassSettings;
	@Entry(name = "screenfx.midnightconfig.configSpyglassTextureOpacity", isSlider = true, min = 0f, max = 1f)
	public static float spyglassTextureOpacity = 1f;
	@Entry(name = "screenfx.midnightconfig.configSpyglassOverlayOpacity", isSlider = true, min = 0, max = 255)
	public static int spyglassOverlayOpacity = 255;
	@Comment(centered = true)
	public static Comment configVignetteSettings;
	@Entry(name = "screenfx.midnightconfig.configVignetteOpacity", isSlider = true, min = 0f, max = 1f)
	public static float vignetteOpacity = 1f;
	@Entry(name = "screenfx.midnightconfig.configVignetteColour", isColor = true)
	public static String vignetteColour = "#000000";
}
