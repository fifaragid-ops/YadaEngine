package net.wander.utils;

public class Counter {

    private double value = 0.0;

    private final boolean autoIncrement;
    private final double step;
    private final long periodMs;

    private double accMs = 0.0; // накопленное время в мс

    /**
     * @param autoIncrement включён ли авто-инкремент
     * @param step          на сколько увеличивать при каждом срабатывании
     * @param periodMs      период срабатывания в миллисекундах
     */

    public Counter(boolean autoIncrement, double step, long periodMs) {
        this.autoIncrement = autoIncrement;
        this.step = step;
        this.periodMs = periodMs;
    }


    public void update(double dt) {
        if (!autoIncrement) return;
        if (periodMs <= 0) return;

        accMs += dt * 1000.0;

        while (accMs >= periodMs) {
            accMs -= periodMs;
            value += step;
        }
    }

    public void add(double delta) {
        value += delta;
    }

    public double get() {
        return value;
    }

    public void set(double v) {
        value = v;
    }

    public void reset() {
        value = 0.0;
        accMs = 0.0;
    }
}