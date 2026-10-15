package net.vivek.hypernova;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.vivek.hypernova.config.HypernovaConfig;
import org.lwjgl.glfw.GLFW;

public class HypernovaClient implements ClientModInitializer {
    private static KeyBinding toggleHudKey;
    private static KeyBinding toggleHitboxKey;
    private static KeyBinding toggleMotionBlurKey;

    @Override
    public void onInitializeClient() {
        toggleHitboxKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.hypernova.toggle_hitbox",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F6,
                KeyBinding.Category.GAMEPLAY
        ));

        toggleMotionBlurKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.hypernova.toggle_motionblur",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F7,
                KeyBinding.Category.GAMEPLAY
        ));

        toggleHudKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.hypernova.toggle_hud",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F8,
                KeyBinding.Category.GAMEPLAY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleHitboxKey.wasPressed()) {
                HypernovaConfig.customHitboxEnabled = !HypernovaConfig.customHitboxEnabled;
                if (client.player != null) {
                    client.player.sendMessage(Text.literal("§b[Hypernova] §fCustom Hitboxes: " + (HypernovaConfig.customHitboxEnabled ? "§aON" : "§cOFF")), true);
                }
            }
            while (toggleMotionBlurKey.wasPressed()) {
                HypernovaConfig.motionBlurEnabled = !HypernovaConfig.motionBlurEnabled;
                if (client.player != null) {
                    client.player.sendMessage(Text.literal("§b[Hypernova] §fMotion Blur: " + (HypernovaConfig.motionBlurEnabled ? "§aON" : "§cOFF")), true);
                }
            }
            while (toggleHudKey.wasPressed()) {
                HypernovaConfig.hudEnabled = !HypernovaConfig.hudEnabled;
                if (client.player != null) {
                    client.player.sendMessage(Text.literal("§b[Hypernova] §fHUD Overlay: " + (HypernovaConfig.hudEnabled ? "§aON" : "§cOFF")), true);
                }
            }
        });
    }
}
