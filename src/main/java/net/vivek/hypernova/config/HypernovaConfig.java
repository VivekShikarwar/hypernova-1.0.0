package net.vivek.hypernova.config;

public class HypernovaConfig {
    public static boolean hudEnabled = true;
    public static boolean customHitboxEnabled = true;
    public static boolean motionBlurEnabled = true;
    public static float motionBlurStrength = 0.65f;
    public static int hudPosition = 0;
    public static int hudAccentColor = 0xFF00FFD5;
    public static String hudThemeName = "Cyan";
    public static double maxEntityRenderDistSq = 4096.0;

    public static void cycleBlurStrength() {
        if (motionBlurStrength < 0.5f) {
            motionBlurStrength = 0.65f;
        } else if (motionBlurStrength < 0.8f) {
            motionBlurStrength = 0.85f;
        } else {
            motionBlurStrength = 0.35f;
        }
    }

    public static String getBlurStrengthLabel() {
        if (motionBlurStrength < 0.5f) return "Low (35%)";
        if (motionBlurStrength < 0.8f) return "Medium (65%)";
        return "High (85%)";
    }

    public static void cycleHudPosition() {
        hudPosition = (hudPosition + 1) % 3;
    }

    public static String getHudPositionLabel() {
        return switch (hudPosition) {
            case 1 -> "Top-Right";
            case 2 -> "Bottom-Left";
            default -> "Top-Left";
        };
    }

    public static void cycleHudTheme() {
        switch (hudThemeName) {
            case "Cyan" -> {
                hudThemeName = "Purple";
                hudAccentColor = 0xFFA855F7;
            }
            case "Purple" -> {
                hudThemeName = "Crimson";
                hudAccentColor = 0xFFEF4444;
            }
            case "Crimson" -> {
                hudThemeName = "Gold";
                hudAccentColor = 0xFFFACC15;
            }
            default -> {
                hudThemeName = "Cyan";
                hudAccentColor = 0xFF00FFD5;
            }
        }
    }
}
