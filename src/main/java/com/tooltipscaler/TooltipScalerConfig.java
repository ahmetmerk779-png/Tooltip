package com.tooltipscaler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** config/tooltipscaler.json */
public final class TooltipScalerConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("tooltipscaler.json");

    public static TooltipScalerConfig INSTANCE = new TooltipScalerConfig();

    public boolean enabled = true;
    public float minScale = 0.35f;
    public int margin = 4;

    public static void load() {
        try {
            if (Files.exists(FILE)) {
                TooltipScalerConfig c = GSON.fromJson(Files.readString(FILE), TooltipScalerConfig.class);
                if (c != null) INSTANCE = c;
            }
            INSTANCE.minScale = Math.max(0.1f, Math.min(1f, INSTANCE.minScale));
            INSTANCE.margin = Math.max(0, INSTANCE.margin);
            Files.writeString(FILE, GSON.toJson(INSTANCE));
        } catch (IOException | RuntimeException e) {
            System.err.println("[TooltipScaler] Config okunamadı: " + e);
        }
    }
}
