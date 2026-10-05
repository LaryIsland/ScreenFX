package com.laryisland.screenfx.support;

import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

/** A plain {@link Args} for feeding vanilla's call arguments into @ModifyArgs handlers. */
public final class TestArgs extends Args {

	public TestArgs(Object... values) {
		super(values.clone());
	}

	@Override
	public <T> void set(int index, T value) {
		this.values[index] = value;
	}

	@Override
	public void setAll(Object... values) {
		System.arraycopy(values, 0, this.values, 0, values.length);
	}

	public float getFloat(int index) {
		return (Float) this.values[index];
	}
}
