package com.laryisland.screenfx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.laryisland.screenfx.support.ScreenFXMixins;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Porting check: every injector in the mixin config must find its injection point and be merged into its target. */
class MixinApplyTest {

	@Test
	void everyInjectorIsMergedIntoItsTarget() {
		List<String> checked = new ArrayList<>();
		List<String> failures = new ArrayList<>();
		ScreenFXMixins.byTarget().forEach((target, mixins) -> {
			Class<?> targetClass;
			try {
				targetClass = ScreenFXMixins.loadTarget(target);
			} catch (Throwable t) {
				failures.add(target + " failed to load: " + rootCause(t));
				return;
			}
			List<String> merged = Arrays.stream(targetClass.getDeclaredMethods()).map(Method::getName).toList();
			for (ScreenFXMixins.Mixin mixin : mixins) {
				for (String injector : mixin.injectors()) {
					String suffix = "$" + ScreenFX.MOD_ID + "$" + injector;
					if (merged.stream().noneMatch(name -> name.endsWith(suffix))) {
						failures.add(mixin.name() + "." + injector + " was not merged into " + target);
					}
					checked.add(mixin.name() + "." + injector);
				}
			}
		});
		assertFalse(checked.isEmpty(), "No injectors found in " + ScreenFX.MOD_ID + ".mixins.json");
		assertEquals(List.of(), failures);
	}

	@Test
	void everyCompiledMixinIsRegistered() {
		List<String> registered = ScreenFXMixins.all().stream().map(ScreenFXMixins.Mixin::name).toList();
		List<String> unregistered = ScreenFXMixins.compiledMixins().stream().filter(name -> !registered.contains(name)).toList();
		assertEquals(List.of(), unregistered, "Compiled for this version but missing from the mixinList in the build script");
	}

	private static String rootCause(Throwable t) {
		while (t.getCause() != null) t = t.getCause();
		return t.toString();
	}
}
