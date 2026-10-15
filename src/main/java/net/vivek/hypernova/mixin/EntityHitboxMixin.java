package net.vivek.hypernova.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.vivek.hypernova.config.HypernovaConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public class EntityHitboxMixin {

    // Performance boost: Skip rendering entities very far away
    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private <E extends Entity> void hypernova$cullDistantEntities(E entity, net.minecraft.client.render.Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (entity.squaredDistanceTo(x, y, z) > HypernovaConfig.maxEntityRenderDistSq) {
            cir.setReturnValue(false);
        }
    }

    // Custom Hitbox Overlay
    @Inject(method = "render", at = @At("TAIL"))
    private <E extends Entity> void hypernova$renderCustomHitbox(E entity, double x, double y, double z, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!HypernovaConfig.customHitboxEnabled || entity == client.player || entity.isInvisible()) {
            return;
        }

        matrices.push();
        matrices.translate(x, y, z);
        Box box = entity.getBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ());
        VertexConsumer lines = vertexConsumers.getBuffer(RenderLayer.getLines());

        // Highlight red when entity takes damage, otherwise custom cyan
        boolean hurt = (entity instanceof net.minecraft.entity.LivingEntity living && living.hurtTime > 0);
        float r = hurt ? 1.0f : HypernovaConfig.hitboxRed;
        float g = hurt ? 0.1f : HypernovaConfig.hitboxGreen;
        float b = hurt ? 0.1f : HypernovaConfig.hitboxBlue;

        VertexRendering.drawBox(matrices, lines, box, r, g, b, HypernovaConfig.hitboxAlpha);
        matrices.pop();
    }
}