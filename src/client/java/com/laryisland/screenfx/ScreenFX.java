package com.laryisland.screenfx;

import com.laryisland.screenfx.config.MidnightConfig;
import com.laryisland.screenfx.config.ScreenFXConfig;
import java.util.regex.Pattern;
import net.fabricmc.api.ClientModInitializer;

public class ScreenFX implements ClientModInitializer {

	public static final String MOD_ID = "screenfx";
	public static final Pattern validColour = Pattern.compile("#[0-9A-F]{2,6}", Pattern.CASE_INSENSITIVE);

	@Override
	public void onInitializeClient() {
		MidnightConfig.init(MOD_ID, ScreenFXConfig.class);
	}
}