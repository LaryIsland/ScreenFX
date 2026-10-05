package com.laryisland.screenfx.support;

import com.laryisland.screenfx.config.ScreenFXConfig;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

//? if >= 26.1 {
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentInitializers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.registries.VanillaRegistries;
//?}

/**
 * Base for value tests: bootstraps Minecraft's registries once, installs a bare {@link Minecraft} instance with
 * no player or level, and restores every ScreenFX config field to its default before each test.
 */
public abstract class ScreenFXTest {

	private static final Map<Field, Object> DEFAULTS = new HashMap<>();

	@BeforeAll
	static void bootstrap() throws ReflectiveOperationException {
		if (!DEFAULTS.isEmpty()) return;
		SharedConstants.tryDetectVersion();
		Bootstrap.bootStrap();
//? if >= 26.1
		bindItemComponents();
		for (Field field : Minecraft.class.getDeclaredFields()) {
			if (Modifier.isStatic(field.getModifiers()) && field.getType() == Minecraft.class) {
				field.setAccessible(true);
				field.set(null, Handlers.allocate(Minecraft.class));
			}
		}
		for (Field field : ScreenFXConfig.class.getDeclaredFields()) {
			if (Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())) {
				DEFAULTS.put(field, copy(field.get(null)));
			}
		}
	}

//? if >= 26.1 {
	// From 26.1 items only get their components when server data loads, so do the same here.
	private static void bindItemComponents() {
//? if > 26.2 {
		HolderLookup.Provider lookup = VanillaRegistries.createWorldLookup();
//?} else
		//HolderLookup.Provider lookup = VanillaRegistries.createLookup();
		BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.build(lookup).forEach(DataComponentInitializers.PendingComponents::apply);
	}
//?}

	@BeforeEach
	void resetConfig() throws IllegalAccessException {
		for (Map.Entry<Field, Object> entry : DEFAULTS.entrySet()) {
			entry.getKey().set(null, copy(entry.getValue()));
		}
	}

	/** Colours go through 8-bit channels in some versions, so allow just over one step of rounding. */
	protected static final float CHANNEL = 1.5f / 255f;
	protected static final float EPSILON = 1e-5f;

	protected static void assertColour(Vanilla.Colour expected, Vanilla.Colour actual) {
		boolean close = Math.abs(expected.a() - actual.a()) <= CHANNEL
			&& Math.abs(expected.r() - actual.r()) <= CHANNEL
			&& Math.abs(expected.g() - actual.g()) <= CHANNEL
			&& Math.abs(expected.b() - actual.b()) <= CHANNEL;
		if (!close) {
			throw new AssertionError("expected " + expected + " but was " + actual);
		}
	}

	private static Object copy(Object value) {
		return value instanceof Map<?, ?> map ? new LinkedHashMap<>(map) : value;
	}
}
