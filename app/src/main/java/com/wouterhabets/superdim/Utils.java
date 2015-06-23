package com.wouterhabets.superdim;

public class Utils {
    private Utils() {
    }

    public static String toHex(int level) {
        return toHex(level, 0, 0, 0);
    }

    private static String toHex(int a, int r, int g, int b) {
        a = (int) map(a, 0, 100, 0, 225);
        String alpha = pad(Integer.toHexString(a));
        String red = pad(Integer.toHexString(r));
        String green = pad(Integer.toHexString(g));
        String blue = pad(Integer.toHexString(b));
        return "#" + alpha + red + blue + green;
    }

    private static String pad(String s) {
        return (s.length() == 1) ? "0" + s : s;
    }

    private static long map(int x, int in_min, int in_max, int out_min, int out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }
}
