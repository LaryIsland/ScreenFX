package com.laryisland.screenfx.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import static com.laryisland.screenfx.ScreenFX.MOD_ID;

public class ModMenuEntry implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> ScreenFXConfig.getScreen(parent, MOD_ID);
	}
}