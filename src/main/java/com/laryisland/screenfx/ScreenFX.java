package com.laryisland.screenfx;

import com.laryisland.screenfx.config.ScreenFXConfig;
import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ClientModInitializer;

public class ScreenFX implements ClientModInitializer {

  public static final String MOD_ID = "screenfx";

  @Override
  public void onInitializeClient() {
    MidnightConfig.init(MOD_ID, ScreenFXConfig.class);
  }
}