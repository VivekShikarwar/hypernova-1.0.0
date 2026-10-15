package net.vivek.hypernova.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.vivek.hypernova.config.HypernovaConfig;

public class MotionBlurManager {
    private static float lastYaw = 0.0f;
    private static float lastPitch = 0.0f;
    private static float smoothYawSpeed = 0.0f;
    private static float smoothPitchSpeed = 0.0f;
    private static float currentBlurFactor = 0.0f;

    public static void updateCameraMotion(MinecraftClient client) {
        if (!HypernovaConfig.motionBlurEnabled || client.player == null) {
            currentBlurFactor = 0.0f;
            smoothYawSpeed = 0.0f;
            smoothPitchSpeed = 0.0f;
            return;
        }
        float yaw = client.player.getYaw();
        float pitch = client.player.getPitch();

        float dyaw = yaw - lastYaw;
        float dpitch = pitch - lastPitch;
        lastYaw = yaw;
        lastPitch = pitch;

        if (Math.abs(dyaw) > 120.0f) dyaw = 0.0f;

        smoothYawSpeed = smoothYawSpeed * 0.7f + dyaw * 0.3f;
        smoothPitchSpeed = smoothPitchSpeed * 0.7f + dpitch * 0.3f;

        float totalSpeed = (float) Math.sqrt(smoothYawSpeed * smoothYawSpeed + smoothPitchSpeed * smoothPitchSpeed);
        float targetBlur = Math.min(1.0f, totalSpeed / 7.5f) * HypernovaConfig.motionBlurStrength;
        currentBlurFactor = (currentBlurFactor * 0.78f) + (targetBlur * 0.22f);
    }

    public static void renderLightweightMotionTrail(DrawContext context, int width, int height) {
        MinecraftClient client = MinecraftClient.getInstance();
        updateCameraMotion(client);

        if (!HypernovaConfig.motionBlurEnabled || currentBlurFactor <= 0.01f) {
            return;
        }

        int baseAlpha = Math.min(115, (int) (currentBlurFactor * 145.0f));
        if (baseAlpha <= 3) return;

        // Full screen temporal smoothing pass
        context.fill(0, 0, width, height, (baseAlpha / 3) << 24);

        // Directional peripheral motion blur bands
        int edgeBand = Math.max(14, (int) (width * 0.09f * currentBlurFactor));
        int edgeColor = (baseAlpha << 24);
        int midColor = ((baseAlpha / 2) << 24);

        context.fill(0, 0, edgeBand, height, edgeColor);
        context.fill(edgeBand, 0, edgeBand * 2, height, midColor);
        context.fill(width - edgeBand, 0, width, height, edgeColor);
        context.fill(width - edgeBand * 2, 0, width - edgeBand, height, midColor);

        int vBand = Math.max(10, (int) (height * 0.06f * currentBlurFactor));
        context.fill(0, 0, width, vBand, midColor);
        context.fill(0, height - vBand, width, height, midColor);
    }
}
