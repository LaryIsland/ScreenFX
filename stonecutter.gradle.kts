plugins {
	id("dev.kikugie.stonecutter")
}

stonecutter active "26.1.2"

// See https://stonecutter.kikugie.dev/wiki/config/params
stonecutter parameters {
	swaps["mod_version"] = "\"${property("mod.version")}\";"
	swaps["minecraft"] = "\"${node.metadata.version}\";"
	constants["release"] = property("mod.id") != "template"
	dependencies["fapi"] = node.project.property("fabric_api") as String

	swaps["render_camera_overlays"] = when {
		current.parsed < "1.21" -> "method = \"render\","
		else -> "method = \"renderCameraOverlays\","
	}

	replacements {
		string(current.parsed >= "1.21.8") {
			replace("RenderType::guiTextured", "RenderPipelines.GUI_TEXTURED")
		}
		string(current.parsed >= "1.21.11") {
			replace("ResourceLocation", "Identifier")
		}
		string(current.parsed >= "26.1") {
			replace("GuiGraphics", "GuiGraphicsExtractor")
			replace("renderCameraOverlays", "extractCameraOverlays")
			replace("renderTextureOverlay", "extractTextureOverlay")
			replace("renderVignette", "extractVignette")
			replace("renderConfusionOverlay", "extractConfusionOverlay")
			replace("renderPortalOverlay", "extractPortalOverlay")
			replace("renderSpyglassOverlay", "extractSpyglassOverlay")
			replace("render(", "extractRenderState(")
			replace("renderListSeparators", "extractListSeparators")
			replace("renderContent", "extractContent")
			replace(".drawString(", ".text(")
			replace("drawCenteredString", "centeredText")
			replace("renderTransparentBackground", "extractTransparentBackground")
			replace("renderListBackground", "extractListBackground")
		}
	}
}