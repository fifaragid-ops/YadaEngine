package net.wander.utils;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Arrays;

/**
 * Глобальный ввод мыши.
 *
 * Использование:
 *   1) В SimpleGame.start() вызвать Mouse.attach(this);
 *   2) В SimpleGame.run() в начале кадра вызывать Mouse.nextFrame();
 *   3) В update/render:
 *        Mouse.getX(), Mouse.getY()
 *        Mouse.isButtonDown(MouseEvent.BUTTON1)
 *        Mouse.isButtonPressed(MouseEvent.BUTTON1)
 *        Mouse.isHoverRect(...)
 *        Mouse.isButtonPressedOn(MouseEvent.BUTTON1, myTarget)
 */
public class Mouse extends MouseAdapter implements MouseMotionListener {

    // Кнопки: индекс = MouseEvent.BUTTON1..BUTTON5 (по факту 1..3, но берём с запасом)
    private static final int MAX_BUTTON = 7;
    private static final boolean[] buttonsDown     = new boolean[MAX_BUTTON + 1];
    private static final boolean[] buttonsPressed  = new boolean[MAX_BUTTON + 1]; // "в этом кадре"
    private static final boolean[] buttonsReleased = new boolean[MAX_BUTTON + 1]; // "в этом кадре"

    // Позиция курсора относительно Canvas
    private static int mouseX = 0;
    private static int mouseY = 0;

    private Mouse() {}

    /** Подключить мышь к компоненту (Canvas из SimpleGame). */
    public static void attach(Component c) {
        Mouse m = new Mouse();
        c.addMouseListener(m);
        c.addMouseMotionListener(m);
    }

    /** Вызвать ОДИН раз за кадр в игровом цикле (очищает флаги pressed/released). */
    public static void nextFrame() {
        Arrays.fill(buttonsPressed,  false);
        Arrays.fill(buttonsReleased, false);
    }

    // ====== События мыши (внутренние) ======

    @Override
    public void mousePressed(MouseEvent e) {
        int b = e.getButton();
        if (b >= 0 && b <= MAX_BUTTON) {
            buttonsDown[b] = true;
            buttonsPressed[b] = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int b = e.getButton();
        if (b >= 0 && b <= MAX_BUTTON) {
            buttonsDown[b] = false;
            buttonsReleased[b] = true;
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    // ====== Публичное API ======

    public static int getX() { return mouseX; }
    public static int getY() { return mouseY; }

    /** Кнопка сейчас зажата? */
    public static boolean isButtonDown(int button) {
        if (button < 0 || button > MAX_BUTTON) return false;
        return buttonsDown[button];
    }

    /** Кнопка была нажата в этом кадре? (с момента последнего nextFrame()) */
    public static boolean isButtonPressed(int button) {
        if (button < 0 || button > MAX_BUTTON) return false;
        return buttonsPressed[button];
    }

    /** Кнопка была отпущена в этом кадре? */
    public static boolean isButtonReleased(int button) {
        if (button < 0 || button > MAX_BUTTON) return false;
        return buttonsReleased[button];
    }

    // ====== Наведение ======

    /** Наведена ли мышь на прямоугольник (x1,y1) - (x2,y2) ? */
    public static boolean isHoverRect(double x1, double y1, double x2, double y2) {
        int mx = getX();
        int my = getY();

        double left   = Math.min(x1, x2);
        double right  = Math.max(x1, x2);
        double top    = Math.min(y1, y2);
        double bottom = Math.max(y1, y2);

        return mx >= left && mx <= right && my >= top && my <= bottom;
    }

    /** Наведена ли мышь в радиусе radius от точки (cx, cy)? */
    public static boolean isHoverCircle(double cx, double cy, double radius) {
        int mx = getX();
        int my = getY();
        double dx = mx - cx;
        double dy = my - cy;
        return dx * dx + dy * dy <= radius * radius;
    }

    /** Наведена ли мышь на объект с хитбоксом MouseTarget? */
    public static boolean isHover(MouseTarget target) {
        if (target == null) return false;
        double x1 = target.getX();
        double y1 = target.getY();
        double x2 = x1 + target.getWidth();
        double y2 = y1 + target.getHeight();
        return isHoverRect(x1, y1, x2, y2);
    }

    // ====== Клик по области / объекту ======

    /**
     * Кнопка нажата в этом кадре И курсор внутри круга
     * с центром (cx,cy) и радиусом radius?
     */
    public static boolean isButtonPressedInCircle(int button,
                                                  double cx, double cy,
                                                  double radius) {
        return isButtonPressed(button) && isHoverCircle(cx, cy, radius);
    }

    /**
     * Кнопка нажата в этом кадре И курсор внутри хитбокса объекта?
     */
    public static boolean isButtonPressedOn(int button, MouseTarget target) {
        return isButtonPressed(button) && isHover(target);
    }

    /** Аналогично, но для "кнопка сейчас зажата". */
    public static boolean isButtonDownOn(int button, MouseTarget target) {
        return isButtonDown(button) && isHover(target);
    }
}