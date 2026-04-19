pluginManagement {
	repositories {
		mavenLocal()
		mavenCentral()
		gradlePluginPortal()
		maven("https://maven.fabricmc.net/")
		maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie Snapshots" }
	}
}

plugins {
	id("dev.kikugie.stonecutter") version "0.9"
}

stonecutter {
	create(rootProject) {
		fun versionsObfuscation(vararg versions: String) {
			for (version in versions)
				version(version).buildscript(
					if (stonecutter.eval(version, ">=26.1"))
						"build.gradle.kts"
					else
						"build-obfuscated.gradle.kts"
				)
		}
		versionsObfuscation("1.20.1", "1.21.1", "1.21.3", "1.21.4", "1.21.5", "1.21.8", "1.21.11", "26.1.2")
		vcsVersion = "26.1.2"
	}
}

rootProject.name = "ScreenFX"