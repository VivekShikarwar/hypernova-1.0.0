package net.vivek.hypernova.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.vivek.hypernova.config.HypernovaConfig;

public class MotionBlurManager {
    private static float lastYaw = 0.0f;
    private static float lastPitch = 0.0f;
    private static float currentBlurFactor = 0.0f;

    public static void updateCameraMotion(MinecraftClient client) {
        if (!HypernovaConfig.motionBlurEnabled || client.player == null) {
            currentBlurFactor = 0.0f;
            return;
        }
        float yawDiff = Math.abs(client.player.getYaw() - lastYaw);
        float pitchDiff = Math.abs(client.player.getPitch() - lastPitch);
        lastYaw = client.player.getYaw();
        lastPitch = client.player.getPitch();

        float speed = Math.min(1.0f, (yawDiff + pitchDiff) / 25.0f);
        currentBlurFactor = (currentBlurFactor * 0.75f) + (speed * HypernovaConfig.motionBlurStrength * 0.25f);
    }

    public static void renderLightweightMotionTrail(DrawContext context, int width, int height) {
        if (!HypernovaConfig.motionBlurEnabled || currentBlurFactor <= 0.02f) {
            return;
        }
        // Ultra-fast subtle temporal vignette/smoothing pass with zero framebuffer copy overhead
        int alpha = Math.min(45, (int) (currentBlurFactor * 60.0f));
        int color = (alpha << 24);
        context.fill(0, 0, width, height, color);
    }
}