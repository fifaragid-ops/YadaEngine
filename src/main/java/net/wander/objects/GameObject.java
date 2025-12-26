package net.wander.objects;

import net.wander.utils.Mouse;
import net.wander.utils.MouseTarget;
import net.wander.utils.Scene;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Базовый игровой объект.
 * По нему строятся все остальные (игрок, враг, кнопка и т.д.).
 */
public class GameObject implements MouseTarget {

    // --- положение и размер (в твоей системе координат, обычно в пикселях) ---
    protected double x;
    protected double y;
    protected double width;
    protected double height;

    // --- скорость ---
    protected double vx;
    protected double vy;

    // --- поворот и масштаб ---
    // поворот в радианах (0 по умолчанию)
    protected double rotation = 0.0;
    protected double scaleX = 1.0;
    protected double scaleY = 1.0;

    // --- логика жизни ---
    protected boolean active    = true;   // участвует в update?
    protected boolean visible   = true;   // рисуется?
    protected boolean destroyed = false;  // помечен на удаление?

    // --- доп. инфа ---
    protected Scene scene;       // сцена, в которой живёт объект
    protected String name;       // опциональное имя
    protected String tag;        // опциональный тег (типа "player", "enemy")
    protected int layer = 0;     // слой отрисовки (чем больше, тем "выше")

    // ================== КОНСТРУКТОРЫ ==================

    public GameObject() {
    }

    public GameObject(double x, double y, double width, double height) {
        this.x      = x;
        this.y      = y;
        this.width  = width;
        this.height = height;
    }

    // ================== ЖИЗНЕННЫЙ ЦИКЛ ==================

    /**
     * Обновление логики.
     * dt — дельта в секундах.
     * По умолчанию просто двигает объект по скорости.
     * Переопределяй в наследниках, не забыв вызвать super.update(dt), если надо движение по скорости.
     */
    public void update(double dt) {
        if (!active || destroyed) return;
        x += vx * dt;
        y += vy * dt;
    }

    /**
     * Отрисовка объекта.
     * Переопределяй в наследниках.
     */
    public void render(Graphics2D g) {
        if (!visible || destroyed) return;
        // по умолчанию ничего не рисуем
    }

    /**
     * Помечает объект как уничтоженный.
     * Сцена/движок должны сами убрать destroyed-объекты из списка.
     */
    public void destroy() {
        destroyed = true;
    }

    // ================== ПОЛОЖЕНИЕ / РАЗМЕР ==================

    public double getX() { return x; }
    public double getY() { return y; }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /** Сдвинуть объект на dx, dy. */
    public void moveBy(double dx, double dy) {
        this.x += dx;
        this.y += dy;
    }

    public double getWidth()  { return width; }
    public double getHeight() { return height; }

    public void setWidth(double width)   { this.width = width; }
    public void setHeight(double height) { this.height = height; }

    public void setSize(double width, double height) {
        this.width  = width;
        this.height = height;
    }

    // Центр объекта

    public double getCenterX() {
        return x + width * 0.5;
    }

    public double getCenterY() {
        return y + height * 0.5;
    }

    public void setCenter(double cx, double cy) {
        this.x = cx - width * 0.5;
        this.y = cy - height * 0.5;
    }

    // ================== СКОРОСТЬ ==================

    public double getVx() { return vx; }
    public double getVy() { return vy; }

    public void setVx(double vx) { this.vx = vx; }
    public void setVy(double vy) { this.vy = vy; }

    public void setVelocity(double vx, double vy) {
        this.vx = vx;
        this.vy = vy;
    }

    public void addVelocity(double dvx, double dvy) {
        this.vx += dvx;
        this.vy += dvy;
    }

    // ================== ПОВОРОТ / МАСШТАБ ==================

    public double getRotation() {
        return rotation;
    }

    /** Установить поворот в радианах. */
    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    /** Повернуть на угол (радианы). */
    public void rotateBy(double dr) {
        this.rotation += dr;
    }

    public double getScaleX() { return scaleX; }
    public double getScaleY() { return scaleY; }

    public void setScale(double scale) {
        this.scaleX = scale;
        this.scaleY = scale;
    }

    public void setScale(double sx, double sy) {
        this.scaleX = sx;
        this.scaleY = sy;
    }

    // ================== АКТИВНОСТЬ / ВИДИМОСТЬ ==================

    public boolean isActive() {
        return active && !destroyed;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isVisible() {
        return visible && !destroyed;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    // ================== СЦЕНА / МЕТАДАННЫЕ ==================

    public Scene getScene() {
        return scene;
    }

    /** Сцена сама должна вызывать это при добавлении объекта. */
    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public String getName()          { return name; }
    public void setName(String name) { this.name = name; }

    public String getTag()        { return tag; }
    public void setTag(String tag){ this.tag = tag; }

    public int getLayer()             { return layer; }
    public void setLayer(int layer)   { this.layer = layer; }

    // ================== КОЛЛИЗИИ / ГЕОМЕТРИЯ ==================

    /** Прямоугольный хитбокс объекта. */
    public Rectangle2D getBounds() {
        return new Rectangle2D.Double(x, y, width, height);
    }

    /** Содержит ли объект точку (px, py)? */
    public boolean contains(double px, double py) {
        return px >= x && px <= x + width &&
                py >= y && py <= y + height;
    }

    /** Прямоугольники двух объектов пересекаются? */
    public boolean intersects(GameObject other) {
        if (other == null) return false;
        return this.getBounds().intersects(other.getBounds());
    }

    /** Расстояние до другого объекта (по центрам). */
    public double distanceTo(GameObject other) {
        if (other == null) return Double.POSITIVE_INFINITY;
        double dx = this.getCenterX() - other.getCenterX();
        double dy = this.getCenterY() - other.getCenterY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    /** Расстояние до точки. */
    public double distanceTo(double px, double py) {
        double dx = this.getCenterX() - px;
        double dy = this.getCenterY() - py;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /** Угол (радианы) от этого объекта к точке (px,py). */
    public double angleTo(double px, double py) {
        double dx = px - getCenterX();
        double dy = py - getCenterY();
        return Math.atan2(dy, dx);
    }

    /** Повернуться лицом к точке. */
    public void lookAt(double px, double py) {
        this.rotation = angleTo(px, py);
    }

    // ================== MouseTarget (наведение/клики) ==================


    /** Наведена ли мышь на этот объект? */
    public boolean isMouseOver() {
        return Mouse.isHover(this);
    }

    /** Кликнута ли ЛКМ по объекту в этом кадре? */
    public boolean isLeftClicked() {
        return Mouse.isButtonPressedOn(java.awt.event.MouseEvent.BUTTON1, this);
    }

    /** Кликнута ли ПКМ по объекту в этом кадре? */
    public boolean isRightClicked() {
        return Mouse.isButtonPressedOn(java.awt.event.MouseEvent.BUTTON3, this);
    }
}