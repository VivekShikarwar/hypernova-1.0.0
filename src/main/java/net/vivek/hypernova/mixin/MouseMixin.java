package net.vivek.hypernova.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.input.MouseInput;
import net.vivek.hypernova.util.CpsTracker;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {
    @Inject(method = "onMouseButton", at = @At("HEAD"))
    private void hypernova$onMouseClick(long window, MouseInput input, int action, CallbackInfo ci) {
        if (action == GLFW.GLFW_PRESS && MinecraftClient.getInstance().currentScreen == null) {
            if (input.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                CpsTracker.registerLeftClick();
            } else if (input.button() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                CpsTracker.registerRightClick();
            }
        }
    }
}
