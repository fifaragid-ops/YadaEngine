package net.wander.utils;

public final class MathUtil {

    private MathUtil() {} // утилитный класс

    /**
     * Линейная интерполяция между a и b.
     * t в диапазоне [0..1]:
     *  t = 0  -> вернёт a
     *  t = 1  -> вернёт b
     *  t = 0.5 -> середина между a и b
     */
    public static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }

    public static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }
}
