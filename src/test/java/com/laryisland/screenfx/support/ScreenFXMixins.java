package com.laryisland.screenfx.support;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.laryisland.screenfx.ScreenFX;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

/** The ScreenFX mixins registered for the Minecraft version under test, read without loading the mixin classes. */
public final class ScreenFXMixins {

	private static final Set<String> INJECTORS = Set.of(
		"Lorg/spongepowered/asm/mixin/injection/Inject;",
		"Lorg/spongepowered/asm/mixin/injection/ModifyArg;",
		"Lorg/spongepowered/asm/mixin/injection/ModifyArgs;",
		"Lorg/spongepowered/asm/mixin/injection/ModifyConstant;",
		"Lorg/spongepowered/asm/mixin/injection/ModifyVariable;",
		"Lorg/spongepowered/asm/mixin/injection/Redirect;",
		"Lcom/llamalad7/mixinextras/injector/ModifyExpressionValue;",
		"Lcom/llamalad7/mixinextras/injector/wrapoperation/WrapOperation;"
	);

	public record Mixin(String name, List<String> targets, List<String> injectors) {}

	private static List<Mixin> mixins;
	private static String mixinPackage;

	private ScreenFXMixins() {}

	public static synchronized List<Mixin> all() {
		if (mixins == null) {
			mixins = load();
		}
		return mixins;
	}

	/** Every target class name, mapped to the mixins applied to it. */
	public static Map<String, List<Mixin>> byTarget() {
		Map<String, List<Mixin>> byTarget = new LinkedHashMap<>();
		for (Mixin mixin : all()) {
			for (String target : mixin.targets()) {
				byTarget.computeIfAbsent(target, t -> new ArrayList<>()).add(mixin);
			}
		}
		return byTarget;
	}

	/** Every class compiled into the mixin package that carries @Mixin, named relative to the package. */
	public static List<String> compiledMixins() {
		all();
		String packagePath = mixinPackage.replace('.', '/');
		List<String> found = new ArrayList<>();
		try {
			Enumeration<URL> roots = ScreenFXMixins.class.getClassLoader().getResources(packagePath);
			while (roots.hasMoreElements()) {
				URL root = roots.nextElement();
				if (!root.getProtocol().equals("file")) continue;
				Path dir = Path.of(root.toURI());
				try (Stream<Path> files = Files.walk(dir)) {
					for (Path file : files.filter(f -> f.toString().endsWith(".class")).toList()) {
						String relative = dir.relativize(file).toString().replace(File.separatorChar, '.');
						String name = relative.substring(0, relative.length() - ".class".length());
						if (isMixin(readClass(mixinPackage + "." + name))) found.add(name);
					}
				}
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		}
		if (found.isEmpty()) throw new IllegalStateException("Found no compiled mixin classes under " + packagePath);
		return found;
	}

	public static Class<?> loadTarget(String target) throws ClassNotFoundException {
		// Loading through Knot (this class's loader) is what applies the mixins.
		return Class.forName(target, false, ScreenFXMixins.class.getClassLoader());
	}

	private static List<Mixin> load() {
		try {
			var mod = FabricLoader.getInstance().getModContainer(ScreenFX.MOD_ID).orElseThrow();
			JsonObject config = JsonParser.parseString(Files.readString(mod.findPath(ScreenFX.MOD_ID + ".mixins.json").orElseThrow())).getAsJsonObject();
			mixinPackage = config.get("package").getAsString();

			List<Mixin> result = new ArrayList<>();
			for (JsonElement entry : config.getAsJsonArray("client")) {
				ClassNode node = readClass(mixinPackage + "." + entry.getAsString());
				List<String> injectors = new ArrayList<>();
				for (MethodNode method : node.methods) {
					if (hasAnnotation(method.visibleAnnotations) || hasAnnotation(method.invisibleAnnotations)) {
						injectors.add(method.name);
					}
				}
				result.add(new Mixin(entry.getAsString(), targets(node), injectors));
			}
			return List.copyOf(result);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private static ClassNode readClass(String className) throws IOException {
		ClassNode node = new ClassNode();
		// Mixin classes must never be loaded directly, so read their bytes instead.
		try (InputStream bytes = ScreenFXMixins.class.getClassLoader().getResourceAsStream(className.replace('.', '/') + ".class")) {
			if (bytes == null) throw new IllegalStateException("Missing mixin class " + className);
			new ClassReader(bytes).accept(node, ClassReader.SKIP_CODE);
		}
		return node;
	}

	private static List<AnnotationNode> classAnnotations(ClassNode node) {
		List<AnnotationNode> annotations = new ArrayList<>();
		if (node.invisibleAnnotations != null) annotations.addAll(node.invisibleAnnotations);
		if (node.visibleAnnotations != null) annotations.addAll(node.visibleAnnotations);
		return annotations;
	}

	private static boolean isMixin(ClassNode node) {
		return classAnnotations(node).stream().anyMatch(a -> a.desc.equals("Lorg/spongepowered/asm/mixin/Mixin;"));
	}

	private static List<String> targets(ClassNode mixin) {
		List<String> targets = new ArrayList<>();
		for (AnnotationNode annotation : classAnnotations(mixin)) {
			if (!annotation.desc.equals("Lorg/spongepowered/asm/mixin/Mixin;") || annotation.values == null) continue;
			for (int i = 0; i < annotation.values.size(); i += 2) {
				String key = (String) annotation.values.get(i);
				for (Object value : (List<?>) annotation.values.get(i + 1)) {
					if (key.equals("value")) targets.add(((Type) value).getClassName());
					if (key.equals("targets")) targets.add(((String) value).replace('/', '.'));
				}
			}
		}
		if (targets.isEmpty()) throw new IllegalStateException("No @Mixin targets on " + mixin.name);
		return targets;
	}

	private static boolean hasAnnotation(List<AnnotationNode> annotations) {
		return annotations != null && annotations.stream().anyMatch(a -> INJECTORS.contains(a.desc));
	}
}
