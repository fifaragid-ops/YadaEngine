package net.wander.utils.camera;
import java.awt.Graphics2D;

public class Camera {

    private double x;
    private double y;

    // ширина видимой области мира (в мировых единицах)
    private double fov;

    // зум = сколько пикселей на 1 мировую единицу
    private double zoom;

    private final int screenWidth;
    private final int screenHeight;

    private CameraTarget target; // то, к чему прикреплена камера (может быть null)
    private boolean destroyed = false; // флаг "камера уничтожена"

    public Camera(int screenWidth, int screenHeight, double initialFov) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        setFov(initialFov);
    }

    // Привязать камеру к объекту
    public void follow(CameraTarget target) {
        this.target = target;
    }

    // Отвязать камеру (свободная)
    public void unfollow() {
        this.target = null;
    }

    // Установить FOV (ширину видимой области в мире)
    public void setFov(double fov) {
        this.fov = fov;
        this.zoom = screenWidth / fov;
    }

    public double getFov() {
        return fov;
    }

    public double getZoom() {
        return zoom;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
        this.target = null; // если руками двигаем — отвязываем от цели
    }

    public double getX() { return x; }
    public double getY() { return y; }

    // Обновление камеры каждый кадр
    public void update(double dt) {
        if (destroyed) return;

        if (target != null) {
            // можно сделать плавное следование (lerp)
            double lerp = 0.1; // 0..1, чем больше — тем "жёстче" камера липнет
            x += (target.getX() - x) * lerp;
            y += (target.getY() - y) * lerp;
        }
    }

    public void apply(Graphics2D g) {
        if (destroyed) return;

        // 1. Сдвигаем начало координат в центр экрана
        g.translate(screenWidth / 2.0, screenHeight / 2.0);

        // 2. Масштабируем (zoom)
        g.scale(zoom, zoom);

        // 3. Сдвигаем мир так, чтобы камера смотрела на (x, y)
        g.translate(-x, -y);
    }

    public void destroy() {
        destroyed = true;
        target = null;

    }

    public boolean isDestroyed() {
        return destroyed;
    }
}