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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public class EntityHitboxMixin {

    // Performance entity culling
    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void hypernova$cullDistantEntities(Entity entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (entity.squaredDistanceTo(x, y, z) > HypernovaConfig.maxEntityRenderDistSq) {
            cir.setReturnValue(false);
        }
    }

    // Smooth Combat Hitboxes expansion for PvP & 3D Box scaling
    @Inject(method = "getBoundingBox", at = @At("RETURN"), cancellable = true)
    private void hypernova$combatHitbox(Entity entity, CallbackInfoReturnable<Box> cir) {
        if (HypernovaConfig.customHitboxEnabled && cir.getReturnValue() != null && entity instanceof LivingEntity && entity != net.minecraft.client.MinecraftClient.getInstance().player) {
            cir.setReturnValue(cir.getReturnValue().expand(0.08));
        }
    }
}
