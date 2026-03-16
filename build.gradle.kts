plugins {
	id("net.fabricmc.fabric-loom-remap") version "1.15-SNAPSHOT"
}

version = "${property("mod.version")}+${sc.current.version}"
base.archivesName = property("mod.id") as String

val requiredJava = when {
	sc.current.parsed >= "26.1" -> JavaVersion.VERSION_25
	else -> JavaVersion.VERSION_21
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
	modApi("com.terraformersmc:modmenu:${property("modmenu_version")}")

	fapi("fabric-screen-api-v1", "fabric-key-binding-api-v1", "fabric-lifecycle-events-v1", "fabric-resource-loader-v0")
}

loom {
	//fabricModJsonPath = rootProject.file("src/main/resources/fabric.mod.json")
	//accessWidenerPath = rootProject.file("src/main/resources/screenfx.accesswidener")

	decompilerOptions.named("vineflower") {
		options.put("mark-corresponding-synthetics", "1")
	}

	runConfigs.all {
		ideConfigGenerated(true)
		vmArgs("-Dmixin.debug.export=true")
		runDir = "../../run"
	}
}

java {
	withSourcesJar()
	targetCompatibility = requiredJava
	sourceCompatibility = requiredJava
}

tasks {
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

		duplicatesStrategy = DuplicatesStrategy.EXCLUDE

		val mixinJava = "JAVA_${requiredJava.majorVersion}"
		filesMatching("*.mixins.json") { expand("java" to mixinJava) }
	}

	register<Copy>("buildAndCollect") {
		group = "build"
		from(remapJar.map { it.archiveFile }, remapSourcesJar.map { it.archiveFile })
		into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
		dependsOn("build")
	}
}