package net.vivek.hypernova.config;

public class HypernovaConfig {
    public static boolean hudEnabled = true;
    public static boolean customHitboxEnabled = true;
    public static boolean motionBlurEnabled = true;

    // Motion Blur strength (0.0f to 0.90f)
    public static float motionBlurStrength = 0.65f;

    // Custom Hitbox RGBA Color (Default: Neon Cyan/Red PvP style)
    public static float hitboxRed = 0.0f;
    public static float hitboxGreen = 1.0f;
    public static float hitboxBlue = 0.85f;
    public static float hitboxAlpha = 0.9f;

    // Performance optimization: distant entity culling distance squared (64 blocks)
    public static double maxEntityRenderDistSq = 4096.0;
}