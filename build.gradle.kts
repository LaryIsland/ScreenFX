plugins {
	id("net.fabricmc.fabric-loom") version "1.16-SNAPSHOT"
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
	minecraft("com.mojang:minecraft:${sc.current.version}")
	implementation("net.fabricmc:fabric-loader:${property("fabric_loader")}")
	implementation("com.terraformersmc:modmenu:${property("modmenu_version")}")
}

loom {
	fabricModJsonPath = rootProject.file("src/main/resources/fabric.mod.json")
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

		val mixinList = buildString {
			appendLine("""		,"ElderGuardianParticleMixin"""")
			appendLine("""		,"ElderGuardianParticleGroupMixin"""")
			append("""		,"ElderGuardianParticleGroupMixin${'$'}ElderGuardianRenderStateMixin"""")
		}

		val mixinJava = "JAVA_${requiredJava.majorVersion}"
		filesMatching("*.mixins.json") { expand("java" to mixinJava, "mixinList" to mixinList) }
	}

	register<Copy>("buildAndCollect") {
		group = "build"
		from(jar.map { it.archiveFile })
		into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
		dependsOn("build")
	}
}