//? if > 26.2 {
package com.laryisland.screenfx.mixin;

import com.laryisland.screenfx.config.ScreenFXConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.extract.LevelExtractor;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState.ItemActivationRenderState;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LevelExtractor.class)
public abstract class LevelExtractorMixin {

	@WrapOperation(
		method = "extractPlayerState",
		at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/state/level/PlayerRenderState;isEyeInWater:Z", opcode = Opcodes.PUTFIELD)
	)
	private void underwaterTest(PlayerRenderState instance, boolean value, Operation<Void> original) {
		original.call(instance, value || ScreenFXConfig.underwaterTesting);
	}

	@WrapOperation(
		method = "extractPlayerState",
		at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/state/level/PlayerRenderState;isOnFire:Z", opcode = Opcodes.PUTFIELD)
	)
	private void fireTest(PlayerRenderState instance, boolean value, Operation<Void> original) {
		if (ScreenFXConfig.fireTesting) {
			instance.isOnFire = true;
		} else {
			original.call(instance, value);
		}
	}

	@ModifyVariable(
		method = "extractPlayerState",
		at = @At("STORE"),
		name = "viewBlockingState"
	)
	private BlockState inWallTest(BlockState viewBlockingState) {
		return (viewBlockingState == null && ScreenFXConfig.inWallTesting) ? Blocks.COBBLESTONE.defaultBlockState() : viewBlockingState;
	}

	@WrapOperation(
		method = "extractPlayerState",
		at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/state/level/PlayerRenderState;itemActivation:Lnet/minecraft/client/renderer/state/level/PlayerRenderState$ItemActivationRenderState;", opcode = Opcodes.PUTFIELD)
	)
	private void totemOfUndying_disable(PlayerRenderState instance, ItemActivationRenderState value, Operation<Void> original) {
		if (!(ScreenFXConfig.totemOfUndyingDisable && value.item.is(Items.TOTEM_OF_UNDYING))) {
			original.call(instance, value);
		}
	}

	@ModifyArgs(
		method = "extractPlayerState",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ARGB;colorFromFloat(FFFF)I")
	)
	private void underwaterOverlay(Args args) {
		args.set(0, ScreenFXConfig.underwaterOpacity);
	}
}
//?}