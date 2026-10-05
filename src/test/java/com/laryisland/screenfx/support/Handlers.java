package com.laryisland.screenfx.support;

import com.laryisland.screenfx.ScreenFX;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sun.misc.Unsafe;

/**
 * Calls ScreenFX handler methods as Mixin merged them into the real Minecraft classes, so a test exercises
 * whichever target class the handler lives in for the version under test.
 */
public final class Handlers {

	private static final Map<String, Method> METHODS = new HashMap<>();
	private static final Map<Class<?>, Object> RECEIVERS = new HashMap<>();
	private static final Set<String> CALLED = new TreeSet<>();

	private Handlers() {}

	/** Every handler name a test has called so far in this run. */
	public static synchronized Set<String> called() {
		return Set.copyOf(CALLED);
	}

	/**
	 * Invokes the handler named {@code name}. Each provided value fills the first remaining parameter it fits;
	 * every other parameter gets a neutral default (0, false, null, or a fresh CallbackInfo).
	 */
	public static Object call(String name, Object... provided) {
		Method method = find(name);
		Class<?>[] types = method.getParameterTypes();
		Object[] args = new Object[types.length];
		boolean[] filled = new boolean[types.length];
		for (Object value : provided) {
			int slot = -1;
			for (int i = 0; i < types.length && slot < 0; i++) {
				if (!filled[i] && fits(types[i], value)) slot = i;
			}
			if (slot < 0) {
				throw new IllegalArgumentException(name + Arrays.toString(types) + " has no free parameter for " + value);
			}
			args[slot] = value;
			filled[slot] = true;
		}
		for (int i = 0; i < types.length; i++) {
			if (!filled[i]) args[i] = defaultFor(types[i], name);
		}
		synchronized (Handlers.class) {
			CALLED.add(name);
		}
		try {
			Object receiver = Modifier.isStatic(method.getModifiers()) ? null : receiver(method.getDeclaringClass());
			return method.invoke(receiver, args);
		} catch (InvocationTargetException e) {
			throw new AssertionError("Handler " + name + " threw", e.getCause());
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}

	public static boolean exists(String name) {
		return !matches(name).isEmpty();
	}

	/** The Minecraft class the handler was merged into for this version. */
	public static Class<?> owner(String name) {
		return find(name).getDeclaringClass();
	}

	/** The instance an instance handler is called on, for checking fields it writes. */
	public static Object receiverOf(String name) {
		return receiver(owner(name));
	}

	private static synchronized Method find(String name) {
		return METHODS.computeIfAbsent(name, n -> {
			List<Method> found = matches(n);
			if (found.size() != 1) {
				throw new IllegalStateException("Expected exactly one merged handler named " + n + " but found " + found);
			}
			Method method = found.get(0);
			method.setAccessible(true);
			return method;
		});
	}

	private static List<Method> matches(String name) {
		String suffix = "$" + ScreenFX.MOD_ID + "$" + name;
		List<Method> found = new ArrayList<>();
		for (String target : ScreenFXMixins.byTarget().keySet()) {
			try {
				for (Method method : ScreenFXMixins.loadTarget(target).getDeclaredMethods()) {
					if (method.getName().endsWith(suffix)) found.add(method);
				}
			} catch (ClassNotFoundException e) {
				throw new IllegalStateException("Mixin target " + target + " is missing", e);
			}
		}
		return found;
	}

	private static boolean fits(Class<?> type, Object value) {
		if (value == null) return !type.isPrimitive();
		if (!type.isPrimitive()) return type.isInstance(value);
		return (type == float.class && value instanceof Float)
			|| (type == int.class && value instanceof Integer)
			|| (type == boolean.class && value instanceof Boolean)
			|| (type == double.class && value instanceof Double)
			|| (type == long.class && value instanceof Long);
	}

	private static Object defaultFor(Class<?> type, String name) {
		if (type == float.class) return 0f;
		if (type == int.class) return 0;
		if (type == boolean.class) return false;
		if (type == double.class) return 0d;
		if (type == long.class) return 0L;
		if (type == CallbackInfo.class) return new CallbackInfo(name, false);
		return null;
	}

	private static synchronized Object receiver(Class<?> owner) {
		// Handlers only read config, so an instance with no constructor run is enough to call them on.
		return RECEIVERS.computeIfAbsent(owner, Handlers::allocate);
	}

	/** Creates an instance without running any constructor. */
	public static Object allocate(Class<?> type) {
		try {
			Field field = Unsafe.class.getDeclaredField("theUnsafe");
			field.setAccessible(true);
			return ((Unsafe) field.get(null)).allocateInstance(type);
		} catch (ReflectiveOperationException e) {
			throw new IllegalStateException("Could not allocate " + type, e);
		}
	}
}
