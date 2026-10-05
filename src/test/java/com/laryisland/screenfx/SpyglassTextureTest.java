//? if >= 1.21.6 {
package com.laryisland.screenfx;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.laryisland.screenfx.config.ScreenFXConfig;
import com.laryisland.screenfx.support.Handlers;
import com.laryisland.screenfx.support.ScreenFXTest;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import org.junit.jupiter.api.Test;

//? if >= 26.1 {
import net.minecraft.client.renderer.state.gui.GuiRenderState;
//?} else
//import net.minecraft.client.gui.render.state.GuiRenderState;

// From 1.21.6 the spyglass texture opacity is passed as the tint of a blit call, so capture that call.
// Up to 1.21.5 it set the RenderSystem shader colour instead, which can't run without a render thread.
class SpyglassTextureTest extends ScreenFXTest {

	@Test
	void defaultTintIsOpaqueWhite() {
		assertEquals(0xFFFFFFFF, tint());
	}

	@Test
	void textureOpacityTintsTheBlit() {
		ScreenFXConfig.spyglassTextureOpacity = 0.4f;
		assertEquals(0x66FFFFFF, tint());
	}

	private static int tint() {
		RecordingGraphics graphics = (RecordingGraphics) Handlers.allocate(RecordingGraphics.class);
		Handlers.call("spyglassOverlay_textureOpacity", graphics);
		return graphics.tint;
	}

	private static class RecordingGraphics extends GuiGraphicsExtractor {
		int tint;

		// Never run: instances are allocated without a constructor so no real GUI state is needed.
		private RecordingGraphics() {
//? if >= 1.21.9 {
			super(null, (GuiRenderState) null, 0, 0);
//?} else
			//super(null, (GuiRenderState) null);
		}

		@Override
		public void blit(RenderPipeline pipeline, Identifier texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight, int color) {
			this.tint = color;
		}
	}
}
//?}
