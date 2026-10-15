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

    // Performance optimization: distant entity culling
    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void hypernova$cullDistantEntities(Entity entity, net.minecraft.client.render.Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (entity.squaredDistanceTo(x, y, z) > HypernovaConfig.maxEntityRenderDistSq) {
            cir.setReturnValue(false);
        }
    }

    // Clean Solid White 3D Combat Hitbox Renderer
    @Inject(method = "render", at = @At("TAIL"))
    private <E extends Entity> void hypernova$renderTrue3DHitbox(E entity, double x, double y, double z, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!HypernovaConfig.customHitboxEnabled || entity == client.player || entity.isInvisible()) {
            return;
        }

        matrices.push();
        matrices.translate(x, y, z);
        Box box = entity.getBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ());
        
        // Use debug lines layer for crisp rendering without glitches
        VertexConsumer lines = vertexConsumers.getBuffer(RenderLayer.getDebugFilledBox());

        // Pure clean white color with high visibility (No damage color change)
        float r = 1.0f;
        float g = 1.0f;
        float b = 1.0f;
        float alpha = 0.95f;

        VertexRendering.drawBox(matrices, lines, box, r, g, b, alpha);
        matrices.pop();
    }
}
