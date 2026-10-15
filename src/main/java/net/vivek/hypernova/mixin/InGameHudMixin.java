package net.vivek.hypernova.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.vivek.hypernova.config.HypernovaConfig;
import net.vivek.hypernova.util.CpsTracker;
import net.vivek.hypernova.util.MotionBlurManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method = "render", at = @At("TAIL"))
    private void hypernova$renderHud(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) {
            return;
        }

        CpsTracker.updateFromWindow(client);

        int screenWidth = context.getScaledWindowWidth();
        int screenHeight = context.getScaledWindowHeight();

        // Render Clean Hitboxes (White normally, Red when hurt)
        if (HypernovaConfig.customHitboxEnabled && client.world != null) {
            hypernova$renderCleanHitboxes(context, client, screenWidth, screenHeight);
        }

        MotionBlurManager.renderLightweightMotionTrail(context, screenWidth, screenHeight);

        if (client.getDebugHud().shouldShowDebugHud() || !HypernovaConfig.hudEnabled) {
            return;
        }

        int fps = client.getCurrentFps();
        int lmb = CpsTracker.getLeftCps();
        int rmb = CpsTracker.getRightCps();

        String hudText = String.format("%d FPS | %d : %d CPS", fps, lmb, rmb);
        int textWidth = client.textRenderer.getWidth(hudText);

        int x = 6;
        int y = 6;
        if (HypernovaConfig.hudPosition == 1) {
            x = screenWidth - textWidth - 6;
        } else if (HypernovaConfig.hudPosition == 2) {
            y = screenHeight - 16;
        }

        context.drawTextWithShadow(client.textRenderer, hudText, x, y, 0xFFFFFFFF);
    }

    @Unique
    private void hypernova$renderCleanHitboxes(DrawContext context, MinecraftClient client, int screenWidth, int screenHeight) {
        double camX = client.player.getX();
        double camY = client.player.getEyeY();
        double camZ = client.player.getZ();

        double yawRad = Math.toRadians(client.player.getYaw());
        double pitchRad = Math.toRadians(client.player.getPitch());
        double sinYaw = Math.sin(yawRad);
        double cosYaw = Math.cos(yawRad);
        double sinPitch = Math.sin(pitchRad);
        double cosPitch = Math.cos(pitchRad);

        double fov = client.options.getFov().getValue();
        double focalLength = (screenHeight / 2.0) / Math.tan(Math.toRadians(fov / 2.0));

        for (Entity entity : client.world.getEntities()) {
            if (entity == client.player || entity.isInvisible() || !(entity instanceof LivingEntity living)) {
                continue;
            }
            if (entity.squaredDistanceTo(client.player) > 1600.0) {
                continue;
            }

            Box box = entity.getBoundingBox();
            double[] xs = {box.minX, box.maxX};
            double[] ys = {box.minY, box.maxY};
            double[] zs = {box.minZ, box.maxZ};

            int minScreenX = Integer.MAX_VALUE;
            int minScreenY = Integer.MAX_VALUE;
            int maxScreenX = Integer.MIN_VALUE;
            int maxScreenY = Integer.MIN_VALUE;
            int visibleCorners = 0;

            for (double wx : xs) {
                for (double wy : ys) {
                    for (double wz : zs) {
                        double dx = wx - camX;
                        double dy = wy - camY;
                        double dz = wz - camZ;

                        double rightX = -cosYaw * dx - sinYaw * dz;
                        double forwardZ = -sinYaw * dx + cosYaw * dz;
                        double depthZ = forwardZ * cosPitch - dy * sinPitch;
                        double upY = dy * cosPitch + forwardZ * sinPitch;

                        if (depthZ > 0.2) {
                            int sx = (int) ((screenWidth / 2.0) + (rightX / depthZ) * focalLength);
                            int sy = (int) ((screenHeight / 2.0) - (upY / depthZ) * focalLength);
                            minScreenX = Math.min(minScreenX, sx);
                            minScreenY = Math.min(minScreenY, sy);
                            maxScreenX = Math.max(maxScreenX, sx);
                            maxScreenY = Math.max(maxScreenY, sy);
                            visibleCorners++;
                        }
                    }
                }
            }

            if (visibleCorners == 8 && maxScreenX > 0 && minScreenX < screenWidth && maxScreenY > 0 && minScreenY < screenHeight) {
                int boxW = maxScreenX - minScreenX;
                int boxH = maxScreenY - minScreenY;
                if (boxW < 2 || boxH < 2 || boxW > screenWidth || boxH > screenHeight) {
                    continue;
                }

                // Normal color White, turns Red when hurt/attacked
                int boxColor = (living.hurtTime > 0) ? 0xFFFF2222 : 0xFFFFFFFF;

                // Clean Box borders (No eye-height line)
                context.fill(minScreenX, minScreenY, maxScreenX, minScreenY + 1, boxColor);
                context.fill(minScreenX, maxScreenY - 1, maxScreenX, maxScreenY, boxColor);
                context.fill(minScreenX, minScreenY, minScreenX + 1, maxScreenY, boxColor);
                context.fill(maxScreenX - 1, minScreenY, maxScreenX, maxScreenY, boxColor);
            }
        }
    }
}
