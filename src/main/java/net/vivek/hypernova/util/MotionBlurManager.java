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
        float yaw = client.player.getYaw();
        float pitch = client.player.getPitch();

        float dyaw = Math.abs(yaw - lastYaw);
        float dpitch = Math.abs(pitch - lastPitch);
        lastYaw = yaw;
        lastPitch = pitch;

        if (dyaw > 100.0f) dyaw = 0.0f;

        float speed = Math.min(1.0f, (dyaw + dpitch) / 20.0f);
        currentBlurFactor = (currentBlurFactor * 0.75f) + (speed * HypernovaConfig.motionBlurStrength * 0.25f);
    }

    public static void renderLightweightMotionTrail(DrawContext context, int width, int height) {
        MinecraftClient client = MinecraftClient.getInstance();
        updateCameraMotion(client);

        if (!HypernovaConfig.motionBlurEnabled || currentBlurFactor <= 0.02f) {
            return;
        }

        // Clean subtle overlay without any black side bands
        int alpha = Math.min(35, (int) (currentBlurFactor * 45.0f));
        if (alpha > 2) {
            context.fill(0, 0, width, height, alpha << 24);
        }
    }
}
