package com.laryisland.screenfx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import com.laryisland.screenfx.support.Handlers;
import com.laryisland.screenfx.support.ScreenFXMixins;
import com.laryisland.screenfx.support.Vanilla;
import java.util.Map;
import java.util.TreeSet;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

/**
 * Runs after every other test class and fails if any injector registered for this version was never called by a
 * test, so a handler that moves to a different class or a newly added one can't go untested.
 */
@Order(Integer.MAX_VALUE)
class HandlerCoverageTest {

	private static final String RENDER_SYSTEM = "drives RenderSystem directly, which needs a live render thread";

	private static Map<String, String> exemptions() {
		if (Vanilla.mc("<=1.21.5")) {
			return Map.of(
				"inWallOverlay_opacityBegin", RENDER_SYSTEM,
				"inWallOverlay_opacityEnd", RENDER_SYSTEM,
				"spyglassOverlay_textureOpacity", RENDER_SYSTEM,
				"spyglassOverlay_textureOpacityReset", RENDER_SYSTEM
			);
		}
		return Map.of();
	}

	@Test
	void everyInjectorIsExercisedByATest() {
		// Only meaningful when the whole suite ran in this JVM, not for a single filtered test class.
		assumeFalse(Handlers.called().isEmpty(), "No handler tests ran");
		TreeSet<String> untested = new TreeSet<>();
		for (ScreenFXMixins.Mixin mixin : ScreenFXMixins.all()) {
			for (String injector : mixin.injectors()) {
				if (!Handlers.called().contains(injector) && !exemptions().containsKey(injector)) {
					untested.add(mixin.name() + "." + injector);
				}
			}
		}
		assertEquals(new TreeSet<String>(), untested);
	}
}
