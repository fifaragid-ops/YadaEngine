package net.wander.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;

public abstract class SimpleGame extends Canvas implements Runnable {

    private JFrame frame;
    private boolean running = false;
    private final String title;
    private final int width;
    private final int height;
    private Thread gameThread;

    public SimpleGame(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;
    }

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

        // ВАЖНО: включить фокус и навесить слушатель клавы
        setFocusable(true);
        requestFocus();
        addKeyListener(new Input());

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

        init(); // твой метод

        while (running) {
            long now = System.nanoTime();
            double dt = (now - last) / 1_000_000_000.0;
            last = now;

            update(dt);     // твоя логика
            renderFrame();  // отрисовка

            long sleepTime = (long) (frameTime - (System.nanoTime() - now));
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime / 1_000_000L);
                } catch (InterruptedException ignored) {}
            }
        }

        cleanup();
        if (frame != null) {
            frame.dispose();
        }
    }

    private void renderFrame() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            // создаём двойную буферизацию один раз
            createBufferStrategy(2);
            return;
        }

        Graphics2D g = (Graphics2D) bs.getDrawGraphics();

        // очистка фона
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);

        render(g); // твой метод

        g.dispose();
        bs.show();
        Toolkit.getDefaultToolkit().sync();
    }

    protected abstract void init();
    protected abstract void update(double dt);
    protected abstract void render(Graphics2D g);
    protected void cleanup() {}
}