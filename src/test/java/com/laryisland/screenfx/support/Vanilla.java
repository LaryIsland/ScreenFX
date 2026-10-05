package com.laryisland.screenfx.support;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;

/**
 * Feeds each ScreenFX handler the arguments vanilla passes it in the Minecraft version under test, and reads the
 * result back in one version-independent form. The argument layouts here were checked against decompiled vanilla
 * sources for every supported version; when porting, re-check them against the new version's sources.
 */
public final class Vanilla {

	/** A colour as alpha, red, green, blue in 0..1. */
	public record Colour(float a, float r, float g, float b) {
		public static Colour ofArgb(int argb) {
			return new Colour(((argb >>> 24) & 255) / 255f, ((argb >>> 16) & 255) / 255f, ((argb >>> 8) & 255) / 255f, (argb & 255) / 255f);
		}

		public static Colour hex(float a, String rgb) {
			return ofArgb(((int) (a * 255) << 24) | Integer.parseInt(rgb.substring(1), 16));
		}
	}

	private Vanilla() {}

	public static boolean mc(String predicate) {
		try {
			var minecraft = FabricLoader.getInstance().getModContainer("minecraft").orElseThrow().getMetadata().getVersion();
			return VersionPredicate.parse(predicate).test(minecraft);
		} catch (VersionParsingException e) {
			throw new IllegalArgumentException(predicate, e);
		}
	}

	// Fire: alpha 0.9, as vertex colour (1, 1, 1, 0.9) up to 26.1.2, then packed 0xE5FFFFFF.
	public static float fireAlpha() {
		if (mc("<=26.1.2")) return (Float) Handlers.call("fireOverlay_opacity", 0.9f);
		return Colour.ofArgb((Integer) Handlers.call("fireOverlay_opacity", 0xE5FFFFFF)).a();
	}

	// Fire: translated to y = -0.3.
	public static float fireY() {
		return (Float) Handlers.call("fireOverlay_translate", -0.3f);
	}

	// Underwater: alpha 0.1, from shader colour (b, b, b, 0.1) up to 1.21.3 then colorFromFloat(0.1, b, b, b).
	// Up to 26.2 the handler swaps the alpha argument alone; from 26.3 it rewrites the whole call in LevelExtractor.
	public static float underwaterAlpha(float brightness) {
		if (mc("<=26.2")) return (Float) Handlers.call("underwaterOverlay", 0.1f);
		TestArgs args = new TestArgs(0.1f, brightness, brightness, brightness);
		Handlers.call("underwaterOverlay", args);
		return args.getFloat(0);
	}

	// In wall: (0.1, 0.1, 0.1, 1) as vertex colour (r, g, b, a) up to 1.21.3, colorFromFloat(1, 0.1, 0.1, 0.1) to 26.1.2, then packed.
	public static Colour inWall() {
		if (mc("<=1.21.3")) {
			TestArgs args = new TestArgs(0.1f, 0.1f, 0.1f, 1f);
			Handlers.call("inWallOverlay", args);
			return new Colour(args.getFloat(3), args.getFloat(0), args.getFloat(1), args.getFloat(2));
		}
		if (mc("<=26.1.2")) {
			TestArgs args = new TestArgs(1f, 0.1f, 0.1f, 0.1f);
			Handlers.call("inWallOverlay", args);
			return new Colour(args.getFloat(0), args.getFloat(1), args.getFloat(2), args.getFloat(3));
		}
		return Colour.ofArgb((Integer) Handlers.call("inWallOverlay", 0xFF191919));
	}

	// Portal: alpha is the portal strength, via setColor(1, 1, 1, s) up to 1.21.1, then ARGB.white(s).
	public static float portalAlpha(float strength) {
		return (Float) Handlers.call("portalOverlay", strength);
	}

	// Spyglass: the bars around the scope are filled with 0xFF000000.
	public static Colour spyglassBars() {
		return Colour.ofArgb((Integer) Handlers.call("spyglassOverlay_opacity", 0xFF000000));
	}

	// Vignette: setColor(h, h, h, 1) up to 1.21.1, then colorFromFloat(1, h, h, h).
	public static Colour vignette(float brightness) {
		return vignetteCall("vignetteOverlay", brightness, brightness, brightness);
	}

	// World border vignette: setColor(0, f, f, 1) up to 1.21.1, then colorFromFloat(1, 0, f, f).
	public static Colour worldBorderVignette(float strength) {
		return vignetteCall("vignetteOverlay_worldBorder", 0f, strength, strength);
	}

	private static Colour vignetteCall(String handler, float r, float g, float b) {
		if (mc("<=1.21.1")) {
			TestArgs args = new TestArgs(r, g, b, 1f);
			Handlers.call(handler, args);
			return new Colour(args.getFloat(3), args.getFloat(0), args.getFloat(1), args.getFloat(2));
		}
		TestArgs args = new TestArgs(1f, r, g, b);
		Handlers.call(handler, args);
		return new Colour(args.getFloat(0), args.getFloat(1), args.getFloat(2), args.getFloat(3));
	}

	// Pumpkin and powder snow: texture overlay alpha, 1 for the pumpkin and the frozen percentage for powder snow.
	public static float pumpkinAlpha() {
		return (Float) Handlers.call("pumpkinBlurOverlay", 1f);
	}

	public static float powderSnowAlpha(float percentFrozen) {
		return (Float) Handlers.call("powderSnowOverlay", percentFrozen);
	}

	// Nausea: (0.2s, 0.4s, 0.2s) as setColor(r, g, b, 1) up to 1.21.1, then colorFromFloat(1, r, g, b).
	public static Colour distortion(float strength) {
		float r = 0.2f * strength, g = 0.4f * strength, b = 0.2f * strength;
		if (mc("<=1.21.1")) {
			TestArgs args = new TestArgs(r, g, b, 1f);
			Handlers.call("distortionOverlay", args);
			return new Colour(args.getFloat(3), args.getFloat(0), args.getFloat(1), args.getFloat(2));
		}
		TestArgs args = new TestArgs(1f, r, g, b);
		Handlers.call("distortionOverlay", args);
		return new Colour(args.getFloat(0), args.getFloat(1), args.getFloat(2), args.getFloat(3));
	}

	// Nausea: the overlay is drawn at size lerp(s, 2, 1).
	public static float distortionSize(float strength) {
		return (Float) Handlers.call("fixDistortionRadius", 2f - strength);
	}

	// Elder guardian: alpha is 0.05 + 0.5 * sin(progress), so it peaks at the sum of the two constants.
	public static float elderGuardianPeakAlpha() {
		return (Float) Handlers.call("elderGuardianFadeInFadeOutOpacity", 0.05f) + (Float) Handlers.call("elderGuardianOpacity", 0.5f);
	}

	// Elder guardian: returns the factor applied to each axis of vanilla's PoseStack.scale(x, y, z) call.
	public static float[] elderGuardianScale() {
		TestArgs args = new TestArgs(1f, 1f, 1f);
		Handlers.call("elderGuardianScale", args);
		return new float[]{args.getFloat(0), args.getFloat(1), args.getFloat(2)};
	}

	// Elder guardian: the particle lives for 30 ticks.
	public static int elderGuardianLifetime() {
		return (Integer) Handlers.call("elderGuardianAnimationDuration", 30);
	}
}
