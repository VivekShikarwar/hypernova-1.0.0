package net.vivek.hypernova.mixin;

import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShapes;
import net.vivek.hypernova.config.HypernovaConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public class EntityHitboxMixin {

    // Performance optimization: distant entity culling
    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void hypernova$cullDistantEntities(Entity entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (entity.squaredDistanceTo(x, y, z) > HypernovaConfig.maxEntityRenderDistSq) {
            cir.setReturnValue(false);
        }
    }

    // Clean Solid White 3D Combat Hitbox Renderer
    @Inject(method = "render", at = @At("TAIL"))
    private void hypernova$renderTrue3DHitbox(Entity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (!HypernovaConfig.customHitboxEnabled || entity == net.minecraft.client.MinecraftClient.getInstance().player || entity.isInvisible() || !(entity instanceof LivingEntity)) {
            return;
        }

        matrices.push();
        Box box = entity.getBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ());
        VertexConsumer lines = vertexConsumers.getBuffer(RenderLayers.LINES);

        // Pure clean solid white color
        int whiteColor = 0xFFAFFFFF;

        VertexRendering.drawOutline(matrices, lines, VoxelShapes.cuboid(box), 0.0, 0.0, 0.0, whiteColor, 2.0f);
        matrices.pop();
    }
}
