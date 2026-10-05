//? if <= 26.2 {
/*package com.laryisland.screenfx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.laryisland.screenfx.config.ScreenFXConfig;
import com.laryisland.screenfx.support.Handlers;
import com.laryisland.screenfx.support.ScreenFXTest;
import java.lang.reflect.Field;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.junit.jupiter.api.Test;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Up to 26.2 these toggles hook ScreenEffectRenderer (and GameRenderer for the totem up to 1.21.5).
// From 26.3 the same behaviour lives in LevelExtractor, covered by PlayerStateTogglesTest.
class ScreenEffectTogglesTest extends ScreenFXTest {

	@Test
	void fireTestingShowsTheFireOverlay() {
		assertFalse((Boolean) Handlers.call("fireTest", false));
		ScreenFXConfig.fireTesting = true;
		assertTrue((Boolean) Handlers.call("fireTest", false));
	}

	@Test
	void underwaterTestingShowsTheUnderwaterOverlay() {
		assertFalse((Boolean) Handlers.call("underwaterTest", false));
		ScreenFXConfig.underwaterTesting = true;
		assertTrue((Boolean) Handlers.call("underwaterTest", false));
	}

	@Test
	void inWallTestingShowsTheInWallOverlay() {
		CallbackInfoReturnable<BlockState> result = new CallbackInfoReturnable<>("getViewBlockingState", true, (BlockState) null);
		Handlers.call("inWallTest", result);
		assertNull(result.getReturnValue());

		ScreenFXConfig.inWallTesting = true;
		Handlers.call("inWallTest", result);
		assertEquals(Blocks.COBBLESTONE.defaultBlockState(), result.getReturnValue());
	}

	@Test
	void totemAnimationCanBeDisabled() throws ReflectiveOperationException {
		Object renderer = Handlers.receiverOf("floatingItem_disable");
		Field ticks = Handlers.owner("floatingItem_disable").getDeclaredField("itemActivationTicks");
		ticks.setAccessible(true);

		ticks.setInt(renderer, 40);
		Handlers.call("floatingItem_disable");
		assertEquals(40, ticks.getInt(renderer));

		ScreenFXConfig.totemOfUndyingDisable = true;
		Handlers.call("floatingItem_disable");
		assertEquals(0, ticks.getInt(renderer));
	}
}
*///?}
