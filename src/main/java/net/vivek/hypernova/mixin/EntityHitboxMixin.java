package net.vivek.hypernova.mixin;

import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShapes;
import net.vivek.hypernova.config.HypernovaConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public class EntityHitboxMixin {

    // Performance boost: Skip rendering entities very far away
    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void hypernova$cullDistantEntities(Entity entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (entity.squaredDistanceTo(x, y, z) > HypernovaConfig.maxEntityRenderDistSq) {
            cir.setReturnValue(false);
        }
    }

    // Custom Hitbox Bounding Box adjustment
    @Inject(method = "getBoundingBox", at = @At("RETURN"), cancellable = true)
    private void hypernova$customBoundingBox(Entity entity, CallbackInfoReturnable<Box> cir) {
        if (HypernovaConfig.customHitboxEnabled && cir.getReturnValue() != null) {
            cir.setReturnValue(cir.getReturnValue().expand(0.02));
        }
    }

    @Unique
    private static void hypernova$drawCustomBox(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Box box, int color) {
        VertexConsumer lines = vertexConsumers.getBuffer(RenderLayers.LINES);
        VertexRendering.drawOutline(matrices, lines, VoxelShapes.cuboid(box), 0.0, 0.0, 0.0, color, 2.0f);
    }
}
