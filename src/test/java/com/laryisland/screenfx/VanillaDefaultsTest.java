package com.laryisland.screenfx;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.laryisland.screenfx.support.ScreenFXTest;
import com.laryisland.screenfx.support.Vanilla;
import com.laryisland.screenfx.support.Vanilla.Colour;
import org.junit.jupiter.api.Test;

/** With the default config, every value ScreenFX touches must come out exactly as vanilla would draw it. */
class VanillaDefaultsTest extends ScreenFXTest {

	@Test
	void fire() {
		assertEquals(0.9f, Vanilla.fireAlpha(), CHANNEL);
		assertEquals(-0.3f, Vanilla.fireY(), EPSILON);
	}

	@Test
	void underwater() {
		assertEquals(0.1f, Vanilla.underwaterAlpha(0.6f), CHANNEL);
	}

	@Test
	void inWall() {
		assertColour(new Colour(1f, 0.1f, 0.1f, 0.1f), Vanilla.inWall());
	}

	@Test
	void portal() {
		assertEquals(0.7f, Vanilla.portalAlpha(0.7f), EPSILON);
	}

	@Test
	void spyglassBars() {
		assertColour(Colour.ofArgb(0xFF000000), Vanilla.spyglassBars());
	}

	@Test
	void vignette() {
		assertColour(new Colour(1f, 0.6f, 0.6f, 0.6f), Vanilla.vignette(0.6f));
	}

	@Test
	void worldBorderVignette() {
		assertColour(new Colour(1f, 0f, 0.6f, 0.6f), Vanilla.worldBorderVignette(0.6f));
	}

	@Test
	void pumpkinAndPowderSnow() {
		assertEquals(1f, Vanilla.pumpkinAlpha(), EPSILON);
		assertEquals(0.4f, Vanilla.powderSnowAlpha(0.4f), EPSILON);
	}

	@Test
	void distortion() {
		assertColour(new Colour(1f, 0.1f, 0.2f, 0.1f), Vanilla.distortion(0.5f));
		assertEquals(1.5f, Vanilla.distortionSize(0.5f), EPSILON);
	}

	@Test
	void elderGuardian() {
		assertEquals(30, Vanilla.elderGuardianLifetime());
		assertArrayEquals(new float[]{1f, 1f, 1f}, Vanilla.elderGuardianScale(), EPSILON);
	}
}
