package net.wander;

import net.wander.utils.Input;
import net.wander.utils.SimpleGame;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Tutorial {
    /*
    So, here's how to make a game using this engine. First, you need to create a SimpleGame class object and then describe what the game will do. The rest is up to you: you'll need to write object classes (for example, player classes) and their logic. This is the most basic functionality; the engine can be customized in real time to suit your needs. This isn't the latest version; the engine needs a lot of updates.
    */

    // arguments for simple game are title, width, height.
    public static void main(String[] args) {

         SimpleGame game = new SimpleGame("SimpleGame", 600, 600) {
             double speed;
             double x;
             double y;
            @Override
            protected void init() {
                // Here your init function
                speed = 200;
                x = 50;
                y = 50;
            }

            @Override
            protected void update(double dt) {
                // Here your update function (it will be used every frame)
                // For example:
                if (Input.isKeyDown(KeyEvent.VK_W)){
                    y -= speed * dt;
                }
                if (Input.isKeyDown(KeyEvent.VK_S)) {
                    y += speed * dt;

                }
                if (Input.isKeyDown(KeyEvent.VK_A)) {
                    x -= speed * dt;

                }
                if (Input.isKeyDown(KeyEvent.VK_D)) {
                    x += speed * dt;

                }
            }

            @Override
            protected void render(Graphics2D g) {
                // Here your render function.

                // For example:
                // Setting color of our drawings
                g.setColor(Color.WHITE);
                // And then drawing rect
                g.drawRect(100,100,50,50);
                // or if you want you can draw filled rect
                g.fillRect((int) x, (int) y, 50, 50);
            }
        };
         // And then you need to start your game
        game.start();
    }
}
