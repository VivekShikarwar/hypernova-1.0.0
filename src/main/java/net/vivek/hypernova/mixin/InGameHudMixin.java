package net.vivek.hypernova.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.vivek.hypernova.config.HypernovaConfig;
import net.vivek.hypernova.util.CpsTracker;
import net.vivek.hypernova.util.MotionBlurManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method = "render", at = @At("TAIL"))
    private void hypernova$renderHud(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getDebugHud().shouldShowDebugHud()) {
            return;
        }

        // Motion Blur Pass
        MotionBlurManager.renderLightweightMotionTrail(
                context,
                context.getScaledWindowWidth(),
                context.getScaledWindowHeight()
        );

        if (!HypernovaConfig.hudEnabled) {
            return;
        }

        int fps = client.getCurrentFps();
        int lmb = CpsTracker.getLeftCps();
        int rmb = CpsTracker.getRightCps();

        String hudText = String.format("Hypernova | %d FPS | %d : %d CPS", fps, lmb, rmb);
        int textWidth = client.textRenderer.getWidth(hudText);

        // Background box + Accent bar
        context.fill(6, 6, 14 + textWidth, 20, 0x90000000);
        context.fill(6, 6, 8, 20, 0xFF00FFD5);

        // Crisp HUD text
        context.drawTextWithShadow(client.textRenderer, hudText, 11, 9, 0xFFFFFF);
    }
}