package com.tooltipscaler;

import net.fabricmc.api.ClientModInitializer;

public class TooltipScalerClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        TooltipScalerConfig.load();
    }
}
