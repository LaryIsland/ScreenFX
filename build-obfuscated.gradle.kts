plugins {
	id("net.fabricmc.fabric-loom-remap") version "1.18-SNAPSHOT"
	id("me.modmuss50.mod-publish-plugin") version "2.2.1"
}

version = "${property("mod.version")}+${sc.current.version}"
base.archivesName = property("mod.id") as String

val requiredJava = when {
	sc.current.parsed >= "26.1" -> JavaVersion.VERSION_25
	sc.current.parsed >= "1.20.5" -> JavaVersion.VERSION_21
	sc.current.parsed >= "1.18" -> JavaVersion.VERSION_17
	sc.current.parsed >= "1.17" -> JavaVersion.VERSION_16
	sc.current.parsed >= "1.12" -> JavaVersion.VERSION_1_8
	else -> JavaVersion.VERSION_25
}

repositories {
	maven("https://maven.terraformersmc.com/releases/")
}

dependencies {
	fun fapi(vararg modules: String) {
		for (it in modules) modImplementation(fabricApi.module(it, property("fabric_api") as String))
	}

	minecraft("com.mojang:minecraft:${sc.current.version}")
	mappings(loom.officialMojangMappings())
	modImplementation("net.fabricmc:fabric-loader:${property("fabric_loader")}")
	modImplementation("com.terraformersmc:modmenu:${property("modmenu_version")}")

	fapi("fabric-screen-api-v1", "fabric-key-binding-api-v1", "fabric-lifecycle-events-v1", "fabric-resource-loader-v0")

	testImplementation("net.fabricmc:fabric-loader-junit:${property("fabric_loader")}")
}

// Tests only need vanilla plus ScreenFX, and Fabric API and Mod Menu are pinned to the oldest Minecraft version a
// target supports (e.g. 1.21.9 for the 1.21.11 jar), which may not load on the newer Minecraft the tests run on.
configurations.testRuntimeClasspath {
	exclude(group = "remapped.net.fabricmc.fabric-api")
	exclude(group = "remapped.com.terraformersmc")
}

loom {
	fabricModJsonPath = rootProject.file("src/main/resources/fabric.mod.json")
	//accessWidenerPath = rootProject.file("src/main/resources/screenfx.accesswidener")

	decompilerOptions.named("vineflower") {
		options.put("mark-corresponding-synthetics", "1")
	}

	runConfigs.all {
		generateRunConfig = true
		jvmArguments.add("-Dmixin.debug.export=true")
		runDirectory = file("../../run")
	}
}

java {
	withSourcesJar()
	targetCompatibility = requiredJava
	sourceCompatibility = requiredJava
}

publishMods {
	file = tasks.remapJar.flatMap { it.archiveFile }
	version = property("mod.version") as String
	displayName = "${property("mod.name")} v${property("mod.version")}"
	changelog = providers.fileContents(rootProject.layout.projectDirectory.file("CHANGELOG.md")).asText
	type = STABLE
	modLoaders.addAll("fabric", "quilt")
	dryRun = providers.environmentVariable("MODRINTH_TOKEN").getOrElse("").isEmpty()

	val mcVersions = (property("mod.mc_targets") as String).split(" ")

	modrinth {
		projectId = property("mod.modrinth_id") as String
		accessToken = providers.environmentVariable("MODRINTH_TOKEN")
		minecraftVersions.addAll(mcVersions)
		optional("modmenu")
	}

	curseforge {
		projectId = property("mod.curseforge_id") as String
		projectSlug = property("mod.id") as String
		accessToken = providers.environmentVariable("CURSEFORGE_TOKEN")
		minecraftVersions.addAll(mcVersions)
		client = true
		server = false
		optional("modmenu")
	}
}

tasks {
	test {
		useJUnitPlatform()
		// Lets HandlerCoverageTest run after every other test class.
		systemProperty("junit.jupiter.testclass.order.default", "org.junit.jupiter.api.ClassOrderer\$OrderAnnotation")
		// Minecraft writes logs/ into the working directory, so keep it inside build/.
		workingDir = layout.buildDirectory.dir("test-run").get().asFile
		doFirst { workingDir.mkdirs() }
	}

	processResources {
		inputs.property("id", project.property("mod.id"))
		inputs.property("name", project.property("mod.name"))
		inputs.property("version", project.property("mod.version"))
		inputs.property("minecraft", project.property("mod.mc_dep"))

		val props = mapOf(
			"id" to project.property("mod.id"),
			"name" to project.property("mod.name"),
			"version" to project.property("mod.version"),
			"minecraft" to project.property("mod.mc_dep")
		)

		filesMatching("fabric.mod.json") { expand(props) }

		val mixinList = buildList {
			add("GuiMixin")
			add("ItemInHandRendererMixin")
			if (stonecutter.compare(stonecutter.current.version, "1.21.9") >= 0) {
				add("ElderGuardianParticleMixin")
				add("ElderGuardianParticleGroupMixin")
				add("ElderGuardianParticleGroupMixin\$ElderGuardianRenderStateMixin")
			} else {
				if (stonecutter.compare(stonecutter.current.version, "1.21.5") <= 0) {
					add("GameRendererMixin")
				}
				add("MobAppearanceParticleMixin")
			}
		}.joinToString("\",\n\t\t\"")

		val mixinJava = "JAVA_${requiredJava.majorVersion}"
		inputs.property("mixinList", mixinList)
		inputs.property("mixinJava", mixinJava)
		filesMatching("*.mixins.json") { expand("java" to mixinJava, "mixinList" to mixinList) }
	}

	register<Copy>("buildAndCollect") {
		group = "build"
		from(remapJar.map { it.archiveFile }, remapSourcesJar.map { it.archiveFile })
		into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
		dependsOn("build")
	}
}