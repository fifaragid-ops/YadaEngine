package net.wander.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class SimpleGame extends Canvas implements Runnable {

    private JFrame frame;
    private boolean running = false;
    private final String title;
    private final int width;
    private final int height;
    private Thread gameThread;

    // ===== СЦЕНЫ =====
    private final Map<String, Scene> scenes = new HashMap<>();
    private Scene currentScene;

    public SimpleGame(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;
    }

    // --- работа со сценами ---

    /** Зарегистрировать сцену в игре. Первая добавленная сцена может стать текущей. */
    public void addScene(Scene scene) {
        if (scene == null) return;
        scene.setGame(this);
        scenes.put(scene.getName(), scene);

        // если сцена ещё не выбрана, первая добавленная становится активной
        if (currentScene == null) {
            currentScene = scene;
            currentScene.onEnter();
        }
    }

    /** Получить сцену по имени. */
    public Scene getScene(String name) {
        return scenes.get(name);
    }

    /** Текущая активная сцена. */
    public Scene getCurrentScene() {
        return currentScene;
    }

    /** Переход на сцену по имени. */
    public void gotoScene(String name) {
        Scene next = scenes.get(name);
        if (next == null) {
            throw new IllegalArgumentException("Scene not found: " + name);
        }
        gotoScene(next);
    }

    /** Переход на сцену по ссылке. */
    public void gotoScene(Scene next) {
        if (next == null) return;
        if (next == currentScene) return;

        if (currentScene != null) {
            currentScene.onExit();
        }
        currentScene = next;
        currentScene.onEnter();
    }

    // ==========================

    public void start() {
        if (running) return;
        running = true;

        frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        setPreferredSize(new Dimension(width, height));
        frame.add(this);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Input
        setFocusable(true);
        requestFocus();
        addKeyListener(new Input());
        Mouse.attach(this);

        gameThread = new Thread(this, "GameThread");
        gameThread.start();
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        final int fps = 60;
        final double frameTime = 1_000_000_000.0 / fps;

        long last = System.nanoTime();

        init();

        if (currentScene == null) {
            throw new IllegalStateException("Game must have at least one Scene. Call addScene() in init().");
        }

        while (running) {
            long now = System.nanoTime();
            double dt = (now - last) / 1_000_000_000.0;
            last = now;

            Mouse.nextFrame();   // сброс "нажато в этом кадре" перед логикой

            update(dt);
            renderFrame();

            long sleepTime = (long) (frameTime - (System.nanoTime() - now));
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime / 1_000_000L);
                } catch (InterruptedException ignored) {}
            }
        }

        cleanup();
        if (frame != null) frame.dispose();
    }

    private void renderFrame() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(2);
            return;
        }

        Graphics2D g = (Graphics2D) bs.getDrawGraphics();

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);

        render(g);

        g.dispose();
        bs.show();
        Toolkit.getDefaultToolkit().sync();
    }

    // ==== методы, которые ты можешь переопределять ====

    /** Вызывается один раз перед игровым циклом. Здесь добавляешь сцены. */
    protected abstract void init();

    /** Обновление игры. По умолчанию обновляет текущую сцену. */
    protected void update(double dt) {
        if (currentScene != null) {
            currentScene.update(dt);
        }
    }

    /** Отрисовка игры. По умолчанию рисует текущую сцену. */
    protected void render(Graphics2D g) {
        if (currentScene != null) {
            currentScene.render(g);
        }
    }

    protected void cleanup() {}
}