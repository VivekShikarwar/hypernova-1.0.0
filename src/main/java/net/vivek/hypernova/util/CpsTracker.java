package net.vivek.hypernova.util;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayDeque;
import java.util.Deque;

public class CpsTracker {
    private static final Deque<Long> LEFT_CLICKS = new ArrayDeque<>(64);
    private static final Deque<Long> RIGHT_CLICKS = new ArrayDeque<>(64);
    private static boolean wasLeftPressed = false;
    private static boolean wasRightPressed = false;

    public static void updateFromWindow(MinecraftClient client) {
        if (client == null || client.getWindow() == null || client.currentScreen != null) {
            wasLeftPressed = false;
            wasRightPressed = false;
            return;
        }
        long handle = client.getWindow().getHandle();
        boolean leftDown = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean rightDown = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;

        if (leftDown && !wasLeftPressed) {
            registerLeftClick();
        }
        if (rightDown && !wasRightPressed) {
            registerRightClick();
        }

        wasLeftPressed = leftDown;
        wasRightPressed = rightDown;
    }

    public static void registerLeftClick() {
        long now = System.currentTimeMillis();
        if (LEFT_CLICKS.isEmpty() || now - LEFT_CLICKS.peekLast() > 15L) {
            LEFT_CLICKS.addLast(now);
        }
    }

    public static void registerRightClick() {
        long now = System.currentTimeMillis();
        if (RIGHT_CLICKS.isEmpty() || now - RIGHT_CLICKS.peekLast() > 15L) {
            RIGHT_CLICKS.addLast(now);
        }
    }

    public static int getLeftCps() {
        return cleanAndCount(LEFT_CLICKS);
    }

    public static int getRightCps() {
        return cleanAndCount(RIGHT_CLICKS);
    }

    private static int cleanAndCount(Deque<Long> clicks) {
        long cutoff = System.currentTimeMillis() - 1000L;
        while (!clicks.isEmpty() && clicks.peekFirst() < cutoff) {
            clicks.pollFirst();
        }
        return clicks.size();
    }
}
