//? if > 26.2 {
package com.laryisland.screenfx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.laryisland.screenfx.config.ScreenFXConfig;
import com.laryisland.screenfx.support.Handlers;
import com.laryisland.screenfx.support.ScreenFXTest;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState.ItemActivationRenderState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import org.junit.jupiter.api.Test;

// From 26.3 these toggles wrap the writes into PlayerRenderState in LevelExtractor.
// Up to 26.2 the same behaviour is covered by ScreenEffectTogglesTest.
class PlayerStateTogglesTest extends ScreenFXTest {

	@Test
	void fireTestingShowsTheFireOverlay() {
		PlayerRenderState state = new PlayerRenderState();
		Operation<Void> write = args -> {
			((PlayerRenderState) args[0]).isOnFire = (Boolean) args[1];
			return null;
		};
		Handlers.call("fireTest", state, false, write);
		assertFalse(state.isOnFire);

		ScreenFXConfig.fireTesting = true;
		Handlers.call("fireTest", state, false, write);
		assertTrue(state.isOnFire);
	}

	@Test
	void underwaterTestingShowsTheUnderwaterOverlay() {
		PlayerRenderState state = new PlayerRenderState();
		Operation<Void> write = args -> {
			((PlayerRenderState) args[0]).isEyeInWater = (Boolean) args[1];
			return null;
		};
		Handlers.call("underwaterTest", state, false, write);
		assertFalse(state.isEyeInWater);

		ScreenFXConfig.underwaterTesting = true;
		Handlers.call("underwaterTest", state, false, write);
		assertTrue(state.isEyeInWater);
	}

	@Test
	void inWallTestingShowsTheInWallOverlay() {
		assertNull(Handlers.call("inWallTest", (Object) null));
		ScreenFXConfig.inWallTesting = true;
		assertEquals(Blocks.COBBLESTONE.defaultBlockState(), Handlers.call("inWallTest", (Object) null));
		assertEquals(Blocks.STONE.defaultBlockState(), Handlers.call("inWallTest", Blocks.STONE.defaultBlockState()));
	}

	@Test
	void totemAnimationCanBeDisabled() {
		ItemActivationRenderState totem = new ItemActivationRenderState(new ItemStack(Items.TOTEM_OF_UNDYING), 40, 0f, 0f);
		ItemActivationRenderState other = new ItemActivationRenderState(new ItemStack(Items.DIAMOND), 40, 0f, 0f);
		Operation<Void> write = args -> {
			((PlayerRenderState) args[0]).itemActivation = (ItemActivationRenderState) args[1];
			return null;
		};

		PlayerRenderState state = new PlayerRenderState();
		Handlers.call("totemOfUndying_disable", state, totem, write);
		assertSame(totem, state.itemActivation);

		ScreenFXConfig.totemOfUndyingDisable = true;
		state = new PlayerRenderState();
		Handlers.call("totemOfUndying_disable", state, totem, write);
		assertNull(state.itemActivation);

		Handlers.call("totemOfUndying_disable", state, other, write);
		assertSame(other, state.itemActivation);
	}
}
//?}
