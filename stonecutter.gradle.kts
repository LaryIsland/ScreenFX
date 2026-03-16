plugins {
	id("dev.kikugie.stonecutter")
	id("net.fabricmc.fabric-loom-remap") version "1.15-SNAPSHOT" apply false
}

stonecutter active "1.21.1"

// See https://stonecutter.kikugie.dev/wiki/config/params
stonecutter parameters {
	swaps["mod_version"] = "\"${property("mod.version")}\";"
	swaps["minecraft"] = "\"${node.metadata.version}\";"
	constants["release"] = property("mod.id") != "template"
	dependencies["fapi"] = node.project.property("fabric_api") as String

	replacements {
		string(current.parsed >= "1.21.11") {
			replace("ResourceLocation", "Identifier")
		}
	}
}