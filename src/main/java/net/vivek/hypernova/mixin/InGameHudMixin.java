package net.vivek.hypernova.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShapes;
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
        if (client == null || client.player == null) {
            return;
        }

        // 1. Update CPS clicks every frame
        CpsTracker.updateFromWindow(client);

        int screenWidth = context.getScaledWindowWidth();
        int screenHeight = context.getScaledWindowHeight();

        // 2. Render Motion Blur
        MotionBlurManager.renderLightweightMotionTrail(context, screenWidth, screenHeight);

        if (client.getDebugHud().shouldShowDebugHud() || !HypernovaConfig.hudEnabled) {
            return;
        }

        // 3. Clean FPS & CPS HUD (No background, No "Hypernova" text, Custom Position support)
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

        // Render clean text with shadow and accent color
        context.drawTextWithShadow(client.textRenderer, hudText, x, y, 0xFFFFFFFF);
    }
}
