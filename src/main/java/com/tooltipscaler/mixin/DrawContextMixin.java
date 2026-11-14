package com.tooltipscaler.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.tooltipscaler.TooltipScalerConfig;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2ic;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(DrawContext.class)
public abstract class DrawContextMixin {

    @WrapMethod(method = "drawTooltipImmediately")
    private void tooltipscaler$fit(TextRenderer textRenderer, List<TooltipComponent> components,
                                   int x, int y, TooltipPositioner positioner,
                                   @Nullable Identifier texture, Operation<Void> original) {
        TooltipScalerConfig cfg = TooltipScalerConfig.INSTANCE;
        DrawContext self = (DrawContext) (Object) this;

        if (!cfg.enabled || components.isEmpty()) {
            original.call(textRenderer, components, x, y, positioner, texture);
            return;
        }

        int w = 0, h = components.size() > 1 ? 2 : 0;
        for (TooltipComponent c : components) {
            w = Math.max(w, c.getWidth(textRenderer));
            h += c.getHeight(textRenderer);
        }
        w += 8;
        h += 8;

        int sw = self.getScaledWindowWidth();
        int sh = self.getScaledWindowHeight();
        float s = Math.min(1f, Math.min(
                (sw - 2f * cfg.margin) / w,
                (sh - 2f * cfg.margin) / h));
        s = Math.max(cfg.minScale, s);

        if (s >= 0.999f) {
            original.call(textRenderer, components, x, y, positioner, texture);
            return;
        }

        final float scale = s;
        TooltipPositioner scaled = (screenW, screenH, mx, my, tw, th) -> {
            Vector2ic p = positioner.getPosition(Math.round(screenW / scale), Math.round(screenH / scale), mx, my, tw, th);
            return p;
        };

        self.getMatrices().pushMatrix();
        self.getMatrices().scale(scale, scale);
        original.call(textRenderer, components, Math.round(x / scale), Math.round(y / scale), scaled, texture);
        self.getMatrices().popMatrix();
    }
}
