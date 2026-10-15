package net.vivek.hypernova.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShapes;
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

    // Clean Solid White 3D Combat Hitbox Renderer for Minecraft 1.21.11
    @Inject(method = "render", at = @At("TAIL"))
    private <E extends Entity> void hypernova$renderTrue3DHitbox(E entity, double x, double y, double z, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!HypernovaConfig.customHitboxEnabled || entity == client.player || entity.isInvisible()) {
            return;
        }

        matrices.push();
        matrices.translate(x, y, z);
        Box box = entity.getBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ());
        
        VertexConsumer lines = vertexConsumers.getBuffer(RenderLayers.LINES);

        // Pure clean solid white color (ARGB format: White with high opacity)
        int whiteColor = 0xFFAFFFFF;

        VertexRendering.drawOutline(matrices, lines, VoxelShapes.cuboid(box), 0.0, 0.0, 0.0, whiteColor, 2.0f);
        matrices.pop();
    }
}
