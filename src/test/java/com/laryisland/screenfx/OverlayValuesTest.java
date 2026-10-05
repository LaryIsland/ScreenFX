package com.laryisland.screenfx;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.laryisland.screenfx.config.ScreenFXConfig;
import com.laryisland.screenfx.config.ScreenFXConfig.effectModeEnum;
import com.laryisland.screenfx.support.ScreenFXTest;
import com.laryisland.screenfx.support.Vanilla;
import com.laryisland.screenfx.support.Vanilla.Colour;
import org.junit.jupiter.api.Test;

/** Each setting must produce the same drawn value on every supported Minecraft version. */
class OverlayValuesTest extends ScreenFXTest {

	@Test
	void fireOpacityAndPosition() {
		ScreenFXConfig.fireOpacity = 0.4f;
		ScreenFXConfig.firePosition = 0.8f;
		assertEquals(0.4f, Vanilla.fireAlpha(), CHANNEL);
		assertEquals(0f, Vanilla.fireY(), EPSILON);
	}

	@Test
	void underwaterOpacity() {
		ScreenFXConfig.underwaterOpacity = 0.35f;
		assertEquals(0.35f, Vanilla.underwaterAlpha(0.6f), CHANNEL);
	}

	@Test
	void inWallBrightnessAndOpacity() {
		ScreenFXConfig.inWallBrightness = 0.6f;
		ScreenFXConfig.inWallOpacity = 0.5f;
		assertColour(new Colour(0.5f, 0.6f, 0.6f, 0.6f), Vanilla.inWall());
	}

	@Test
	void portalOpacityScalesWithStrength() {
		ScreenFXConfig.portalOpacity = 0.5f;
		assertEquals(0.4f, Vanilla.portalAlpha(0.8f), EPSILON);
	}

	@Test
	void portalWithoutFadeInIsAlwaysAtFullOpacity() {
		ScreenFXConfig.portalOpacity = 0.5f;
		ScreenFXConfig.portalRemoveFadeIn = true;
		assertEquals(0.5f, Vanilla.portalAlpha(0.1f), EPSILON);
	}

	@Test
	void spyglassBarColourAndOpacity() {
		ScreenFXConfig.spyglassOverlayColour = "#336699";
		ScreenFXConfig.spyglassOverlayOpacity = 0.5f;
		assertColour(Colour.ofArgb(0x7F336699), Vanilla.spyglassBars());
	}

	@Test
	void spyglassBarsFallBackToBlackForAnInvalidColour() {
		ScreenFXConfig.spyglassOverlayColour = "not a colour";
		ScreenFXConfig.spyglassOverlayOpacity = 0.5f;
		assertColour(Colour.ofArgb(0x7F000000), Vanilla.spyglassBars());
	}

	@Test
	void fixedVignetteIgnoresBrightness() {
		ScreenFXConfig.vignetteMode = effectModeEnum.FIXED;
		ScreenFXConfig.vignetteColour = "#3366CC";
		ScreenFXConfig.vignetteOpacity = 0.5f;
		Colour expected = new Colour(1f, 0.4f, 0.3f, 0.1f);
		assertColour(expected, Vanilla.vignette(0.2f));
		assertColour(expected, Vanilla.vignette(0.9f));
	}

	@Test
	void dynamicVignetteScalesWithBrightness() {
		ScreenFXConfig.vignetteColour = "#3366CC";
		ScreenFXConfig.vignetteOpacity = 0.5f;
		assertColour(new Colour(1f, 0.24f, 0.18f, 0.06f), Vanilla.vignette(0.6f));
	}

	@Test
	void worldBorderVignetteColour() {
		ScreenFXConfig.vignetteMode = effectModeEnum.FIXED;
		ScreenFXConfig.vignetteWorldBorderColour = "#00FF00";
		assertColour(new Colour(1f, 1f, 0f, 1f), Vanilla.worldBorderVignette(0.6f));
	}

	@Test
	void disabledWorldBorderVignetteUsesTheNormalVignette() {
		ScreenFXConfig.vignetteMode = effectModeEnum.FIXED;
		ScreenFXConfig.vignetteColour = "#3366CC";
		ScreenFXConfig.vignetteOpacity = 0.5f;
		ScreenFXConfig.vignetteWorldBorderDisable = true;
		assertColour(new Colour(1f, 0.4f, 0.3f, 0.1f), Vanilla.worldBorderVignette(0.6f));
	}

	@Test
	void pumpkinAndPowderSnowOpacity() {
		ScreenFXConfig.pumpkinOpacity = 0.3f;
		ScreenFXConfig.powderSnowOpacity = 0.5f;
		assertEquals(0.3f, Vanilla.pumpkinAlpha(), EPSILON);
		assertEquals(0.4f, Vanilla.powderSnowAlpha(0.8f), EPSILON);
	}

	@Test
	void distortionColourAndOpacity() {
		ScreenFXConfig.distortionColour = "#FF0000";
		ScreenFXConfig.distortionOpacity = 0.5f;
		assertColour(new Colour(1f, 0.4f, 0f, 0f), Vanilla.distortion(0.8f));
	}

	@Test
	void fixedDistortionRadius() {
		ScreenFXConfig.distortionMode = effectModeEnum.FIXED;
		ScreenFXConfig.distortionRadius = 0.7f;
		assertEquals(1.3f, Vanilla.distortionSize(0.2f), EPSILON);
		assertEquals(1.3f, Vanilla.distortionSize(0.9f), EPSILON);
	}

	@Test
	void dynamicDistortionRadius() {
		ScreenFXConfig.distortionRadius = 0.5f;
		assertEquals(1.6f, Vanilla.distortionSize(0.8f), EPSILON);
	}

	@Test
	void elderGuardianPeaksAtTheConfiguredOpacity() {
		ScreenFXConfig.elderGuardianFadeInFadeOutOpacity = 0.1f;
		ScreenFXConfig.elderGuardianOpacity = 0.8f;
		assertEquals(0.8f, Vanilla.elderGuardianPeakAlpha(), EPSILON);
	}

	@Test
	void elderGuardianScaleLeavesDepthAlone() {
		ScreenFXConfig.elderGuardianScale = 1.5f;
		assertArrayEquals(new float[]{1.5f, 1.5f, 1f}, Vanilla.elderGuardianScale(), EPSILON);
	}

	@Test
	void elderGuardianDuration() {
		ScreenFXConfig.elderGuardianAnimationDuration = 2f;
		assertEquals(60, Vanilla.elderGuardianLifetime());
	}
}
