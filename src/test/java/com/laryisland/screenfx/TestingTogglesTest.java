package com.laryisland.screenfx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.laryisland.screenfx.config.ScreenFXConfig;
import com.laryisland.screenfx.config.ScreenFXConfig.effectModeEnum;
import com.laryisland.screenfx.support.Handlers;
import com.laryisland.screenfx.support.ScreenFXTest;
import com.laryisland.screenfx.support.Vanilla;
import org.junit.jupiter.api.Test;

//? if >= 1.21.2
import net.minecraft.world.item.equipment.Equippable;

/** The config screen's "testing" toggles force each overlay on, and leave vanilla alone when off. */
class TestingTogglesTest extends ScreenFXTest {

	@Test
	void portalTestingOverridesPortalIntensity() {
		assertEquals(0.1f, (Float) Handlers.call("portalEffectTesting", 0.1f), EPSILON);
		ScreenFXConfig.portalTesting = 0.6f;
		assertEquals(0.6f, (Float) Handlers.call("portalEffectTesting", 0.1f), EPSILON);
	}

	@Test
	void powderSnowTestingFreezesThePlayer() {
		assertEquals(0, Handlers.call("powderSnowTesting", 0));
		ScreenFXConfig.powerSnowTesting = 0.7f;
		ScreenFXConfig.powderSnowOpacity = 0.5f;
		assertEquals(1, Handlers.call("powderSnowTesting", 0));
		assertEquals(0.35f, Vanilla.powderSnowAlpha(0f), EPSILON);
	}

	@Test
	void spyglassTestingScopesIn() {
		assertFalse((Boolean) Handlers.call("spyglassTesting", false));
		ScreenFXConfig.spyglassTesting = true;
		assertTrue((Boolean) Handlers.call("spyglassTesting", false));
	}

	@Test
	void distortionTestingSetsNauseaIntensity() {
		assertEquals(0.2f, (Float) Handlers.call("distortionTesting_NauseaIntensity", 0.2f), EPSILON);
		ScreenFXConfig.distortionTesting = 0.4f;
		assertEquals(0.4f, (Float) Handlers.call("distortionTesting_NauseaIntensity", 0.2f), EPSILON);
		ScreenFXConfig.distortionMode = effectModeEnum.FIXED;
		assertEquals(1f, (Float) Handlers.call("distortionTesting_NauseaIntensity", 0.2f), EPSILON);
	}

	@Test
	void distortionTestingPassesTheNauseaCheck() {
		// Only versions up to 1.21.4 gate the nausea overlay on having the effect.
		if (!Vanilla.mc("<=1.21.4")) return;
		assertFalse((Boolean) Handlers.call("distortionTesting_NauseaCheck", false));
		ScreenFXConfig.distortionTesting = 0.4f;
		assertTrue((Boolean) Handlers.call("distortionTesting_NauseaCheck", false));
	}

	@Test
	void pumpkinTestingShowsThePumpkinOverlay() {
		ScreenFXConfig.pumpkinTesting = true;
//? if >= 1.21.2 {
		Equippable equippable = (Equippable) Handlers.call("pumpkinBlurTesting", (Object) null);
		assertEquals("minecraft:misc/pumpkinblur", equippable.cameraOverlay().orElseThrow().toString());
//?} else
		//assertTrue((Boolean) Handlers.call("pumpkinBlurTesting", false));
	}
}
