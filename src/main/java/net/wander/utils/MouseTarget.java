package net.wander.utils;

/**
 * Любой объект, по которому можно "навести/кликнуть" мышью.
 * Координаты и размеры в твоей системе (обычно пиксели).
 */
public interface MouseTarget {
    abstract double getX();
    abstract double getY();
    abstract double getWidth();
    abstract double getHeight();
}