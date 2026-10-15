package net.vivek.hypernova.util;

import java.util.ArrayDeque;
import java.util.Deque;

public class CpsTracker {
    private static final Deque<Long> LEFT_CLICKS = new ArrayDeque<>(64);
    private static final Deque<Long> RIGHT_CLICKS = new ArrayDeque<>(64);

    public static void registerLeftClick() {
        LEFT_CLICKS.addLast(System.currentTimeMillis());
    }

    public static void registerRightClick() {
        RIGHT_CLICKS.addLast(System.currentTimeMillis());
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